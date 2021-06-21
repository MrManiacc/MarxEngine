package marx.assets

/**
 * This allow for a direct implementation of folders as assets. They can store child assets.
 */
class AssetFolder(name: String) : Asset<AssetFolder>(folderType) {

    /**
     * All assets are required to have a name. This *SHOULD* include the extension.
     * For example: -> test.mat
     */
    override val name: String

    /**
     * This should be the assets name without the extension. This **Could** be done dynamically,
     * but would be better to be done statically as it would be an extra strain for no reason.
     */
    override val displayName: String

    /**
     * This should be the
     */
    override val parent: AssetFolder

    /**
     * This should only be true of the root folder
     */
    var isRoot: Boolean = false
        private set

    init {
        if (!name.contains("/")) {
            this.parent = ROOT
            this.name = name
        } else {
            this.parent = AssetFolder(name.substringBeforeLast("/"))
            this.name = name.substringAfterLast("/")
        }
        this.displayName = this.name.substringBefore(".", this.name)
    }

    /**
     * This will get the actual urn of the assets folder by recursively walking up
     */
    operator fun invoke(nameIn: String? = null): String {
        if (isRoot) return "$displayName/$nameIn"
        return parent(this.displayName)
    }

    /**
     * This will simply get the current asset folder
     */
    override fun toString(): String = "AssetFolder(${this()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssetFolder) return false

        if (name != other.name) return false
        if (displayName != other.displayName) return false
        if (parent != other.parent) return false
        if (isRoot != other.isRoot) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + isRoot.hashCode()
        return result
    }

    /**
     * This should store the static reference to the given type.
     */
    companion object {
        val ROOT = AssetFolder("assets").apply { isRoot = true }
        private val folderType: AssetType<AssetFolder> = AssetType()
    }
}