package marx.editor.camera

import dorkbox.messageBus.annotations.*
import marx.engine.events.*
import marx.engine.render.camera.*
import org.joml.*

/**
 * Provides a 2d camera projections
 */
class OrthographicCamera(
    left: Float,
    right: Float,
    bottom: Float,
    top: Float,
    var scale: Float = 5f
) : Camera<OrthographicCamera>() {

    override var projectionMatrix: Matrix4f = Matrix4f().ortho(left, right, bottom, top, -1.0f, 1.0f)

    /**The player's view matrix**/
    override val viewMatrix: Matrix4f
        get() = viewBuffer.identity()
            .translate(position)
            .rotateZ(rotation.x)
            .invertOrtho()

    /**
     * This allows us to recompute our projection matrix every time our window resizes
     */
    @Subscribe fun onResize(event: Events.Window.Resize) {
        val aspect = event.width.toFloat() / event.height.toFloat()
        projectionMatrix = projectionMatrix.identity().ortho(
            -aspect * scale,
            aspect * scale,
            -scale,
            scale,
            1.0f,
            -1.0f
        )
    }


}