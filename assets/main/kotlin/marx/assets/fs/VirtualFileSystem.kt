package marx.assets.fs

import marx.assets.fs.impl.VirtualFileImpl
import marx.assets.fs.impl.VirtualFileSystemImpl
import org.jetbrains.annotations.NonNls
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi

/**
 * This interface represents the internal file system that is used to manage all assets withing the project.
 */
@ExperimentalPathApi
interface VirtualFileSystem {

    /**
     * This is used for
     */
    val cache: VirtualFileCache

    /**
     * This is the root for the assets.
     */
    val root: VirtualFile

    /**
     * Searches for the file specified by given path. Path is a string which uniquely identifies file within given
     * [VirtualFileSystem]. Format of the path depends on the concrete file system.
     * For `LocalFileSystem` it is an absolute path (both Unix- and Windows-style separator chars are allowed).
     *
     * @param path the path to find file by
     * @return a virtual file if found, `null` otherwise
     */
    fun findFileByPath(@NonNls path: String): VirtualFile?

    /**
     * Fetches presentable URL of file with the given path in this file system.
     *
     * @param path the path to get presentable URL for
     * @return presentable URL
     */
    fun extractPresentableUrl(path: String): String = path.replace('/', File.separatorChar)


    /**
     * Refreshes the cached information for all files in this file system from the physical file system.
     * If `asynchronous` is `false` this method should be only called within write-action.
     *
     * @param asynchronous if `true` then the operation will be performed in a separate thread,
     * otherwise will be performed immediately
     */
    fun refresh(asynchronous: Boolean)


    /**
     * This should return all of the current listeners.
     */
    fun getListeners(): List<VirtualFileListener>

    /**
     * Adds listener to the file system. Normally one should use [VirtualFileCache.VFS_CHANGES] message bus topic.
     *
     * @param listener the listener
     * @see VirtualFileListener
     *
     * @see VirtualFileCache.VFS_CHANGES
     */
    fun addVirtualFileListener(listener: VirtualFileListener)


    /**
     * Removes listener form the file system.
     *
     * @param listener the listener
     */
    fun removeVirtualFileListener(listener: VirtualFileListener)

    /**
     * Implementation of deleting files in this file system
     *
     * @see VirtualFile.delete
     */
    @Throws(IOException::class)
    fun deleteFile(vFile: VirtualFile)

    /**
     * Implementation of moving files in this file system
     *
     * @see VirtualFile.move
     */
    @Throws(IOException::class)
    fun moveFile(vFile: VirtualFile, newParent: VirtualFile)

    /**
     * Implementation of renaming files in this file system
     *
     * @see VirtualFile.rename
     */
    @Throws(IOException::class)
    fun renameFile(vFile: VirtualFile, newName: String)

    /**
     * Implementation of adding files in this file system
     *
     * @see VirtualFile.createChildData
     */
    @Throws(IOException::class)
    fun createChildFile(vDir: VirtualFile, fileName: String): VirtualFile

    /**
     * Implementation of adding directories in this file system
     *
     * @see VirtualFile.createChildDirectory
     */
    @Throws(IOException::class)
    fun createChildDirectory(vDir: VirtualFile, dirName: String): VirtualFile


    /**
     * Implementation of copying files in this file system
     *
     * @see VirtualFile.copy
     */
    @Throws(IOException::class)
    fun copyFile(
        virtualFile: VirtualFile,
        newParent: VirtualFile,
        copyName: String
    ): VirtualFile

    /**
     * Checks if the name is not empty and if the characters are valid
     */
    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.indexOf('\\') < 0 && name.indexOf('/') < 0
    }


    companion object{
        /**
         * This creates a new instance the virtual file system at the given [path]
         */
        fun of(path: Path): VirtualFileSystem{
            return VirtualFileSystemImpl(VirtualFileImpl(path, null))
        }


    }

}