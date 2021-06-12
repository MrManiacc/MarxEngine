package marx.window

import dorkbox.messageBus.annotations.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.window.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*
import java.awt.SystemColor.*

data class GlfwWindow(
    override val width: Int = 1920,
    override val height: Int = 1080,
    override val title: String = "glfw-window",
    val vsync: Boolean = false,
    val app: Application
) : IWindow {

    private var handle: Long = -1
    private val keyEvent = Events.AppKeyEvent(this, -1, -1, -1, -1)
    private val mouseEvent = Events.AppMouseEvent(this, -1, -1, -1)
    private val resizeEvent = Events.WindowResizedEvent(this, -1, -1)
    override val shouldClose: Boolean
        get() = GLFW.glfwWindowShouldClose(handle)

    @Subscribe
    fun onInitialize(event: Events.AppCreatedEvent) {
        initWindow()
        initCallbacks()
        finalizeWindow()
        presentWindow()
        app.publish(Events.WindowCreatedEvent(this))
    }

    private fun initCallbacks() {
        glfwSetKeyCallback(handle) { _, key, scancode, action, mods ->
            keyEvent.key = key
            keyEvent.scancode = scancode
            keyEvent.action = action
            keyEvent.mods = mods
            app.publish(keyEvent)
        }
        glfwSetMouseButtonCallback(handle) { _, button, action, mods ->
            mouseEvent.button = button
            mouseEvent.action = action
            mouseEvent.mods = mods
            app.publish(mouseEvent)
        }

        glfwSetFramebufferSizeCallback(handle) { _, width, height ->
            resizeEvent.width = width
            resizeEvent.height = height
            app.publish(resizeEvent)
        }
    }

    private fun finalizeWindow() {
        // Get the thread stack and push a new frame
        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(handle, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                handle,
                (videoMode!!.width() - pWidth[0]) / 2,
                (videoMode.height() - pHeight[0]) / 2
            )
        }

        // Make the OpenGL context current

        // Make the OpenGL context current
        glfwMakeContextCurrent(handle)
        // Enable v-sync
        // Enable v-sync
        glfwSwapInterval(if (vsync) 1 else 0)

        // Make the window visible

        // Make the window visible
        glfwShowWindow(handle)
    }

    private fun presentWindow() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    private fun initWindow() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()
        if (!glfwInit())
            throw IllegalStateException("Unable to create glfw")
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        handle = glfwCreateWindow(width, height, title, NULL, NULL)
        if (handle == NULL)
            throw  RuntimeException("Failed to create the GLFW window");

    }

    @Subscribe
    fun onUpdate(event: Events.AppUpdateEvent) {
        if (!this.shouldClose) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer
            glfwSwapBuffers(handle) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()

        }
    }

    @Subscribe
    fun onClose(event: Events.AppShutdownEvent) {

    }
}