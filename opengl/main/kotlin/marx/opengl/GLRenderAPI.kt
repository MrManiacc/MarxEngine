package marx.opengl

import marx.engine.render.*
import marx.engine.render.Renderer.RenderAPI.*
import marx.engine.window.*
import org.lwjgl.glfw.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*

/**
 * This is the backend render context for opengl rendering related functionality
 */
open class GLRenderAPI(val window: IWindow) : Renderer.RenderAPI() {

    /**
     * Initialize the given graphics context
     */
    override fun init() {
        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window.handle)
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()
    }

    /**
     * Swap the given buffers of the graphics context
     */
    override fun swap() {
        window.swapBuffers()
    }

    /**
     * Poll the input for the graphics context
     */
    override fun poll() {
        window.pollInput()
    }

    /**
     * Allows for viewport resizing
     */
    override fun viewport(size: Pair<Int, Int>, pos: Pair<Int, Int>) {
        glViewport(pos.first, pos.second, size.first, size.second)
    }

    /**
     * Clear the screen with the given color
     */
    override fun clear(color: FloatArray?, clearFlags: ClearFlags) {
        when (clearFlags) {
            ClearFlags.COLOR -> {
                glClear(GL_COLOR_BUFFER_BIT)
            }
            ClearFlags.DEPTH -> {
                glClear(GL_DEPTH_BUFFER_BIT)
            }
            ClearFlags.COLOR_DEPTH -> {
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            }
        }
        if (color != null) glClearColor(color[0], color[1], color[2], color[3])
    }


}