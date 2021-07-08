package marx.api.extension.impl

import marx.api.annotation.Attrib
import marx.api.extension.ExtensionDescription
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * This allows for reflective extension description propagation
 */
class ExtensionDescriptionImpl<T : Any>(
    /*
     * This refers to the interface the extension is targeting
     */
    override val targetInterface: KClass<T>,
    /**
     * This is the anchor name, i.e it would be test in: <test something="testing"/>. It's the extension point name
     */
    override val targetName: String = targetInterface.simpleName ?: error("Undefined target interface name")
) : ExtensionDescription<T> {

    /**
     * This should be propagated upon creation of an extension point.
     */
    private val mappings: Map<String, String> = propagateMappings()

    /**
     * This should get the value of the
     */
    override fun get(key: String): String? = mappings[key]
    
    /**
     * Returns the keySet of the extensions
     */
    override fun keys(): Set<String> = mappings.keys

    /**
     * Returns the value sof the descriptions
     */
    override fun values(): Collection<String> = mappings.values

    /**
     * This uses reflection to find all of values with the [Attrib] tag
     */
    private fun propagateMappings(): Map<String, String> {
        val map = HashMap<String, String>()
        for (it in targetInterface.members) {
            if (it.hasAnnotation<Attrib>()) {
                val attrib = it.findAnnotation<Attrib>() ?: continue

            }
        }


        return map
    }
}
