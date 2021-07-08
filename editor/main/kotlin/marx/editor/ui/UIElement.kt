package marx.editor.ui

import marx.api.extension.Extension


/**
 * This is the root of all elements. They are singletons that allow for snippets of code to be rendered.
 */
fun interface UIElement : Extension {
    /**
     * This should render all of the elements related to this value
     */
    fun render(context: UIContext)
}
