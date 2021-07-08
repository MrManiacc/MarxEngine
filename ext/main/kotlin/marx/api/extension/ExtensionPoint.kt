package marx.api.extension

import marx.api.plugin.PluginDescriptor
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * This is the core of basically everything. This provides bite-sized bits of code that can
 * be override using configuration files. Soon there will be an intellij plugin for auto completion
 */
interface ExtensionPoint<T : Extension> {
    /**
     * We need the actual extension interface for some mappings.
     */
    val extensionInterface: KClass<T>

    /**
     * This should be created using reflection upon the annotations inside the extension point.
     */
    val extensionDescription: ExtensionDescription<T>

    /**
     * This describes/contains all of the information pertaining to the extension's plugin/parent
     */
    val pluginDescriptor: PluginDescriptor


    /**
     * should return the list of extensions registered via [registerExtension].
     * They shu
     */
    fun getExtensions(): List<T>

    /**
     * This will register the extension.
     */
    fun registerExtension(extension: T)

    fun extensions(): Stream<T>

    fun size(): Int


}