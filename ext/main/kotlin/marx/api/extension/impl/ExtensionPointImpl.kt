package marx.api.extension.impl

import marx.api.plugin.PluginDescriptor
import marx.api.extension.Extension
import marx.api.extension.ExtensionDescription
import marx.api.extension.ExtensionPoint
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * This stores all of the extensions.
 */
class ExtensionPointImpl<T : Extension>(override val extensionInterface: KClass<T>) :
    ExtensionPoint<T> {
    private val extensions: MutableList<T> = ArrayList()

    /**
     * This describes/contains all of the information pertaining to the extension's plugin/parent
     */
    override val pluginDescriptor: PluginDescriptor
        get() = TODO("Not yet implemented")

    /**
     * This should be created using reflection upon the annotations inside the extension point.
     */
    override val extensionDescription: ExtensionDescription<T>
        get() = TODO("Not yet implemented")

    /**
     * should return the list of extensions registered via [registerExtension].
     * They shu
     */
    override fun getExtensions(): List<T> = extensions

    /**
     * This will register the extension.
     */
    override fun registerExtension(extension: T) {
        extensions.add(extension)
    }

    override fun extensions(): Stream<T> = extensions.stream()

    override fun size(): Int = extensions.size


}