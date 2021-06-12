package marx.sandbox.layer

import marx.engine.events.*
import marx.engine.events.Events.App.Update
import marx.engine.layer.*
import mu.*
import org.lwjgl.opengl.GL11.*
import org.slf4j.*

/**
 * This layer is used for debugging purposes
 */
class LayerDebug() : Layer("debug-layer") {
    private val log: Logger = KotlinLogging.logger { }

    override fun onUpdate(update: Update) {

    }

    override fun onEvent(event: Event) {

    }


}