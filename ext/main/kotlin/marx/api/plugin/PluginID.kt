package marx.api.plugin

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.jetbrains.annotations.NonNls

/**
 * This is used for identifying a given module. It should be registered the  id of main inside the mod.toml file.
 */
class PluginID private constructor(
    @NonNls val nameId: String
) {
    operator fun compareTo(o: PluginID): Int = nameId.compareTo(o.nameId)

    override fun toString(): String = nameId
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PluginID

        if (nameId != other.nameId) return false

        return true
    }

    override fun hashCode(): Int {
        return nameId.hashCode()
    }

    /**
     * This allows us to cache the plugin ids
     */
    companion object {
        private val registeredIds: MutableMap<String, PluginID> = Object2ObjectOpenHashMap()

        /**
         * This is used to cache the plugin id instances
         */
        @Synchronized
        operator fun get(nameId: String): PluginID =
            registeredIds.computeIfAbsent(nameId) { PluginID(it) }

        /**
         * This will find the first available [PluginID] based upon the [nameIds]
         */
        @Synchronized
        fun find(vararg nameIds: String): PluginID? {
            for (id in nameIds)
                return registeredIds[id] ?: continue
            return null
        }

        /**
         * This create a list of all of the valid plugin id values.
         */
        @Synchronized
        fun list(): Collection<PluginID> = ArrayList(registeredIds.values)


    }

}