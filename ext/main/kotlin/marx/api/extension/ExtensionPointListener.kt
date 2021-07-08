package marx.api.extension

import marx.api.plugin.PluginDescriptor

/**
 * This is used to notify a listener of extension events
 */
interface ExtensionPointListener<T : Extension> {
    fun extensionAdded(extension: T, pluginDescriptor: PluginDescriptor)
    fun extensionRemoved(extension: T, pluginDescriptor: PluginDescriptor)
}