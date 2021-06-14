package marx.engine.utils

object MathUtils {

    fun Number.orEquals(vararg numbers: Number): Int {
        var out = this.toInt()
        numbers.forEach {
            out = out or it.toInt()
        }
        return out
    }
}
