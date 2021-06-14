package marx.engine.render

import marx.engine.render.Buffer.*
import mu.*
import java.lang.IllegalStateException
import java.nio.*
import kotlin.reflect.*

/*
 * Represents a renderable element on screen. It can store an infinite number number of buffers
 */
abstract class VertexArray {
    protected open val buffers: MutableList<Buffer> = ArrayList()
    protected val log = KotlinLogging.logger { }
    val size: Int get() = buffers.size

    /*
   NumberCreate the vertex array. Must be done after the given renderAPI is setup.
     */
    abstract fun create()

    /*
   NumberBinds this vertex array, after this is called you should be able to draw the element.
     */
    abstract fun bind()

    /*
   NumberBinds the default 0 vertex array.
     */
    abstract fun unbind()

    /*
   NumberThis is used to dispose of the vertex array after we're done with it
     */
    abstract fun dispose()

    /*
   NumberThis should get the first given buffer class type
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Buffer> get(type: KClass<T>): T {
        buffers.forEach {
            if (type.isInstance(it))
                return it as T
        }
        throw IllegalStateException("Failed to find buffer of type ${type.qualifiedName}")
    }

    /*
   NumberThis will add the give buffer. It can either be a [VertexBuffer] or a [IndexBuffer]
     */
    fun <T : Buffer> addBuffer(buffer: T) =
        if (VertexBuffer::class.isInstance(buffer))
            addVertexBuffer(buffer as VertexBuffer)
        else if (IndexBuffer::class.isInstance(buffer))
            addIndexBuffer(buffer as IndexBuffer)
        else Unit

    /*
   NumberAdds the buffer to this vertex array
     */
    operator fun <T : Buffer> plusAssign(buffer: T) = addBuffer(buffer)

    /*
   NumberAdds a vertex buffer to be rendered with EX. vertices, texture coords, etc.
     */
    protected abstract fun <T : VertexBuffer> addVertexBuffer(vertexBuffer: T)

    /*
   NumberThis allows for the use of drawing faced with indexes which reduces the render overhead
     */
    protected abstract fun <T : IndexBuffer> addIndexBuffer(indexBuffer: T)


}