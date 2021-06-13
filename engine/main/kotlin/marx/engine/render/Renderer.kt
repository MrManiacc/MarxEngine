package marx.engine.render

import com.google.common.collect.*
import mu.*
import java.lang.IllegalStateException
import kotlin.reflect.*

/**
 * This is hte core of the
 */
object Renderer {
    val renderers: MutableMap<KClass<out RenderAPI>, RenderAPI> = Maps.newHashMap()
    val log = KotlinLogging.logger { }

    /**
     * sets/registers [API] to the corresponding [context]
     */
    operator fun set(API: KClass<out RenderAPI>, context: RenderAPI) = context.let {
        renderers[API] = it
        log.info { "registered rendered: ${API::class.qualifiedName}" }
    }

    /**
     * This will register the give api
     */
    inline fun <reified API : RenderAPI> register(renderer: API) =
        set(API::class, renderer)

    /**
     * [API] the render api to get the render context for
     * This will get the corresponding [RenderAPI] per the given type
     */
    operator fun <API : RenderAPI> invoke(cls: KClass<API>): API {
        if (!renderers.containsKey(cls)) return casted(cls, RenderAPI.Null)
        val api = renderers[cls] ?: return casted(cls, RenderAPI.Null)
        if (!cls.isInstance(api)) return casted(cls, RenderAPI.Null)
        return casted(cls, api)
    }

    fun <API : RenderAPI> casted(cls: KClass<API>, renderAPI: RenderAPI): API =
        cls.safeCast(renderAPI) ?: throw IllegalStateException("Failed to casted render api from${renderAPI::class.qualifiedName} to ${cls.qualifiedName}")

    inline fun <reified API : RenderAPI> casted(renderAPI: RenderAPI): API = casted(API::class, renderAPI)

    /**
     * [API] the render api to get the render context for
     * This will get the corresponding [RenderAPI] per the given type
     */
    inline operator fun <reified API : RenderAPI> invoke(): API =
        invoke(API::class)

    /**
     * This is the core of all of the rendering done throughout the engine.
     * It's purpose is to provide a platform agnostic rendering subset of tools.
     */
    abstract class RenderAPI {
        init {
            this.register()
        }

        /**
         * This will register the api with the [Renderer]
         */
        open fun register() =
            set(this::class, this)

        /**
         * Initialize the given graphics context
         */
        open fun init() {}

        /**
         * Swap the given buffers of the graphics context
         */
        open fun swap() {}

        /**
         * Poll the input for the graphics context
         */
        open fun poll() {}

        /**
         * Allows for viewport resizing
         */
        open fun viewport(size: Pair<Int, Int>, pos: Pair<Int, Int> = 0 to 0) {}

        /**
         * Clear the screen with the given color
         */
        abstract fun clear(color: FloatArray? = null, clearFlags: ClearFlags = ClearFlags.COLOR)
        /**Called upon unloading of the given renderAPI **/
        open fun dispose() {}

        /**
         * This clear flags for clearing the screen
         */
        enum class ClearFlags {
            COLOR, DEPTH, COLOR_DEPTH
        }

        companion object {
            /**Provides and a "null-safe" api. This will be the defaulted to renderApi **/
            val Null: RenderAPI = object : RenderAPI() {
                /**
                 * Initialize the given graphics context
                 */
                override fun register() = Unit

                /**
                 * Initialize the given graphics context
                 */
                override fun init() = Unit

                /**
                 * Swap the given buffers of the graphics context
                 */
                override fun swap() = Unit

                /**
                 * Poll the input for the graphics context
                 */
                override fun poll() = Unit

                /**
                 * Allows for viewport resizing
                 */
                override fun viewport(size: Pair<Int, Int>, pos: Pair<Int, Int>) = Unit

                /**
                 * Clear the screen with the given color
                 */
                override fun clear(color: FloatArray?, clearFlags: ClearFlags) = Unit
            }
        }

    }
}
