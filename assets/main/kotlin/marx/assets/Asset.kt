package marx.assets

import marx.assets.fs.VirtualFile
import kotlin.io.path.ExperimentalPathApi

/**
 * This is the main class that will be instantiated for individual assets. It should be able to refresh it's instance
 * on the fly using new [AssetData]. It should be able to dispose of it's self as well, which should be handled by the
 * [AssetType]
 */
interface Asset<DATA : AssetData> {

    /**
     * This represents the actual path to the assets. This can be relative or absolute
     */
    @ExperimentalPathApi
    val file: VirtualFile

    /**@return the name of the current path**/
    @ExperimentalPathApi
    val name: String get() = file.name

    /**
     * This reloads the current instance of the asset with the new [data]. This should only be called at the appropriate
     * time, for example if this asset were and instance of say a texture asset,
     */
    fun load(data: DATA)

    /**
     * This should save the the asset.
     */
    fun save(): DATA


    /**
     * This should cleanup/destroy all of the instance data, for a mesh this would mean that it unloads/deletes
     * all of it's opengl context data like vertex buffer objects, frame buffer objects, etc.
     */
    fun dispose() {}

}