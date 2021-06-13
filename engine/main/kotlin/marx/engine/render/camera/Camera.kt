package marx.engine.render.camera

import org.joml.*
import java.lang.IllegalStateException

/**
 * This root of a camera.
 */
abstract class Camera<C>(
    val position: Vector3f = Vector3f(),
    val rotation: Quaternionf = Quaternionf()
) {
    abstract var projectionMatrix: Matrix4f
        protected set
    var viewMatrix: Matrix4f = Matrix4f()
        protected set

    /**This is what is going to be used for rendering**/
    var viewProjection: Matrix4f = Matrix4f().identity()
        protected set

    /**
     * This method will be called per frame to update the view matrix. It should be able to move independently of the projection
     * matrix.
     */
    protected open fun recalculateViewMatrix() {
        viewMatrix = viewMatrix.translate(position)
            .rotate(rotation.z, Vector3f(0f, 0f, 1f))
            .invert()
        viewProjection = projectionMatrix.mul(viewMatrix)
    }

    /**
     * This will offset the camera's position
     */
    infix fun rotateX(rot: Float): Camera<C> {
        rotation.rotateAxis(rot, 1f, 0f, 0f)
        recalculateViewMatrix()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun rotateY(rot: Float): Camera<C> {
        rotation.rotateAxis(rot, 0f, 1f, 0f)
        recalculateViewMatrix()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun rotateZ(rot: Float): Camera<C> {
        rotation.rotateAxis(rot, 0f, 0f, 1f)
        recalculateViewMatrix()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun <V : Vector2fc> rotate(rot: V): Camera<C> {
        this rotateX rot.x()
        this rotateY rot.y()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun <V : Vector3fc> rotate(rot: V): Camera<C> {
        this rotateX rot.x()
        this rotateY rot.y()
        this rotateZ rot.z()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun move(offset: Float): Camera<C> {
        position.add(offset, offset, offset)
        recalculateViewMatrix()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun <V : Vector2fc> move(offset: V): Camera<C> {
        position.add(offset.x(), offset.y(), 0.0f)
        recalculateViewMatrix()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun <V : Vector3fc> move(offset: V): Camera<C> {
        position.add(offset)
        recalculateViewMatrix()
        return this
    }

    /**
     * This will offset the camera's position
     */
    infix fun move(offset: Pair<Float, Float>): Camera<C> = this.move(floatArrayOf(offset.first, offset.second))

    /**
     * This will offset the camera's position
     */
    infix fun move(offset: FloatArray): Camera<C> = this.apply {
        when (offset.size) {
            1 -> {
                this.move(offset[0])
                return@apply
            }
            2 -> {
                this.move(Vector2f(offset))
                return@apply
            }
            3 -> {
                this.move(Vector3f(offset))
                return@apply
            }
            else -> throw IllegalStateException("Invalid number of elements. Must be exactly 1, 2, or 3 elements!l")
        }
    }


}