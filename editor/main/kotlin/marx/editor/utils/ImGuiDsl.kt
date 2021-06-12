package marx.editor.utils

import imgui.*
import imgui.flag.*
import mu.*
import org.lwjgl.glfw.*

val logger = KotlinLogging.logger {}

/**
 * This will use the keys to rebind all the keys
 */
fun ImGuiIO.mapKeys() {
    Keys.values().forEach {
        this.setKeyMap(it.mapping.first, it.mapping.second)
        logger.debug { "Mapped key from ${it.mapping.first} to ${it.mapping.second}" }
    }
}

/**
 * This is used for imgui keys.They are mapped to the glfw keys
 */
enum class Keys(val mapping: Pair<Int, Int>) {
    Tab(ImGuiKey.Tab to GLFW.GLFW_KEY_TAB),
    LeftArrow(ImGuiKey.LeftArrow to GLFW.GLFW_KEY_LEFT),
    RightArrow(ImGuiKey.RightArrow to GLFW.GLFW_KEY_RIGHT),
    UpArrow(ImGuiKey.UpArrow to GLFW.GLFW_KEY_UP),
    DownArrow(ImGuiKey.DownArrow to GLFW.GLFW_KEY_DOWN),
    PageUp(ImGuiKey.PageUp to GLFW.GLFW_KEY_PAGE_UP),
    PageDown(ImGuiKey.PageDown to GLFW.GLFW_KEY_PAGE_DOWN),
    Home(ImGuiKey.Home to GLFW.GLFW_KEY_HOME),
    End(ImGuiKey.End to GLFW.GLFW_KEY_END),
    Insert(ImGuiKey.Insert to GLFW.GLFW_KEY_INSERT),
    Delete(ImGuiKey.Delete to GLFW.GLFW_KEY_DELETE),
    Backspace(ImGuiKey.Backspace to GLFW.GLFW_KEY_BACKSPACE),
    Space(ImGuiKey.Space to GLFW.GLFW_KEY_SPACE),
    Enter(ImGuiKey.Enter to GLFW.GLFW_KEY_ENTER),
    Escape(ImGuiKey.Escape to GLFW.GLFW_KEY_ESCAPE),
    KeyPadEnter(ImGuiKey.KeyPadEnter to GLFW.GLFW_KEY_KP_ENTER),

    /**
     * for text edit CTRL+A: select all
     */
    A(ImGuiKey.A to GLFW.GLFW_KEY_A),

    /**
     * for text edit CTRL+C: copy
     */
    C(ImGuiKey.C to GLFW.GLFW_KEY_C),

    /**
     * for text edit CTRL+V: paste
     */
    V(ImGuiKey.V to GLFW.GLFW_KEY_V),

    /**
     * for text edit CTRL+X: cut
     */
    X(ImGuiKey.X to GLFW.GLFW_KEY_X),

    /**
     * for text edit CTRL+Y: redo
     */
    Y(ImGuiKey.Y to GLFW.GLFW_KEY_Y),

    /**
     * for text edit CTRL+Z: undo
     */
    Z(ImGuiKey.Z to GLFW.GLFW_KEY_Z),

}
