package marx.engine.utils

import org.joml.*

/**
 * This will do ors of the given values.
 */
fun Int.orEquals(vararg ints: Int): Int {
    var out = this
    for (element in ints)
        out = out or element
    return out
}
/*================Float array/vector transforms =================*/
fun FloatArray.toFloat(): Float = this[0]
fun FloatArray.toVector2f(): Vector2f = Vector2f(this)
fun FloatArray.toVector3f(): Vector3f = Vector3f(this)
