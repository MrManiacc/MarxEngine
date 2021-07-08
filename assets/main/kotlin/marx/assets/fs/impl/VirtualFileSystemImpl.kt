package marx.assets.fs.impl

import marx.assets.fs.VirtualFile
import marx.assets.fs.VirtualFileListener
import marx.assets.fs.VirtualFileCache
import marx.assets.fs.VirtualFileSystem
import mu.KotlinLogging
import org.slf4j.Logger
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class VirtualFileSystemImpl(
    /**
     * This is required. It's the root location of this virtual file system.
     */
    override val root: VirtualFile,
    /**
     * Used for caching virtual files/managing the file system
     */
    override val cache: VirtualFileCache = VirtualFileCacheImpl()
) : VirtualFileSystem {
    private val listeners: MutableList<VirtualFileListener> = ArrayList()
    private val log: Logger = KotlinLogging.logger { }

    /**
     * Refreshes the cached information for all files in this file system from the physical file system.
     * If `asynchronous` is `false` this method should be only called within write-action.
     *
     * @param asynchronous if `true` then the operation will be performed in a separate thread,
     * otherwise will be performed immediately
     */
    override fun refresh(asynchronous: Boolean) {
        for (listener in listeners)
            listener.preCacheRefresh(cache)
        cache.refresh(this)
        for (listener in listeners)
            listener.postCacheRefresh(cache)
    }


    /**
     * Searches for the file specified by given path. Path is a string which uniquely identifies file within given
     * [VirtualFileSystem]. Format of the path depends on the concrete file system.
     * For `LocalFileSystem` it is an absolute path (both Unix- and Windows-style separator chars are allowed).
     *
     * @param path the path to find file by
     * @return a virtual file if found, `null` otherwise
     */
    override fun findFileByPath(path: String): VirtualFile? {
        //TODO search the cache first then if not found search the actual file path, if found create th virtual file.
        //TODO this should be very verbose in that it should be fast and able to search for file names and folder names
        TODO("Need to search the tree and use the manager to ")

    }

    /**
     * This should return all of the current listeners.
     */
    override fun getListeners(): List<VirtualFileListener> = listeners

    /**
     * Adds listener to the file system. Normally one should use [VirtualFileCache.VFS_CHANGES] message bus topic.
     *
     * @param listener the listener
     * @see VirtualFileListener
     *
     * @see VirtualFileCache.VFS_CHANGES
     */
    override fun addVirtualFileListener(listener: VirtualFileListener) {
        this.listeners.add(listener)
        log.debug("Added virtual file listener: ${listener.javaClass.simpleName}")
    }

    /**
     * Removes listener form the file system.
     *
     * @param listener the listener
     */
    override fun removeVirtualFileListener(listener: VirtualFileListener) {
        this.listeners.remove(listener)
        log.debug("Removed virtual file listener: ${listener.javaClass.simpleName}")
    }

    /**
     * Implementation of deleting files in this file system
     *
     * @see VirtualFile.delete
     */
    override fun deleteFile(vFile: VirtualFile) {
        for (listener in listeners)
            if (!listener.preFileDelete(vFile))
                return
        vFile.delete()
        log.debug("Deleted virtual file: $vFile")
        for (listener in listeners)
            listener.postFileDelete(vFile)
    }

    /**
     * Implementation of moving files in this file system
     *
     * @see VirtualFile.move
     */
    override fun moveFile(vFile: VirtualFile, newParent: VirtualFile) {
        for (listener in listeners)
            if (!listener.preFileMove(newParent, vFile))
                return
        log.debug("Moved virtual file from: ${vFile.parent} to: $newParent")
        vFile.move(newParent)
        for (listener in listeners)
            listener.postFileMove(newParent, vFile)
    }

    /**
     * Implementation of renaming files in this file system
     *
     * @see VirtualFile.rename
     */
    override fun renameFile(vFile: VirtualFile, newName: String) {
        for (listener in listeners)
            if (!listener.preFileRename(vFile, newName))
                return
        log.debug("Renamed virtual file from: ${vFile.name} to: $newName")
        vFile.rename(newName)
        for (listener in listeners)
            listener.postFileRename(vFile, newName)
    }

    /**
     * Implementation of adding files in this file system
     *
     * @see VirtualFile.createChildData
     */
    override fun createChildFile(vDir: VirtualFile, fileName: String): VirtualFile {
        for (listener in listeners)
            if (!listener.preCreateChildFile(vDir, fileName)) {
                log.warn("Creation of child file named $fileName prevented by listener: ${listener.javaClass.simpleName}, returning original dir: $vDir")
                return vDir
            }
        val vFile = vDir.createChildFile(fileName)
        log.debug("Created virtual file child named '$fileName' in virtual direction: $vDir")
        for (listener in listeners)
            listener.postCreateChildFile(vFile)
        return vFile
    }


    /**
     * Implementation of adding directories in this file system
     *
     * @see VirtualFile.createChildDirectory
     */
    override fun createChildDirectory(vDir: VirtualFile, dirName: String): VirtualFile {
        for (listener in listeners)
            if (!listener.preCreateChildDir(vDir, dirName)) {
                log.warn("Creation of child directory named $dirName prevented by listener: ${listener.javaClass.simpleName}, returning original dir: $vDir")
                return vDir
            }
        val newDir = vDir.createChildDir(dirName)
        log.debug("Created virtual directory child named '$dirName' in virtual direction: $newDir")
        for (listener in listeners)
            listener.postCreateChildDir(newDir)
        return newDir
    }

    /**
     * Implementation of copying files in this file system
     *
     * @see VirtualFile.copy
     */
    override fun copyFile(virtualFile: VirtualFile, newParent: VirtualFile, copyName: String): VirtualFile {
        for (listener in listeners)
            if (!listener.preFileCopy(virtualFile, newParent, copyName)) {
                log.warn("Failed to copy virtual file: $virtualFile named $copyName in $newParent. Returning original virtualFile")
                return virtualFile
            }
        val copy = virtualFile.copy(newParent, copyName)
        for (listener in listeners)
            listener.postFileCopy(virtualFile, copy)
        return copy
    }
}