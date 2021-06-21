package marx.assets

/**
 * Represents an asset. They can be reloaded but are generally expected to always be accessible or at the very least able
 * to be loaded at render time. They should be able to be "in memory" meaning they can be created via source code. They should
 * also be able to be created via files automatically. They should be specific loaders (which will be scanned at runtime) that can
 * loaded specific file extension types and map them with their respective resource name. This should all be done via some registry
 * system. Assets that are loaded via file should be accessible via an annotation or easy kotlin-dsl method.
 */
abstract class Asset<T : Asset<T>>(protected val type: AssetType<T>) {

    /**
     * All assets are required to have a name. This *SHOULD* include the extension.
     * For example: -> test.mat
     */
    abstract val name: String

    /**
     * This should be the assets name without the extension. This **Could** be done dynamically,
     * but would be better to be done statically as it would be an extra strain for no reason.
     */
    abstract val displayName: String

    /**
     * This should be the
     */
    abstract val parent: AssetFolder
}
