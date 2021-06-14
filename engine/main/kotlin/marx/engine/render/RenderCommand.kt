package marx.engine.render

interface RenderCommand {

    /*
   NumberAllows for viewport resizing
     */
    fun viewport(size: Pair<Int, Int>, pos: Pair<Int, Int>) = Unit

    /*
   NumberClear the screen with the given color
     */
    fun clear(color: FloatArray? = floatArrayOf(0.1f, 0.1f, 0.1f, 1f), clearFlags: ClearFlags = ClearFlags.COLOR) = Unit

    /*
   NumberSwap the given buffers of the graphics context
     */
    fun swap() = Unit

    /*
   NumberPoll the input for the graphics context
     */
    fun poll() = Unit

    /*
   This clear flags for clearing the screen
     */
    enum class ClearFlags {
        COLOR, DEPTH, COLOR_DEPTH
    }
}