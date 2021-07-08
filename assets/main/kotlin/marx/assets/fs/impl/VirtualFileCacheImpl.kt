package marx.assets.fs.impl

import marx.assets.fs.VirtualFile
import marx.assets.fs.VirtualFileCache
import marx.assets.fs.VirtualFileSystem
import mu.KotlinLogging
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.*

class VirtualFileCacheImpl : VirtualFileCache {
    @ExperimentalPathApi
    private val cachedFiles: MutableMap<Path, VirtualFile> = HashMap()
    private val log: Logger = KotlinLogging.logger { }

    /**
     * This will refresh the cache of virtual files
     */
    @ExperimentalPathApi
    override fun refresh(virtualFileSystem: VirtualFileSystem) {
        log.debug("Refreshing the virtual file system cache.")
        cachedFiles.clear()
        cache(virtualFileSystem.root) //This *should* recursively find all of the virtual files.
    }

    @ExperimentalPathApi
    private fun cache(vFile: VirtualFile): VirtualFile {
        cachedFiles[vFile.getPath()] = vFile
        log.debug("Cached virtual file: $vFile")
        if (vFile.isFile) return vFile
        if (vFile.isDirectory) {
            vFile.getPath().forEach {
                if (!cachedFiles.containsKey(it))
                    cache(VirtualFileImpl(it, vFile))
            }
        }
        return vFile
    }

    /**
     * This should get the virtual file at the given path. If it doesn't exist, it should walk up the path tree
     * until it finds one that does, the create a new instance. The only time it will ever actually
     * return null is when the file/folder specified at the given path really doesn't exist
     */
    @ExperimentalPathApi
    override fun getVirtualFile(virtualFileSystem: VirtualFileSystem, path: Path): VirtualFile? {
        if (cachedFiles.containsKey(path)) return cache(cachedFiles[path]!!)
        if (!path.exists()) {
            log.warn("Attempted to access non-cached and non-existent file/folder: ${path.pathString}")
            return null
        }
        return cache(getOrCreate(virtualFileSystem, path))
    }


    @ExperimentalPathApi
    private fun getOrCreate(virtualFileSystem: VirtualFileSystem, path: Path): VirtualFile {
        if (cachedFiles.containsKey(path)) return cachedFiles[path]!!
        if (cachedFiles.containsKey((path.parent))) {
            val parent = cachedFiles[path.parent]
            return VirtualFileImpl(path, parent)
        }
        if (path.parent == virtualFileSystem.root.getPath())
            return VirtualFileImpl(path, virtualFileSystem.root)
        return VirtualFileImpl(path, getOrCreate(virtualFileSystem, path.parent))
    }

    @ExperimentalPathApi
    override fun toString(): String {
        return "VirtualFileCache(cachedFiles=$cachedFiles)"
    }

}