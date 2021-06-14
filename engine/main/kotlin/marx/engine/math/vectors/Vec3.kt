package marx.engine.math.vectors

import marx.engine.math.comp.*
import marx.engine.math.comp.Comp.*
import marx.engine.math.comp.comps.*
import marx.engine.math.comp.comps.AxisAngle.*
import marx.engine.math.comp.comps.Position.*
import marx.engine.math.comp.comps.Rotation.*
import org.joml.*
import java.nio.*
import java.text.*

/**
 * This is our implementation of a vector4f. Its based upon joml's implementation
 */
class Vec3 : Vector3f, IVec<Vec3> {
    constructor() : super()
    constructor(d: Number) : super(d.toFloat())
    constructor(
        x: Number,
        y: Number,
        z: Number
    ) : super(x.toFloat(), y.toFloat(), z.toFloat())

    constructor(v: Vector3fc?) : super(v)
    constructor(v: Vector3ic?) : super(v)
    constructor(
        v: Vector2fc,
        z: Number
    ) : super(v, z.toFloat())

    constructor(
        v: Vector2ic,
        z: Number
    ) : super(v, z.toFloat())

    constructor(xyz: FloatArray?) : super(xyz)
    constructor(buffer: ByteBuffer?) : super(buffer)
    constructor(
        index: Int,
        buffer: ByteBuffer?
    ) : super(index, buffer)

    constructor(buffer: FloatBuffer?) : super(buffer)
    constructor(
        index: Int,
        buffer: FloatBuffer?
    ) : super(index, buffer)

    /**
     * This should divide add vector [V] from this vector [SELF]
     */
    override fun <V : IVec<*>> plus(other: V): Vec3 =
        Vec3(this.x + other[X, 0f], this.y + other[Y, 0f], this.z + other[Z, 0f])

    /**
     * This should divide subtract vector [V] from this vector [SELF]
     */
    override fun <V : IVec<*>> minus(other: V): Vec3 =
        Vec3(this.x - other[X, 0f], this.y - other[Y, 0f], this.z - other[Z, 0f])

    /**
     * This should divide this vector [SELF] by the other vector [V]
     */
    override fun <V : IVec<*>> div(other: V): Vec3 =
        Vec3(this.x / other[X, 1f], this.y / other[Y, 1f], this.z / other[Z, 1f])

    /**
     * This should multiple this vector [SELF] by the other vector [V]
     */
    override fun <V : IVec<*>> times(other: V): Vec3 =
        Vec3(this.x * other[X, 1f], this.y * other[Y, 1f], this.z * other[Z, 1f])

    /**
     * This should set the float at the given index
     */
    override operator fun set(
        component: Comp,
        value: Float
    ) {
        when (component.idx) {
            0 -> this.x = value
            1 -> this.y = value
            2 -> this.z = value
        }
    }

    /**
     * This should get the
     */
    override operator fun get(component: Comp): Float? =
        when (component.idx) {
            0 -> x
            1 -> y
            2 -> z
            else -> null
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other !is Vector3fc) return false
        return equals(other.x(), other.y(), other.z())
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + java.lang.Float.floatToIntBits(x)
        result = prime * result + java.lang.Float.floatToIntBits(y)
        result = prime * result + java.lang.Float.floatToIntBits(z)
        return result
    }

    override fun toString(): String = super.toString(DecimalFormat.getInstance())


}