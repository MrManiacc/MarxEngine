package marx.api.plugin

import java.nio.file.Path

/**
 * This defines everything related to a plugin.
 */
interface PluginDescriptor {
    /**
     * This is the actual id of the given plugin. It's used as a unique key.
     */
    val pluginID: PluginID

    /**
     * This is used to load the plugin classes. They can be elevated to higher status but are not usually needed.
     */
    val classLoader: ClassLoader

    /**
     * The actual path of the plugin. This should be absolute
     */
    val pluginPath: Path
}