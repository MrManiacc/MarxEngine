package marx.opengl.scene

import com.google.common.collect.*
import marx.engine.render.*
import marx.engine.render.camera.*
import marx.engine.render.scene.*
import java.util.*
import kotlin.reflect.*

/**
 * This controls an editor's scene.
 */
class GLScene<API : RenderAPI>(
    private val apiType: KClass<API>
) : RenderScene {
    private var camera: Camera<*>? = null
    override val renderAPI: RenderAPI get() = Renderer(apiType)
    private var shader: Shader? = null
    private val drawCalls: MutableList<() -> VertexArray> = Lists.newArrayList()

    /**
     * This will start a new scene
     */
    override fun beginScene(camera: Camera<*>) {
        this.camera = camera
        drawCalls.clear()
    }

    /**
     * This method should be overloaded for all of the various types of things we can submit
     */
    override fun submit(array: VertexArray) {
        drawCalls.add {
            array.bind()
            renderAPI.drawIndexed(array)
            array.unbind()
            array
        }
    }

    /**
     * This method should be overloaded for all of the various types of things we can submit
     */
    override fun submit(array: VertexArray, shader: Shader) {
        drawCalls.add {
            shader.bind()
            array.bind()
            camera?.viewProjection?.let { viewProjection -> shader.uploadMat4("u_ViewProjection", viewProjection) }
            renderAPI.drawIndexed(array)
            array.unbind()
            shader.unbind()
            array
        }
    }

    /**
     * This clears our all of objects or entities on the scene
     */
    override fun flush() {
        shader = null
        camera = null
        drawCalls.clear()
    }

    /**
     * Ends the current scene, renders all of the submitted meshes
     */
    override fun endScene() {
        drawCalls.forEach { it() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GLScene<*>) return false

        if (apiType != other.apiType) return false

        return true
    }

    override fun hashCode(): Int {
        return apiType.hashCode()
    }


}
