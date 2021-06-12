package marx.window

import dorkbox.messageBus.annotations.*
import marx.engine.*
import marx.engine.events.*
import marx.engine.window.*
import mu.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*

data class GlfwWindow(
    override val width: Int = 1920,
    override val height: Int = 1080,
    override val title: String = "glfw-window",
    val app: Application
) : IWindow {
    private val log = KotlinLogging.logger {}

    private var handle: Long = -1

    override val shouldClose: Boolean
        get() = GLFW.glfwWindowShouldClose(handle)
    override var fullscreen: Boolean
        get() = false
        set(value) {}
    override var vsync: Boolean
        get() = false
        set(value) {}

    private val keyEvent = Events.Input.KeyEvent(this, -1, -1, -1, -1)
    private val keyPressEvent = Events.Input.KeyPress(this, -1, -1)
    private val keyReleaseEvent = Events.Input.KeyRelease(this, -1, -1)
    private val keyRepeatEvent = Events.Input.KeyRepeat(this, -1, -1)
    private val mousePressEvent = Events.Input.MousePress(this, -1, -1)
    private val mouseEvent = Events.Input.MouseEvent(this, -1, -1, -1)
    private val mouseReleaseEvent = Events.Input.MouseRelease(this, -1, -1)
    private val mouseRepeatEvent = Events.Input.MouseRepeat(this, -1, -1)
    private val mouseScrollEvent = Events.Input.MouseScroll(this, 0.0f, 0.0f)
    private val mouseMoveEvent = Events.Input.MouseMove(this, 0.0f, 0.0f)
    private val resizeEvent = Events.Window.Resize(this, -1, -1)

    @Subscribe
    fun onInitialize(event: Events.App.Initialized) {
        log.info { "initializing the window: $this" }
        initWindow()
        initCallbacks()
        finalizeWindow()
        presentWindow()
        app.publish(Events.Window.Initialized(this))
    }

    private fun initCallbacks() {
        glfwSetScrollCallback(handle) { _, xOffset, yOffset ->
            mouseScrollEvent.xOffset = xOffset.toFloat()
            mouseScrollEvent.yOffset = yOffset.toFloat()
            app.publish(mouseMoveEvent)
        }

        glfwSetCursorPosCallback(handle) { _, x, y ->
            mouseMoveEvent.x = x.toFloat()
            mouseMoveEvent.y = y.toFloat()
            app.publish(mouseMoveEvent)
        }

        glfwSetKeyCallback(handle) { _, key, scancode, action, mods ->
            keyEvent.key = key
            keyEvent.action = action
            keyEvent.scancode = scancode
            keyEvent.mods = mods
            app.publish(keyEvent)
            when (action) {
                0 -> {
                    keyPressEvent.key = key
                    keyPressEvent.mods = mods
                    app.publish(keyPressEvent)
                }
                1 -> {
                    keyReleaseEvent.key = key
                    keyReleaseEvent.mods = mods
                    app.publish(keyReleaseEvent)
                }
                2 -> {
                    keyRepeatEvent.key = key
                    keyRepeatEvent.mods = mods
                    app.publish(keyRepeatEvent)
                }

            }
        }
        glfwSetMouseButtonCallback(handle) { _, button, action, mods ->
            mouseEvent.button = button
            mouseEvent.action = action
            mouseEvent.mods = mods
            app.publish(mouseEvent)
            when (action) {
                0 -> {
                    mousePressEvent.button = button
                    mousePressEvent.mods = mods
                    app.publish(mousePressEvent)
                }
                1 -> {
                    mouseReleaseEvent.button = button
                    mouseReleaseEvent.mods = mods
                    app.publish(mouseReleaseEvent)
                }
                2 -> {
                    mouseRepeatEvent.button = button
                    mouseRepeatEvent.mods = mods
                    app.publish(mouseRepeatEvent)
                }
            }
        }

        glfwSetFramebufferSizeCallback(handle) { _, width, height ->
            resizeEvent.width = width
            resizeEvent.height = height
            app.publish(resizeEvent)
        }

        glfwSetWindowCloseCallback(handle) { _ ->
            app.publish(Events.Window.Destroy(this))
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
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)
        handle = glfwCreateWindow(width, height, title, NULL, NULL)
        if (handle == NULL)
            throw  RuntimeException("Failed to create the GLFW window");

    }

    override fun swapBuffers() {
        glfwSwapBuffers(handle)
    }

    override fun pollInput() {
        glfwPollEvents()
    }

    @Subscribe
    fun onClose(event: Events.App.Shutdown) {
        glfwDestroyWindow(handle)
    }

}