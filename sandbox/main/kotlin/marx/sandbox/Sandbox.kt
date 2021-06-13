package marx.sandbox

import com.google.common.collect.*
import dorkbox.messageBus.*
import dorkbox.messageBus.annotations.*
import imgui.*
import marx.editor.*
import marx.editor.wrapper.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App
import marx.engine.events.Events.Gui.*
import marx.engine.events.Events.Input.KeyPress
import marx.engine.events.Events.Window
import marx.engine.events.Events.Window.Initialized
import marx.engine.input.*
import marx.engine.layer.*
import marx.engine.render.*
import marx.engine.render.Renderer.RenderAPI.ClearFlags.*
import marx.engine.window.*
import marx.opengl.*
import marx.sandbox.layer.*
import marx.window.*
import mu.*
import org.lwjgl.glfw.GLFW.*
import org.slf4j.*

/**
 * This is the front on the engine. Its the sandbox/the application used for testing the engine/libraries.
 */
object Sandbox : Application<GLRenderAPI> {
    override val log: Logger = KotlinLogging.logger { }
    override val eventbus: MessageBus = MessageBus(4)
    override val window: IWindow = GlfwWindow(title = "Sandbox, glfw", app = this)
    override val input: IInput = GlfwInput(window)
    override var isRunning: Boolean = false
    override var gameTime: Double = 0.0
    override var startTime: Long = System.currentTimeMillis()
    override val layers: MutableList<Layer> = Lists.newArrayList()
    override var insertIndex: Int = 0
    val color = floatArrayOf(0.239215f, 0.2549019f, 0.278431372f, 1f)

    /**==================Render==================**/
    override val renderAPI: GLRenderAPI = GLRenderAPI(window).apply(Renderer::register)
    val debugAPI: DebugRenderAPI = DebugRenderAPI(window).apply(Renderer::register)
    private val editorLayer: Layer = LayerImGui(this)
    private val debugLayer: Layer = LayerDebug(this)

    /**
     * We must subscribe anything important here. In the future any entity systems
     * would be subscribed here
     */
    init {
        subscribe(debugAPI)
        subscribe(window)
        subscribe(this)
    }

    /**
     * This is used to initialized our layers
     */
    @Subscribe fun onInit(event: Initialized) {
        pushLayer(debugLayer)
        pushOverlay(editorLayer)
        log.debug("Initialized sandbox window: ${event.window} and added all layers.")
    }

    @Subscribe fun onPropertiesRender(event: PropertiesOverlay) {
        ImGui.colorEdit4("clear color", color)
    }

    /**
     * This maps the layer's accordingly
     */
    @Subscribe fun onKeyPressed(event: KeyPress) {
        when (event.key) {
            GLFW_KEY_KP_0 -> {
                popLayer(editorLayer)
                popOverlay(debugLayer)
                pushOverlay(debugLayer)
            }
            GLFW_KEY_KP_1 -> {
                popLayer(editorLayer)
                popOverlay(debugLayer)
                pushLayer(editorLayer)
            }
            GLFW_KEY_KP_2 -> {
                popLayer(editorLayer)
                popOverlay(debugLayer)
            }
            GLFW_KEY_KP_3 -> {
                popLayer(editorLayer)
                popOverlay(debugLayer)
                pushLayer(editorLayer)
                pushOverlay(debugLayer)
            }
            GLFW_KEY_KP_4 -> {
                popLayer(editorLayer)
                popOverlay(debugLayer)
                pushOverlay(debugLayer)
                pushLayer(editorLayer)
            }
        }
    }

    /**
     * This is called every frame and updates the children layers.
     * It also polls the window and swaps the buffers
     */
    override fun onUpdate(event: App.Update) {
        renderAPI.clear(color, COLOR)
        super.onUpdate(event)
    }

    @Subscribe
    override fun destroy(event: Window.Destroy) {
        super.destroy(event)
        debugAPI.dispose()
        renderAPI.dispose()
    }
}

