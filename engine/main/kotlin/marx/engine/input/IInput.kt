package marx.engine.input

interface IInput {
    fun isKeyPressed(keyCode: Int): Boolean
    fun isKeyReleased(keyCode: Int): Boolean
    fun isKeyRepeated(keyCode: Int): Boolean
}