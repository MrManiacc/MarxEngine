package marx.sandbox

import dorkbox.messageBus.*
import dorkbox.messageBus.annotations.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.window.*
import marx.window.*

/**
 * This is the front on the engine. Its the sandbox/the application used for testing the engine/libraries.
 */
object Sandbox : Application {
    override val eventbus: MessageBus = MessageBus(4)
    override val window: IWindow = GlfwWindow(title = "Sandbow, glfw", app = this)
    override var isRunning: Boolean = false
    override var gameTime: Double = 0.0
    override var startTime: Long = System.currentTimeMillis()

    init {
        subscribe(window)
        subscribe(this)
    }

    @Subscribe
    fun onWindowInit(event: Events.WindowCreatedEvent) {
        println("Window initialized: $event")
    }

    @Subscribe
    fun onDestroy(event: Events.AppCreatedEvent) {
        println("DESTROY")
    }

    @Subscribe
    fun onResize(event: Events.WindowResizedEvent) {
        println("RESIZE: $event")
    }


}

