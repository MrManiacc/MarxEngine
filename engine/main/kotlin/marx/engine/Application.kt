package marx.engine

import dorkbox.messageBus.*
import dorkbox.messageBus.annotations.*
import marx.engine.events.*
import marx.engine.events.Events.App.Initialized
import marx.engine.events.Events.App.Update
import marx.engine.events.Events.Window
import marx.engine.events.Events.Window.Resize
import marx.engine.input.*
import marx.engine.layer.*
import marx.engine.render.*
import marx.engine.window.*
import kotlin.reflect.*

/**
 * This is the main entry for the marx engine. It
 */
interface Application<API : Renderer.RenderAPI> : IBus, LayerStack {
    val eventbus: MessageBus
    val window: IWindow
    val input: IInput
    var isRunning: Boolean
    var gameTime: Double
    var startTime: Long
    val currentTime: Long get() = System.currentTimeMillis()

    /**This will get the render api for the specified [rendererType]**/
    val renderAPI: API

    override fun subscribe(listener: Any) = eventbus.subscribe(listener)

    override fun <T : IEvent> publish(event: T) {
        if (event is Event)
            for (layerId in size - 1 downTo 0) {
                val layer = layers[layerId]
                layer.onEvent(event)
                if (event.isHandled) return
            }
        eventbus.publish(event)
    }

    override fun <T : IEvent> publishAsync(event: T) {
        if (event is Event)
            for (layerId in size - 1 downTo 0) {
                val layer = layers[layerId]
                layer.onEvent(event)
                if (event.isHandled) return
            }
        eventbus.publishAsync(event)
    }

    /**
     * Called upon updating of the game
     */
    fun onUpdate(event: Update) {
        for (layerId in size - 1 downTo 0) {
            val layer = layers[layerId]
            layer.onUpdate(event)
        }

        renderAPI.swap()
        renderAPI.poll()
    }

    @Subscribe
    fun onResize(event: Resize) {
        renderAPI.viewport(event.width to event.height)
    }

    /**
     * This is called upon the start of the application
     */
    fun start() {
        instance = this
        subscribe(input)
        instance = this
        isRunning = true
        publish(Initialized(this))
        startTime = currentTime
        renderAPI.init()
        while (isRunning && !window.shouldClose) {
            val now = currentTime
            val delta = (now - startTime) / 1000.0
            gameTime += delta
            startTime = now
            val event = updateOf(gameTime, delta / 1000.0)
            onUpdate(event) //Called before the global event
            publish(event)
        }
        destroy()
    }

    fun destroy() {
        publish(Events.App.Shutdown(this))
        shutdown()
        isRunning = false
    }

    @Subscribe
    fun destroy(event: Window.Destroy) {
        renderAPI.dispose()
    }

    override fun shutdown() {
        eventbus.shutdown()
    }

    companion object {
        private val updateEvent: Events.App.Update = Events.App.Update(0.1, 1.0)
        lateinit var instance: Application<*>

        fun updateOf(gameTime: Double, delta: Double): Events.App.Update {
            updateEvent.gameTime = gameTime
            updateEvent.deltaTime = delta
            return updateEvent
        }
    }


}

