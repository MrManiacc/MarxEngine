package marx.editor.ui

/**
 * This is the unified api for all imgui related rendering. It provides a wrapper around imgui, allowing for not only
 * the use of the kotlin objects, meaning we can use: with(MarxUI) { /*UI code here*/ }. We can also define our own
 * custom ui elements and types that build upon the core imgui functionally.
 */
object MarxUI {
    /**The current context used for rendering**/
    internal val context: UIContext = UIContext()

    /**
     * Display a simple text element with the supplied [text].
     */
    fun text(text: String) = context.apply {
        push("text", text)
        UICore.Text()
    }

    /**
     * Display a label element with the supplied [label] as the key and the [text] as the value.
     */
    fun label(label: String, text: String) = context.apply {
        push("label", label)
        push("text", text)
        UICore.Label()
    }

    /**
     * This invokes the render method using the MarUI
     */
    operator fun UIElement.invoke() = render(context)

}