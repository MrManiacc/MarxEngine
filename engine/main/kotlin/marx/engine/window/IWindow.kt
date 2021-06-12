package marx.engine.window

interface IWindow {
    val title: String
    val width: Int
    val height: Int
    val shouldClose: Boolean get() = false
    var fullscreen: Boolean
    var vsync: Boolean
    fun swapBuffers()
    fun pollInput()
    var handle: Long
}