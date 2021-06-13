package marx.engine.render.scene

import marx.engine.render.*
import marx.engine.render.camera.*

/**
 * This allows us to render a scene. It is platform agnostic
 */
interface RenderScene {
    val renderAPI: RenderAPI

    fun sceneOf(camera: Camera<*>, body: RenderScene.() -> Unit) {
        beginScene(camera)
        this.body()
        endScene()
    }

    /**
     * This will start a new scene
     */
    fun beginScene(camera: Camera<*>)

    /**
     * This method should be overloaded for all of the various types of things we can submit
     */
    fun submit(array: VertexArray)

    /**
     * This method should be overloaded for all of the various types of things we can submit
     */
    fun submit(array: VertexArray, shader: Shader)

    /**
     * This clears our all of objects or entities on the scene
     */
    fun flush()

    /**
     * Ends the current scene, renders all of the submitted meshes
     */
    fun endScene()


}
