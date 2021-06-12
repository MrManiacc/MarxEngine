package marx.engine

import dorkbox.messageBus.*
import dorkbox.messageBus.annotations.*
import marx.engine.events.*
import marx.engine.events.Events.App.Initialized
import marx.engine.events.Events.App.Update
import marx.engine.input.*
import marx.engine.layer.*
import marx.engine.window.*

/**
 * This is the main entry for the marx engine. It
 */
interface Application : IBus, LayerStack {
    val eventbus: MessageBus
    val window: IWindow
    val input: IInput
    var isRunning: Boolean
    var gameTime: Double
    var startTime: Long
    val currentTime: Long get() = System.currentTimeMillis()

    override fun subscribe(listener: Any) {
        eventbus.subscribe(listener)
    }

    override fun <T : IEvent> publish(event: T) {
        eventbus.publish(event)
    }

    override fun <T : IEvent> publishAsync(event: T) {
        eventbus.publishAsync(event)
    }

    override fun shutdown() {
        eventbus.shutdown()
    }

    @Subscribe
    fun handleEvents(event: Event) {
        val itr = layers.listIterator()
        // `hasPrevious()` returns true if the list has a previous element
        while (itr.hasPrevious()) {
            val layer = itr.previous()
            layer.onEvent(event)
            if (event.isHandled) break
        }
    }

    /**
     * Called upon updating of the game
     */
    fun onUpdate(event: Update) = forEach { it.onUpdate(event) }

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

    companion object {
        private val updateEvent: Events.App.Update = Events.App.Update(0.1, 1.0)
        lateinit var instance: Application

        fun updateOf(gameTime: Double, delta: Double): Events.App.Update {
            updateEvent.gameTime = gameTime
            updateEvent.delta = delta
            return updateEvent
        }
    }


}

