package marx.sandbox.layer

import imgui.*
import imgui.flag.ImGuiWindowFlags.*
import marx.editor.wrapper.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Update
import marx.engine.events.Events.Input.KeyPress
import marx.engine.events.Events.Shader.*
import marx.engine.layer.*
import marx.engine.render.*
import marx.opengl.*
import marx.opengl.GLBuffer.GLIndexBuffer
import marx.opengl.GLBuffer.GLVertexBuffer
import marx.sandbox.*
import mu.*
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.slf4j.*
import kotlin.math.*
import kotlin.time.*

/**
 * This layer is used for debugging purposes
 */
class LayerDebug(app: Application<*>) : Layer<DebugRenderAPI>(app, DebugRenderAPI::class, "debug-layer") {
    private val log: Logger = KotlinLogging.logger { }
    private val simpleShader = GLShader(app)
    private val editorShader = GLShader(app)

    /**This creates a quad of size 0.5**/
    val quadVAO: VertexArray = GLVertexArray().apply {
        this += GLVertexBuffer(
            floatArrayOf(
                0.5f, 0.5f, 0.0f,  // top right
                0.5f, -0.5f, 0.0f,  // bottom right
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

    /**This creates a quad of size 0.5**/
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

    /**
     * This is called upon the layer being presented.
     */
    override fun onAttach() {
        renderAPI.init()
        quadVAO.create()
        triangleVAO.create()
        if (simpleShader.compile(Shaders.simple)) log.warn("Successfully compiled simple shader: ${simpleShader::class.qualifiedName}")
        if (editorShader.compile(Shaders.colored(version = "330", core = true, color = Vector3f(0.8f, 0.232f, 0.323f)))) log.warn(
            "Successfully compiled editor shader: ${editorShader::class.qualifiedName}"
        )
    }

    /**
     * This will draw every frame
     */
    override fun onUpdate(update: Update) {
        if (app.input.isKeyDown(GLFW_KEY_LEFT))
            Sandbox.editorCamera.move(floatArrayOf(0.5f * update.deltaTime.toFloat(), 0f))
        if (app.input.isKeyDown(GLFW_KEY_RIGHT))
            Sandbox.editorCamera.move(floatArrayOf(-0.5f * update.deltaTime.toFloat(), 0f))
        if (app.input.isKeyDown(GLFW_KEY_DOWN))
            Sandbox.editorCamera.move(floatArrayOf(0f, 0.5f * update.deltaTime.toFloat()))
        if (app.input.isKeyDown(GLFW_KEY_UP))
            Sandbox.editorCamera.move(floatArrayOf(0f, -0.5f * update.deltaTime.toFloat()))

        drawScene()
        renderAPI.frame { drawGui(update) }
    }

    /**
     * Draws our test scene
     */
    private fun drawScene() {
        scene.sceneOf(Sandbox.editorCamera) {
            submit(quadVAO, simpleShader)
            submit(triangleVAO, editorShader)
        }
    }

    /**
     * This is called inside the render frame of imgui. It's an overlay so it should be last.
     */
    @OptIn(ExperimentalTime::class)
    private fun drawGui(update: Update) {
        val pos = app.window.pos
        val size = app.window.size
        val width = 140f
        val height = 80f
        val xInset = 30f
        val yInset = 30f
        //        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10f, 10f)
        ImGui.setNextWindowSize(width, height)
        ImGui.setNextWindowPos(pos.first + size.first - (xInset + width), pos.second + yInset)
        if (ImGui.begin("metrics", NoInputs or NoResize or NoScrollbar or NoScrollWithMouse or NoCollapse)) {
            ImGui.text("game time: ${update.gameTime.seconds.absoluteValue}")
            ImGui.text("delta: ${update.deltaTime.seconds.inMilliseconds}")
            ImGui.text("fps: ${ImGui.getIO().framerate.roundToInt()}")
        }
        ImGui.end()

    }

    override fun onEvent(event: Event) {
        if (event is Compiled)
            if (event.result.isValid)
                log.info("Successfully compiled '${event.result.type.name}' shader: ${event.result.message}")
            else
                log.error("Failed to compile '${event.result.type.name}' shader: ${event.result.message}")
        else if (event is KeyPress) {
            if (event.key == GLFW_KEY_R) { //Reload the shader
                editorShader.destroy()
                editorShader.compile(Shaders.simple)
                log.info("Reloaded shader: $editorShader")
            }
        }
    }

    override fun onDetach() {
        editorShader.destroy()
        quadVAO.dispose()
        triangleVAO.dispose()
    }


}