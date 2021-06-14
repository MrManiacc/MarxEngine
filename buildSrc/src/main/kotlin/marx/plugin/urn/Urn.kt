package marx.plugin.urn

import marx.plugin.data.*
import org.gradle.api.*
import org.gradle.kotlin.dsl.*

data class Urn(
    val scheme: String = "",
    val name: String = "engine",
    val path: String = "/",
    val fragment: String? = null,
) {
    var isEmpty = false
        private set

    override fun toString(): String =
        if (fragment != null) "$scheme:$name$path#$fragment"
        else "$scheme:$name$path"

    companion object {
        val EMPTY = Urn("", "", "", null).apply { isEmpty = true }

        /*
       NumberCreates a urn from the given string by parsing it.
         */
        fun of(string: String): Urn {
            val scheme = string.substringBefore(":")
            val name = string.substring(string.indexOf(":") + 1, string.indexOf("/"))
            val path = string.substring(string.indexOf("/"))
            return Urn(scheme, name, path)

        }

        /*
       NumberThis will create a urn for the given project if it doesn't exist
         */
        fun of(project: Project): Urn {
            if (project.extra.has("urn")) {
                val urn = project.extra["urn"]
                if (urn is Urn) return urn
            } else {
                val moduleFile = ModuleFile(project)
                moduleFile.map()
                project.extra["modFile"] = moduleFile
                return of(moduleFile)

            }
            return EMPTY
        }

        fun of(mod: ModuleFile): Urn = Urn("mod", mod.id)

    }


}