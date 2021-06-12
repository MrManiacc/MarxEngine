package marx.sandbox

import com.google.common.collect.*
import dorkbox.messageBus.*
import dorkbox.messageBus.annotations.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.Window.Initialized
import marx.engine.layer.*
import marx.engine.window.*
import marx.sandbox.layer.*
import marx.window.*
import mu.*
import org.lwjgl.opengl.GL11.*
import org.slf4j.*

/**
 * This is the front on the engine. Its the sandbox/the application used for testing the engine/libraries.
 */
object Sandbox : Application {
    override val eventbus: MessageBus = MessageBus(4)
    override val window: IWindow = GlfwWindow(title = "Sandbow, glfw", app = this)
    override var isRunning: Boolean = false
    override var gameTime: Double = 0.0
    override var startTime: Long = System.currentTimeMillis()
    override val layers: MutableList<Layer> = Lists.newArrayList()
    override var insertIndex: Int = 0
    private val log: Logger = KotlinLogging.logger { }

    /**
     * We must subscribe anything important here. In the future any entity systems
     * would be subscribed here
     */
    init {
        subscribe(window)
        subscribe(this)
    }

    /**
     * This is used to initialized our layers
     */
    @Subscribe fun onInitialized(event: Initialized) {
        pushLayer(LayerDebug())
        log.debug("Initialized sandbox window: ${event.window} and added all layers.")
    }

    /**
     * This is called every frame and updates the children layers.
     * It also polls the window and swaps the buffers
     */
    override fun onUpdate(event: Events.App.Update) {
        glClearColor(1f, 0f, 1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
        super.onUpdate(event)
        window.swapBuffers()
        window.pollInput()
    }


}

