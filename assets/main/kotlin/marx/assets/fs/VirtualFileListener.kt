package marx.assets.fs

import kotlin.io.path.ExperimentalPathApi

/**
 * This is used to notify listeners of virtual file events such as being added, removed, updated etc.
 */
interface VirtualFileListener {


    /**
     * This is called before the cache has been refreshed
     */
    fun preCacheRefresh(cache: VirtualFileCache) = Unit

    /**
     * This is called BEFORE the moving of a [VirtualFile], if it returns false the moving wont take place.
     * @return true if moved without a problem, false if there was a problem while moving it.
     */
    @ExperimentalPathApi
    fun preFileMove(movingFile: VirtualFile, newParent: VirtualFile): Boolean = true

    /**
     * This is called BEFORE the deleting of a [VirtualFile], return false to cancel
     */
    @ExperimentalPathApi
    fun preFileDelete(deletingFile: VirtualFile): Boolean = true

    /**
     * This is called before the file is named. return false to cancel
     */
    @ExperimentalPathApi
    fun preFileRename(renamingFile: VirtualFile, newName: String): Boolean = true

    /**
     * This is called before the file is copied. return false to cancel
     */
    @ExperimentalPathApi
    fun preFileCopy(copyingFile: VirtualFile, newParent: VirtualFile, newName: String): Boolean = true

    /**
     * This is called before a child file is created. return false to cancel
     */
    @ExperimentalPathApi
    fun preCreateChildFile(forDir: VirtualFile, newFileName: String): Boolean = true


    /**
     * This is called before a child file is created. return false to cancel
     */
    @ExperimentalPathApi
    fun preCreateChildDir(forDir: VirtualFile, newDirName: String): Boolean = true

    /**
     * This is called after the cache has been refreshed
     */
    fun postCacheRefresh(cache: VirtualFileCache) = Unit

    /**
     * This is called AFTER the moving of a [VirtualFile], if it returns false the moving wont take place.
     * @return true if moved without a problem, false if there was a problem while moving it.
     */
    @ExperimentalPathApi
    fun postFileMove(movedFile: VirtualFile, newParent: VirtualFile) = Unit

    /**
     * This is called AFTER the deleting of a [VirtualFile], return false to cancel
     */
    @ExperimentalPathApi
    fun postFileDelete(deletedFile: VirtualFile) = Unit

    /**
     * This is called after the file is named. return false to cancel
     */
    @ExperimentalPathApi
    fun postFileRename(renamedFile: VirtualFile, newName: String) = Unit

    /**
     * Called after a virtual file has been copied, the [originalFile] is the vfile we copied from, while
     * the [newFile] is newly created (via a copy) virtual file
     */
    @ExperimentalPathApi
    fun postFileCopy(originalFile: VirtualFile, newFile: VirtualFile) = Unit

    /**
     * This is called after a child file is created. return false to cancel
     */
    @ExperimentalPathApi
    fun postCreateChildFile(newFile: VirtualFile) = Unit


    /**
     * This is called after a child file is created. return false to cancel
     */
    @ExperimentalPathApi
    fun postCreateChildDir(newDir: VirtualFile) = Unit

}