package marx.sandbox.layer

import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Timestep
import marx.engine.events.Events.Input.KeyPress
import marx.engine.events.Events.Shader.*
import marx.engine.layer.*
import marx.engine.render.*
import marx.opengl.*
import marx.opengl.GLBuffer.GLIndexBuffer
import marx.opengl.GLBuffer.GLVertexBuffer
import marx.opengl.data.*
import mu.*
import org.lwjgl.glfw.GLFW.*
import org.slf4j.*

/*
 * This layer is used for debugging purposes
 */
class LayerSimulate(app: Application<*>) : Layer<GLRenderAPI>(app, GLRenderAPI::class, "simulation-layer") {
    private val log: Logger = KotlinLogging.logger { }
    private val shader = GLShader(app)

    /*This creates a quad of size 0.5**/
    val quadVAO: VertexArray = GLVertexArray().apply {
        this += GLVertexBuffer(
            floatArrayOf(
                0.25f, 0.5f, 0.0f,  // top right
                0.25f, -0.5f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f,  // bottom left
                -0.5f, 0.5f, 0.0f // top left
            ), 3
        )

        this += GLIndexBuffer(
            intArrayOf(
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
            )
        )
    }

    /*This creates a quad of size 0.5**/
    val triangleVAO: VertexArray = GLVertexArray().apply {
        this += GLVertexBuffer(
            floatArrayOf(
                -0.33f, -0.5f, 0.0f,  // bottom left
                0.0f, 0.33f, 0.0f, // top left
                0.33f, -0.5f, 0.0f,  // bottom right
            ), 3
        )

        this += GLIndexBuffer(
            intArrayOf(
                0, 1, 2,   // first triangle
            )
        )
    }

    /*
   This is called upon the layer being presented.
     */
    override fun onAttach() {
        renderAPI.init()
        quadVAO.create()
        triangleVAO.create()
        if (shader.compile(Shaders.flatShader())) log.warn("Successfully compiled shader: ${shader::class.qualifiedName}")
    }

    /*
   This will draw every frame
     */
    override fun onUpdate(update: Timestep) {
        shader.bind()
        scene.submit(triangleVAO)
        shader.unbind()
    }

    override fun onEvent(event: Event) {
        if (event is Compiled)
            if (event.result.isValid)
                log.info("Successfully compiled '${event.result.type.name}' shader: ${event.result.message}")
            else
                log.error("Failed to compile '${event.result.type.name}' shader: ${event.result.message}")
        else if (event is KeyPress) {
            if (event.key == GLFW_KEY_R) { //Reload the shader
                shader.destroy()
                shader.compile(Shaders.flatShader())
                log.info("Reloaded shader: $shader")
            }
        }
    }

    override fun onDetach() {
        shader.destroy()
        triangleVAO.dispose()
        quadVAO.dispose()
    }


}