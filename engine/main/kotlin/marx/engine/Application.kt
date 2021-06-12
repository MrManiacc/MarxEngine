package marx.engine

import dorkbox.messageBus.*
import marx.engine.events.*
import marx.engine.window.*

/**
 * This is the main entry for the marx engine. It
 */
interface Application : IBus {
    val eventbus: MessageBus
    val window: IWindow
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

    fun create() {
        isRunning = true
        publish(Events.AppCreatedEvent(this))
        startTime = currentTime
        while (isRunning && !window.shouldClose) {
            val now = currentTime
            val delta = (now - startTime) / 1000.0
            gameTime += delta
            startTime = now
            publish(updateOf(gameTime, delta / 1000.0))
        }
        destroy()
    }

    fun destroy() {
        publish(Events.AppShutdownEvent(this))
        shutdown()
        isRunning = false
    }

    companion object {
        private val updateEvent: Events.AppUpdateEvent = Events.AppUpdateEvent(0.1, 1.0)

        fun updateOf(gameTime: Double, delta: Double): Events.AppUpdateEvent {
            updateEvent.gameTime = gameTime
            updateEvent.delta = delta
            return updateEvent
        }
    }

}

