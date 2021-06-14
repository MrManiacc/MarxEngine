package marx.sandbox.layer

import imgui.*
import imgui.flag.*
import imgui.flag.ImGuiWindowFlags.*
import marx.editor.wrapper.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Timestep
import marx.engine.events.Events.Input.KeyPress
import marx.engine.events.Events.Shader.*
import marx.engine.layer.*
import marx.engine.math.*
import marx.engine.math.vectors.*
import marx.engine.render.*
import marx.engine.utils.StringUtils.format
import marx.opengl.*
import marx.opengl.GLBuffer.GLIndexBuffer
import marx.opengl.GLBuffer.GLVertexBuffer
import marx.sandbox.*
import mu.*
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.slf4j.*

/**
 * This layer is used for debugging purposes
 */
class LayerDebug(app: Application<*>) : Layer<DebugRenderAPI>(app, DebugRenderAPI::class, "debug-layer") {
    private val log: Logger = KotlinLogging.logger { }
    private val simpleShader = GLShader(app)
    private val editorShader = GLShader(app)
    private val transformOne = Transform(Vec3(0f, 0f, 0f), Vec3(), Vec3(1f))
    private val transformTwo = Transform(Vec3(), Vec3(), Vec3(1))

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
    override fun onUpdate(time: Timestep) {
        updateCamera(time)
        drawScene()
        renderAPI.frame { drawGui(time) }
    }

    private fun updateCamera(time: Timestep) = with(Sandbox.editorCamera) {
        val speed = 1.75f

        if (app.input.isKeyDown(GLFW_KEY_A))
            this offX (speed * time.deltaTime)
        else if (app.input.isKeyDown(GLFW_KEY_D))
            this offX (speed * time.deltaTime) * -1
        if (app.input.isKeyDown(GLFW_KEY_W))
            this offY (speed * time.deltaTime) * -1
        else if (app.input.isKeyDown(GLFW_KEY_S))
            this offY (speed * time.deltaTime)
        else Unit
    }

    /**
     * Draws our test scene
     */
    private fun drawScene() {
        scene.sceneOf(Sandbox.editorCamera) {
            submit(quadVAO, simpleShader, transformOne)
            submit(triangleVAO, editorShader, transformTwo)
        }
    }

    /**
     * This is called inside the render frame of imgui. It's an overlay so it should be last.
     */
    private fun drawGui(update: Timestep) {
        val winPos = app.window.pos
        val size = app.window.size
        val xInset = 200f
        var position: ImVec2? = null
        var scale: ImVec2? = null
        val metricsWidth = 115f
        val statesWidth = 175f

        ImGui.setNextWindowSize(metricsWidth, 0f, ImGuiCond.Always)
        ImGui.setNextWindowPos(winPos.first + size.first - xInset, winPos.second + 20f, ImGuiCond.Once)
        if (ImGui.begin("metrics", NoResize or NoScrollbar or NoScrollWithMouse or NoCollapse or NoDocking)) {
            ImGui.text("delta: " + update.deltaTime.format(5))
            ImGui.text("time: ${update.gameTime.format(3)}")
            ImGui.text("ms: " + update.milliseconds.format(3))
            ImGui.text("fps: ${ImGui.getIO().framerate.format(1)}")
            position = ImGui.getWindowPos()
            scale = ImGui.getWindowSize()
        }
        ImGui.end()
        scale?.let { size ->
            position?.let { pos ->
                ImGui.setNextWindowSize(statesWidth, 0f, ImGuiCond.Always)
                ImGui.setNextWindowPos(pos.x - (statesWidth - metricsWidth), pos.y + size.y + 10, ImGuiCond.Always)
                if (ImGui.begin("states", NoResize or NoScrollbar or NoScrollWithMouse or NoCollapse or NoDocking)) {
                    ImGui.text("fullscreen[f1]: ${app.window.fullscreen}")
                    ImGui.text("vsync [f3]: ${app.window.vsync}")
                    val frame = app.window.size
                    ImGui.text("window size: ${frame.first}, ${frame.second}")
                }
                ImGui.end()
            }
        }
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