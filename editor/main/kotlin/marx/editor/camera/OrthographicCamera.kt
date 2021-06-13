package marx.editor.camera

import marx.engine.render.camera.*
import org.joml.*

/**
 * Provides a 2d camera projections
 */
class OrthographicCamera(
    left: Float,
    right: Float,
    bottom: Float,
    top: Float
) : Camera<OrthographicCamera>() {
    override var projectionMatrix: Matrix4f = Matrix4f().ortho(left, right, bottom, top, -1.0f, 1.0f)

    init {
        recalculateViewMatrix()
    }
}