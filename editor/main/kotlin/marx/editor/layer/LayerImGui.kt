package marx.editor.layer

import imgui.*
import imgui.type.*
import marx.editor.wrapper.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Timestep
import marx.engine.events.Events.Gui.*
import marx.engine.layer.*

class LayerImGui(app: Application<*>) : Layer<DebugRenderAPI>(app, DebugRenderAPI::class) {

    private val dockspaceName = "core_dockspace"
    private val viewportEvent = ViewportOverlay()
    private val propertiesEvent = PropertiesOverlay()
    private val vsync = ImBoolean(false)

    override fun onAttach() {
        renderAPI.init()
    }

    override fun onUpdate(update: Timestep) = renderAPI.frame { onRenderUi(update, ImGui.getIO()) }

    /**
     * This is called inside the render frame of imgui. It's an overlay so it should be last.
     */
    private fun onRenderUi(update: Timestep, io: ImGuiIO) {
        io.deltaTime = update.deltaTime.toFloat()
        renderAPI.dockspace(dockspaceName, ::renderProperties, ::renderViewport)
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
        app.publish(viewportEvent)
    }

    /**
     * This is used to destroy the application upon pressing escape
     */
    override fun onEvent(event: Event) {

    }

}