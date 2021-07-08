package marx.editor.ui

import imgui.ImGui

object UICore {

    /**
     * This is a ui label
     */
    object Text : UIElement {
        /**
         * This should render all of the elements related to this value
         */
        override fun render(context: UIContext) {
            val text: String = context.pop("text") { "no text found in UIContext" }
            ImGui.text(text)
        }
    }

    /**
     * This is a ui label
     */
    object Label : UIElement {
        /**
         * This should render all of the elements related to this value
         */
        override fun render(context: UIContext) {
            val label: String = context.pop("label") { "no label found in UIContext" }
            val text: String = context.pop("text") { "no text found in UIContext" }
            ImGui.labelText(label, text)
        }
    }

}