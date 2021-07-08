package marx.assets.fs

import java.nio.file.Path
import java.util.*
import kotlin.io.path.ExperimentalPathApi

/**
 * This interface is used to present a way of caching the internal file system structure. This manages all of the
 * internals of the virtual files. It should implement some way of retrieving virtual files via a path,
 * this means that the virtual file cache must stay up to date with virtual files because their paths can
 * change quite frequently.
 */
interface VirtualFileCache : VirtualFileListener {
    /**
     * This will refresh the cache of virtual files
     */
    @ExperimentalPathApi
    fun refresh(virtualFileSystem: VirtualFileSystem)

    /**
     * This should get the virtual file at the given path. If it doesn't exist, it should walk up the path tree
     * until it finds one that does, the create a new instance
     */
    @ExperimentalPathApi
    fun getVirtualFile(virtualFileSystem: VirtualFileSystem, path: Path): VirtualFile?

}