package marx.editor

import imgui.*
import marx.editor.wrapper.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Update
import marx.engine.events.Events.Gui.*
import marx.engine.layer.*
import marx.engine.render.*

class LayerImGui(app: Application<*>) : Layer(app) {
    val guiRenderer: DebugRenderAPI = Renderer()
    private val dockspaceName = "core_dockspace"
    private val viewportEvent = ViewportOverlay()
    private val propertiesEvent = PropertiesOverlay()

    override fun onAttach() {
        guiRenderer.init()
    }

    override fun onUpdate(update: Update) = guiRenderer.frame { onRenderUi(update, ImGui.getIO()) }

    /**
     * This is called inside the render frame of imgui. It's an overlay so it should be last.
     */
    private fun onRenderUi(update: Update, io: ImGuiIO) {
        io.deltaTime = update.deltaTime.toFloat()
        guiRenderer.dockspace(dockspaceName, ::renderProperties, ::renderViewport)
    }

    /**
     * This should render the imgui properties windows on the sidebar
     */
    private fun renderProperties() {
        app.publish(propertiesEvent)
    }

    /**
     * This should render the imgui properties windows on the sidebar
     */
    private fun renderViewport() {
    }

    /**
     * This is used to destroy the application upon pressing escape
     */
    override fun onEvent(event: Event) {

    }

}