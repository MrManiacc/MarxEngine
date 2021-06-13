package marx.engine.layer

import dorkbox.messageBus.annotations.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Update
import marx.engine.render.*
import marx.engine.render.scene.*
import kotlin.reflect.*

/**
 * A layer is like a chunk of methods and variables that will be rendered to screen. They are boxed sections that render
 * specific things
 */
abstract class Layer<API : RenderAPI>(val app: Application<*>, private val rendererType: KClass<API>, val name: String = "layer") {
    val renderAPI: API get() = Renderer(rendererType)
    val scene: RenderScene get() = renderAPI.scene
    open fun onAttach() = Unit
    open fun onDetach() = Unit
    abstract fun onUpdate(update: Update)
    abstract fun onEvent(event: Event)

}