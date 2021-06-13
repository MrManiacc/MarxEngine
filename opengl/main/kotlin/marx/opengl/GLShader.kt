package marx.opengl

import marx.engine.*
import marx.engine.events.Events.Shader.*
import marx.engine.render.*
import org.joml.*
import org.lwjgl.opengl.GL20.*

/**
 * This is the shader implemented with opengl
 */
class GLShader(val app: Application<*>) : Shader() {
    private var vertexShader: Int = -1
    private var fragmentShader: Int = -1
    private var shaderProgram: Int = -1

    override val isValid: Boolean
        get() = vertexShader != -1 && fragmentShader != -1 && shaderProgram != -1

    /***
     * This should compile the shader.
     */
    override fun compile(sources: ShaderProgram): Boolean {
        val shaders = sources.sources
        val results: Array<CompileResult> = Array(shaders.size) { CompileResult(shaders[it].type) }
        var valid = true
        for (idx in shaders.indices) {
            val source = shaders[idx]
            val result = results[idx]
            when (source.type) {
                Type.Vertex -> {
                    vertexShader = compileShader(source.source, GL_VERTEX_SHADER, result)
                    app.publish(Compiled(this, result))
                }
                Type.Fragment -> {
                    fragmentShader = compileShader(source.source, GL_FRAGMENT_SHADER, result)
                    app.publish(Compiled(this, result))
                }
                else -> {
                    result.state = CompileState.Error
                    result.message = "Unsupported shader type: ${source.type.name}"
                    app.publish(Compiled(this, result))
                    valid = false
                }
            }
            if (!result.isValid) valid = false
        }
        val programResult = createProgram()
        app.publish(Compiled(this, programResult))
        return valid && isValid && programResult.isValid
    }


    private fun compileShader(source: String, type: Int, result: CompileResult): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, source)
        glCompileShader(shader)
        val compiled = IntArray(1)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compiled)
        val length = IntArray(1)
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, length)
        if (compiled[0] == GL_FALSE) {
            result.message = glGetShaderInfoLog(shader, length[0])
            result.state = CompileState.Error
            return -1
        }
        result.message = "Successfully compiled shader"
        result.state = CompileState.Compiled
        return shader
    }

    /**
     * Compiles the shaders and links them. Will return the result along with the message
     */
    private fun createProgram(): CompileResult {
        shaderProgram = glCreateProgram()
        glAttachShader(shaderProgram, vertexShader)
        glAttachShader(shaderProgram, fragmentShader)
        glLinkProgram(shaderProgram)
        val linked = IntArray(1)
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, linked)
        if (linked[0] == GL_FALSE) {
            val result = CompileResult(Type.Program)
            val length = IntArray(1)
            glGetShaderiv(shaderProgram, GL_INFO_LOG_LENGTH, length)
            result.state = CompileState.Error
            result.message = glGetProgramInfoLog(shaderProgram, length[0])
            glDeleteProgram(shaderProgram)
            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            return result
        }
        glDetachShader(shaderProgram, vertexShader)
        glDetachShader(shaderProgram, fragmentShader)
        return CompileResult(
            Type.Program,
            CompileState.Compiled,
            "Created and linked shader program with vertex and fragment shaders!"
        )
    }

    /**
     * This should destroy the shader program. Called upon closing of a layer or the window/app
     */
    override fun destroy() {
        glDeleteProgram(shaderProgram)
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
        shaderProgram = -1
        vertexShader = -1
        fragmentShader = -1
    }

    /**
     * Bind the shader for writing to
     */
    override fun bind() =
        glUseProgram(shaderProgram)

    /**
     * Unbind the shader, called when we are done writing.
     */
    override fun unbind() =
        glUseProgram(0)

    /**This should update a vec4 to the shader**/
    override fun updateVec4(uniform: String, vector: Vector4f) {
        TODO("Not yet implemented")
    }

    /**This should update a vec3 to the shader**/
    override fun updateVec3(uniform: String, vector: Vector3f) {
        TODO("Not yet implemented")
    }

    /**This should update a vec2 to the shader**/
    override fun updateVec3(uniform: String, vector: Vector2f) {
        TODO("Not yet implemented")
    }

    /**This should update a float to the shader**/
    override fun uploadFloat(uniform: String, float: Float) {
        TODO("Not yet implemented")
    }

    /**This should update a float to the shader**/
    override fun uploadMat4(uniform: String, matrix: Matrix4f) {
        TODO("Not yet implemented")
    }

    /**This should update a float to the shader**/
    override fun uploadMat3(uniform: String, matrix: Matrix3f) {
        TODO("Not yet implemented")
    }
}