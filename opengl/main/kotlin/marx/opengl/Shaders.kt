package marx.opengl

import marx.engine.render.Shader.*
import org.joml.*

/*
 * Stores a group of named shader sources.
 */
object Shaders {
    /*Our simple shader. Uses version 330 and core by default**/
    val simple: Pair<ShaderSource, ShaderSource> get() = simpleOf("330", true)

    /*
   NumberCompiles a simple shader that has it's version appended based upon what's passed in
     */
    fun colored(version: String, core: Boolean, color: Vector3f): Pair<ShaderSource, ShaderSource> {
        return prefixVersion(
            version, core, ShaderSource(
                Type.Vertex,
                """
                layout(location = 0) in vec3 a_Pos; //Imports the position in 3d space (relative to mesh origin) of this vertex.
                
                uniform mat4 u_ViewProjection; //Calculations done on cpu side
                uniform mat4 u_ModelMatrix;
                
                out vec3 v_Pos;
                
                void main(){
                    gl_Position = u_ViewProjection * u_ModelMatrix * vec4(a_Pos, 1.0);                    
                    v_Pos = a_Pos;         
                }
            """.trimIndent()
            ) to ShaderSource(
                Type.Fragment,
                """
                layout(location = 0) out vec4 color; //Imports the position in 3d space (relative to mesh origin) of this vertex.
                
                in vec3 v_Pos;
                
                void main(){
                    color = vec4(${color.x}, ${color.y} , ${color.z}, 1.0);
                }
            """.trimIndent()
            )
        )
    }

    /*
   NumberCompiles a simple shader that has it's version appended based upon what's passed in
     */
    fun simpleOf(version: String, core: Boolean): Pair<ShaderSource, ShaderSource> {
        return prefixVersion(
            version, core, ShaderSource(
                Type.Vertex,
                """
                layout(location = 0) in vec3 a_Pos; //Imports the position in 3d space (relative to mesh origin) of this vertex.
                
                uniform mat4 u_ViewProjection; //Calculations done on cpu side
                uniform mat4 u_ModelMatrix;
                
                out vec3 v_Pos;
                
                void main(){
                    gl_Position = u_ViewProjection * u_ModelMatrix * vec4(a_Pos, 1.0);                    
                    v_Pos = a_Pos;         
                }
            """.trimIndent()
            ) to ShaderSource(
                Type.Fragment,
                """
                layout(location = 0) out vec4 color; //Imports the position in 3d space (relative to mesh origin) of this vertex.
               
                in vec3 v_Pos;
                
                void main(){
                    color = vec4(v_Pos * 0.5 + 0.45, 1.0);
                }
            """.trimIndent()
            )
        )
    }

    private fun prefixVersion(
        version: String,
        core: Boolean,
        sourceIn: Pair<ShaderSource, ShaderSource>
    ): Pair<ShaderSource, ShaderSource> {
        return ShaderSource(
            sourceIn.first.type,
            "#version $version ${if (core) "core" else ""}\n${sourceIn.first.source}"
        ) to ShaderSource(
            sourceIn.second.type,
            "#version $version ${if (core) "core" else ""}\n${sourceIn.second.source}"
        )
    }

}