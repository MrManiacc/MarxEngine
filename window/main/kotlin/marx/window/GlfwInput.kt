package marx.window

import dorkbox.messageBus.annotations.*
import marx.engine.events.*
import marx.engine.events.Events.Input.KeyEvent
import marx.engine.input.*

class GlfwInput : IInput {
    private val keys: IntArray = IntArray(1024)
    private val mods: IntArray = IntArray(1024)

    /**
     * Allows us to have the key's tracked
     */
    @Subscribe fun onKeyEvent(event: KeyEvent) {
        keys[event.key] = event.action
        mods[event.key] = event.mods
    }

    override fun isKeyReleased(keyCode: Int): Boolean {
        return keys[keyCode] == 0
    }

    override fun isKeyPressed(keyCode: Int): Boolean {
        return keys[keyCode] == 1
    }

    override fun isKeyRepeated(keyCode: Int): Boolean {
        return keys[keyCode] == 2
    }
}
