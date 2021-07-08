package marx.assets

/**
 * This is used throughout the entire engine. It's purpose is to provide a clean easy to use defined type
 * for each individual asset. It should store the type of asset data it contains, it should provide a asset matcher,
 * so that it can be be checked against a [VirtualFile] and check if it's an instance of this. It should also
 * provide an asset factory for creating instance of the asset. Asset types are used a key for bascially everything related
 * to an asset.
 */
interface AssetType<A : AssetData> {



}