package marx.engine.layer

import com.google.common.collect.*
import marx.engine.events.*

interface LayerStack : Collection<Layer> {
    val layers: MutableList<Layer>
    var insertIndex: Int

    //Pushes layers to the front/top of the list
    fun pushLayer(layer: Layer) = layers.add(insertIndex++, layer).also { layer.onAttach() }

    //Pushes layers to the back, as an overlay
    fun pushOverlay(layer: Layer) = layers.add(layers.size - 1, layer).also { layer.onAttach() }

    /**
     * This will find the layer's index and remove it,
     * then decrement the [insertIndex]
     */
    fun popLayer(layer: Layer) {
        val idx = layers.indexOf(layer)
        if (idx != layers.size) {
            layers.removeAt(idx)
            insertIndex--
            layer.onDetach()
        }
    }

    /**
     * Removes the last layer
     */
    fun popOverlay(layer: Layer) = layers.removeAt(layers.size - 1).also { layer.onDetach() }

    /**
     * Returns the size of the collection.
     */
    override val size: Int
        get() = layers.size

    /**
     * Checks if the specified element is contained in this collection.
     */
    override fun contains(element: Layer): Boolean = layers.contains(element)

    /**
     * Checks if all elements in the specified collection are contained in this collection.
     */
    override fun containsAll(elements: Collection<Layer>): Boolean = layers.containsAll(elements)

    /**
     * Returns `true` if the collection is empty (contains no elements), `false` otherwise.
     */
    override fun isEmpty(): Boolean = layers.isEmpty()

    override fun iterator(): Iterator<Layer> = layers.iterator()
}