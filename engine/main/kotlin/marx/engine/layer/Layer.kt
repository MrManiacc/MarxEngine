package marx.engine.layer

import dorkbox.messageBus.annotations.*
import marx.engine.events.*
import marx.engine.events.Events.App.Update

abstract class Layer(val name: String = "layer") {
    open fun onAttach() = Unit
    open fun onDetach() = Unit
    abstract fun onUpdate(update: Update)
    abstract fun onEvent(event: Event)

}