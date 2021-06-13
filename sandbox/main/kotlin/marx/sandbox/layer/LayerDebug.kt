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
import mu.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.*
import org.slf4j.*
import kotlin.math.*
import kotlin.time.*

/**
 * This layer is used for debugging purposes
 */
class LayerDebug(app: Application<*>) : Layer(app, "debug-layer") {
    private val wrapper = DebugRenderAPI(app.window)

    private val log: Logger = KotlinLogging.logger { }
    private var vao: Int = 0
    private val shader = GLShader(app)
    private val quad: Pair<FloatArray, IntArray> =
        floatArrayOf(
            0.5f, 0.5f, 0.0f,  // top right
            0.5f, -0.5f, 0.0f,  // bottom right
            -0.5f, -0.5f, 0.0f,  // bottom left
            -0.5f, 0.5f, 0.0f   // top left
        ) to intArrayOf(
            0, 1, 3,   // first triangle
            1, 2, 3    // second triangle
        )
    val vertexBuffer: GLVertexBuffer = Buffer(quad.first, 3)
    val indexBuffer: GLIndexBuffer = Buffer(quad.second)

    /****/
    val vertexArray: VertexArray = GLVertexArray().apply {
        this += vertexBuffer
        this += indexBuffer
    }

    override fun onAttach() {
        vertexArray.create()
        wrapper.init()
        if (shader.compile(Shaders.simple)) log.warn("Successfully compiled shader: ${shader::class.qualifiedName}")
    }

    override fun onUpdate(update: Update) {
        shader.bind()
        vertexArray.bind()
        GL11.glDrawElements(GL_TRIANGLES, quad.second.size, GL_UNSIGNED_INT, NULL)
        vertexArray.unbind()
        shader.unbind()
        wrapper.frame { onImGui(update) }
    }

    /**
     * This is called inside the render frame of imgui. It's an overlay so it should be last.
     */
    @OptIn(ExperimentalTime::class)
    private fun onImGui(update: Update) {
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
                shader.destroy()
                shader.compile(Shaders.simple)
                log.info("Reloaded shader: $shader")
            }
        }
    }

    override fun onDetach() {
        shader.destroy()
        vertexArray.dispose()
    }


}