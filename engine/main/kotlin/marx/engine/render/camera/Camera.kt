package marx.engine.render.camera

import marx.engine.math.*
import marx.engine.math.MathDSL.Conversions.scale
import marx.engine.math.components.*

import marx.engine.math.components.Position.*

import org.joml.*

/**
 * This root of a camera.
 */
abstract class Camera<C>(
    val position: Vec3 = Vec3(),
    val rotation: Vec3 = Vec3(),
    var moveSpeed: Float = 1f,
    var lookSpeed: Float = 100f
) : IVec<Camera<C>> {
    abstract var projectionMatrix: Matrix4f
        protected set

    /*Used as a buffer for the view matrix**/
    protected
    val viewBuffer: Matrix4f = Matrix4f()

    /*Used as a buffer**/
    protected val viewProBuffer: Matrix4f = Matrix4f()

    /*The player's view matrix**/
    abstract val viewMatrix: Matrix4f

    /*This is what is going to be used for rendering**/
    val viewProjection: Matrix4f get() = projectionMatrix.mul(viewMatrix, viewProBuffer)

    /* should divide add vector [OTHER] from this vector [SELF]*/
    override fun <OTHER : IVec<*>> plus(other: OTHER): Camera<C> {
        position.add(other[X, 0f], other[Y, 0f], other[Z, 0f])
        return this
    }

    /*This should divide subtract vector [OTHER] from this vector [SELF]*/
    override fun <OTHER : IVec<*>> minus(other: OTHER): Camera<C> {
        position.add(-other[X, 0f], -other[Y, 0f], -other[Z, 0f])
        return this
    }

    /*This should divide this vector [SELF] by the other vector [OTHER]*/

    override fun <OTHER : IVec<*>> div(other: OTHER): Camera<C> {
        position.div(other[X, 1f], other[Y, 1f], other[Z, 1f])
        return this
    }

    /*This should multiple this vector [SELF] by the other vector [OTHER]*/
    override fun <OTHER : IVec<*>> times(other: OTHER): Camera<C> {
        position.mul(other[X, 1f], other[Y, 1f], other[Z, 1f])
        return this
    }

    /*This should set the float at the given index*/
    override fun set(
        component: Comp,
        value: Float
    ) {
        when (component) {
            X -> this.position.x = value
            Y -> this.position.y = value
            Z -> this.position.z = value
            Rotation.Yaw -> this.rotation.x = value
            Rotation.Pitch -> this.rotation.y = value
            Rotation.Roll -> this.rotation.z = value
        }
    }

    /*This should get the component*/
    override fun get(component: Comp): Float? =
        when (component) {
            X -> this.position.x
            Y -> this.position.y
            Z -> this.position.z
            Rotation.Yaw -> this.rotation.x
            Rotation.Pitch -> this.rotation.y
            Rotation.Roll -> this.rotation.z
            else -> null
        }

    infix fun <OTHER : IVec<*>> move(other: OTHER): Camera<C> {
        this.position.set(other[X, position.x], other[Y, position.y], other[Z, position.z])
        return this
    }

    infix fun setX(other: Number): Camera<C> {
        this.position.x = other.toFloat()
        return this
    }

    infix fun setY(other: Number): Camera<C> {
        this.position.y = other.toFloat()
        return this
    }

    infix fun setZ(other: Number): Camera<C> {
        this.position.z = other.toFloat()
        return this
    }

    infix fun x(other: Number): Camera<C> {
        this.position.x += other.toFloat()
        return this
    }

    infix fun y(other: Number): Camera<C> {
        this.position.y += other.toFloat()
        return this
    }

    infix fun z(other: Number): Camera<C> {
        this.position.z += other.toFloat()
        return this
    }

    infix fun setPitch(other: Number): Camera<C> {
        this.rotation.x = other.toFloat()
        return this
    }

    infix fun setYaw(other: Number): Camera<C> {
        this.rotation.y = other.toFloat()
        return this
    }

    infix fun setRoll(other: Number): Camera<C> {
        this.rotation.z = other.toFloat()
        return this
    }

    infix fun pitch(other: Number): Camera<C> {
        this.rotation.x += other.toFloat()
        return this
    }

    infix fun yaw(other: Number): Camera<C> {
        this.rotation.y += other.toFloat()
        return this
    }

    infix fun roll(other: Number): Camera<C> {
        this.rotation.z += other.toFloat()
        return this
    }

    override fun toString(): String =
        "Camera(position=$position, scale=$scale, rotation=$rotation)"

}