package marx.editor

import imgui.*
import imgui.type.*
import marx.editor.wrapper.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.events.Events.App.Update
import marx.engine.events.Events.Gui.*
import marx.engine.layer.*
import marx.engine.window.*

class ImGuiLayer(window: IWindow, val application: Application) : Layer() {
    private val wrapper = ImGuiWrapper(window)
    private val showDemo = ImBoolean(true)
    private val dockspaceName = "core_dockspace"
    private val overlayRenderEvent = ViewportOverlay()

    override fun onAttach() {
        wrapper.init()
    }

    override fun onUpdate(update: Update) = wrapper.frame { onRenderUi(update, ImGui.getIO()) }

    /**
     * This is called inside the render frame of imgui. It's an overlay so it should be last.
     */
    private fun onRenderUi(update: Update, io: ImGuiIO) {
        io.deltaTime = update.delta.toFloat()
        ImGui.showDemoWindow(showDemo)
        wrapper.dockspace(dockspaceName, ::renderProperties, ::renderViewport)
    }

    /**
     * This should render the imgui properties windows on the sidebar
     */
    private fun renderProperties() {
        ImGui.text("Testing")
    }

    /**
     * This should render the imgui properties windows on the sidebar
     */
    private fun renderViewport() {
        application.publish(overlayRenderEvent)
    }

    override fun onEvent(event: Event) {}

    override fun onDetach() {
        wrapper.close()
    }
}