package marx.assets.fs

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.io.path.*

/**
 * This can represent either a folder or file on the filesystem. These files don't necessary need to be loaded onto the
 * class path. They can work with relative and absolute locations
 */
@ExperimentalPathApi
interface VirtualFile {

    /**
     * Checks the validity of the virtual file by checking if the backing path exists
     */
    val isValid: Boolean
        get() = getPath().exists()

    /**
     * This should return the actual name of the file. This should include the extension but not the path
     */
    val name: String
        get() = getPath().name

    /**
     * This attempts to use the [name] without the .extension
     */
    val nameNoExtension: String
        get() = getPath().nameWithoutExtension

    /**
     * This returns the extension fo the virtual file
     */
    val extension: String
        get() = getPath().extension

    /**
     * This is a simple helper because typing getPath() is ugly in kotlin
     */
    val pathString: String get() = getPath().pathString

    /**
     * This should return the parent virtual file
     */
    val parent: VirtualFile?

    /**
     * This reads our attributes per call. It's dynamic so it doesn't need to update
     */
    val attributes: BasicFileAttributes get() = Files.readAttributes(getPath(), BasicFileAttributes::class.java)

    /**
     * Should return true if this is a directory false otherwise
     */
    val isDirectory: Boolean get() = getPath().isDirectory()

    /**
     * Simply check if we're a file
     */
    val isFile: Boolean get() = getPath().isRegularFile(LinkOption.NOFOLLOW_LINKS)

    /**
     * This is used a getter. It should only be updated when [updateUUID] is called
     */
    val uuid: UUID

    /**
     * Used to identify a specific virtual file. This will be set to the [BasicFileAttributes.creationTime()]
     * if this is a file, or if it's a directory we use the path it's self as uuid. For directories,
     * this means when they are moved their uuid should be regenerated
     */
    fun updateUUID()

    /**
     * This is the actual file system path.
     */
    fun getPath(): Path


    /**
     * This should move this virtual file from one location to another.
     */
    fun move(newParent: VirtualFile) = move(Path(newParent.getPath().pathString, this.name))

    /**
     * This should simply update the path
     */
    fun move(newPath: Path)

    /**
     * This will rename the file, not the path.
     */
    fun rename(newName: String)

    /**
     * This copies the virtual file to the specified folder ([newParent]) with the specified name
     */
    fun copy(newParent: VirtualFile, newName: String): VirtualFile

    /**
     * This should generate an new virtual file with the given name. The actual file isn't being created
     * here however, more so just a virtual link to it.
     */
    fun createChildFile(newName: String): VirtualFile

    /**
     * This should generate an new virtual file with the given name. The actual file isn't being created
     * here however, more so just a virtual link to it.
     */
    fun createChildDir(newName: String): VirtualFile


    /**
     * This deletes the file immediately. should return true if successful
     */
    fun delete(): Boolean
}