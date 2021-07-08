package marx.api.extension

import kotlin.reflect.KClass

/**
 * This is used to store all of the key/values of things
 */
interface ExtensionDescription<T : Any> {
    /**
     * This refers to the interface the extension is targeting
     */
    val targetInterface: KClass<T>

    /**
     * This is the anchor name, i.e it would be test in: <test something="testing"/>. It's the extension point name
     */
    val targetName: String

    /**
     * This allows us to check if a key is present
     */
    fun contains(key: String) = get(key) != null

    /**
     * This should get the value of the
     */
    fun get(key: String): String?

    /**
     * Returns the keyset of the extensions
     */
    fun keys(): Set<String>

    /**
     * Returns the value sof the descriptions
     */
    fun values(): Collection<String>

}