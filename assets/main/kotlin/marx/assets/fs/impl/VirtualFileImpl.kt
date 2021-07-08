package marx.assets.fs.impl

import marx.assets.fs.VirtualFile
import marx.assets.fs.VirtualFileSystem
import mu.KotlinLogging
import org.slf4j.Logger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.pathString

/**
 * This parent should be able to be retrieved at runtime.
 */
@ExperimentalPathApi
class VirtualFileImpl(
    private var path: Path,
    /**
     * This should return the parent virtual file
     */
    override val parent: VirtualFile?,
) : VirtualFile {

    private val log: Logger = KotlinLogging.logger { }

    /**
     * This is our actual [UUID]. It should be initially random
     */
    private var _uuid: UUID = UUID.randomUUID()

    /**
     * This is used a getter. It should only be updated when [updateUUID] is called
     */
    override val uuid: UUID
        get() = _uuid

    /**
     * Used to identify a specific virtual file. This will be set to the [BasicFileAttributes.creationTime()]
     * if this is a file, or if it's a directory we use the path it's self as uuid. For directories,
     * this means when they are moved their uuid should be regenerated
     */
    override fun updateUUID() {
        val creationTime = this.attributes.creationTime()
        val time = creationTime.toMillis()
        val bytes = longToBytes(time)
        log.debug("Updating uuid for virtual file: $this, found time file creation timestamp: $time, convert to byte array: ${bytes.joinToString { ", " }}")
        _uuid = UUID.nameUUIDFromBytes(bytes)
        log.info("Set uuid for virtual file: ${this.pathString}, to: $uuid")
    }

    private fun longToBytes(x: Long): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(java.lang.Long.BYTES)
        buffer.putLong(x)
        return buffer.array()
    }

    /**
     * This is the actual file system path.
     */
    override fun getPath(): Path = path


    /**
     * This should simply update the path
     */
    override fun move(newPath: Path) {
        Files.move(getPath(), newPath)
        this.path = newPath
        updateUUID()
    }

    /**
     * This will rename the file, not the path.
     */
    override fun rename(newName: String) =
        move(Path(getPath().parent.pathString, newName)).also { updateUUID() }

    /**
     * This copies the virtual file to the specified folder ([newParent]) with the specified name
     */
    override fun copy(newParent: VirtualFile, newName: String): VirtualFile {
        val newPath = Path(newParent.getPath().pathString, newName)
        Files.copy(getPath(), newPath, StandardCopyOption.REPLACE_EXISTING)
        return VirtualFileImpl(newPath, newParent)
    }

    /**
     * This should generate an new virtual file with the given name. The actual file isn't being created
     * here however, more so just a virtual link to it.
     */
    override fun createChildFile(newName: String): VirtualFile {
        val newPath = Path(getPath().pathString, newName)
        Files.createFile(newPath)
        return VirtualFileImpl(newPath, this)
    }


    /**
     * This should generate an new virtual file with the given name. The actual file isn't being created
     * here however, more so just a virtual link to it.
     */
    override fun createChildDir(newName: String): VirtualFile {
        val newPath = Path(getPath().pathString, newName)
        Files.createDirectory(newPath)
        return VirtualFileImpl(newPath, this)
    }

    /**
     * This deletes the file immediately.
     */
    override fun delete(): Boolean {
        return Files.deleteIfExists(getPath())
    }

    override fun toString(): String =
        "VirtualFile(name=$nameNoExtension, uuid=$uuid, type=${if (isDirectory) "directory" else "file"}, path=$pathString)"

    /**
     * This checks the uuid's against one another. It doesn't require that the other virtual file be an instance of this
     * class, just that it's an instance of a virtual file
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VirtualFile) return false
        if (uuid != other.uuid) return false
        return true
    }

    override fun hashCode(): Int = uuid.hashCode()


    /**
     * We need to initially update the uuid upon creation
     */
    init {
        updateUUID()
    }


}