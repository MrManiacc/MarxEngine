package marx.editor.ui

import kotlin.collections.HashMap
import kotlin.reflect.KClass

/**
 * This stores the current context for rendering.
 */
class UIContext {
    private val properties: MutableMap<String, Any> = HashMap()

    /**
     * This pushes our value using the key. The value can be of any type
     */
    fun push(key: String, value: Any) {
        properties[key] = value
    }

    /**
     * This attempts to pop the given value with the key
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> pop(key: String, type: KClass<T>): T? {
        if (properties.containsKey(key)) {
            val value = properties.remove(key)
            if (type.isInstance(value))
                return value as T?
        }
        return null
    }

    /**
     * This attempts to pop the given value with the key
     */
    inline fun <reified T : Any> pop(key: String): T? = pop(key, T::class)

    /**
     * This attempts to pop the given value with the key
     */
    inline fun <reified T : Any> pop(key: String, default: () -> T): T = pop(key, T::class) ?: default()


}