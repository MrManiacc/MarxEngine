package marx.assets.registry

import marx.assets.*

/**
 * This is a registry. It should allow for manipulation and control over a give asset type. They should be scanned via googles
 * reflection and should be kotlin objects. We should be able to statically reference given asset methods. This should act as
 * a template
 * <T> is the asset type
 */
interface IAssetRegistry<T : Asset<T>> {
    /**
     * This should return true if the passes asset is registered.
     * TODO: look into urn registration checking
     */
    fun isRegistered(asset: T): Boolean

    /**
     * @return [RegistryResult] of the [asset] successfully registered with respects to [override] flag
     */
    fun register(
        asset: T,
        override: Boolean = true
    ): RegistryResult




}