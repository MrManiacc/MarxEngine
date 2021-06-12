package marx.engine.events

import marx.engine.*
import marx.engine.window.*

object Events {

    data class WindowResizedEvent(val window: IWindow, var width: Int, var height: Int) : IEvent

    data class WindowCreatedEvent(val window: IWindow) : IEvent

    data class AppCreatedEvent(val app: Application) : IEvent

    data class AppUpdateEvent(var delta: Double, var gameTime: Double) : IEvent

    data class AppRenderEvent(val time: Long, val dt: Float) : IEvent

    data class AppShutdownEvent(val app: Application) : IEvent

    data class AppKeyEvent(val window: IWindow, var key: Int, var scancode: Int, var action: Int, var mods: Int) : IEvent

    data class AppMouseEvent(val window: IWindow, var button: Int, var action: Int, var mods: Int) : IEvent

}

interface IEvent