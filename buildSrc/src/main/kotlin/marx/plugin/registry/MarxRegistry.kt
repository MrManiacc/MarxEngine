package marx.plugin.registry

import org.gradle.api.*
import marx.plugin.urn.Urn
import marx.plugin.MarxPlugin
import org.gradle.kotlin.dsl.*
import marx.plugin.data.ModuleFile
import java.io.*

class MarxRegistry : Plugin<Project> {
    /**
     * Apply this plugin to the given target object.
     *
     * @param project The target object
     */
    override fun apply(project: Project) {
        val registry = Registry()
        project.subprojects {
            val modFile: ModuleFile?
            if (!extra.has("modFile")) {
                val file = File(projectDir, "mod.toml")
                if (file.exists()) {
                    modFile = ModuleFile(this).apply { map() }
                    extra["modFile"] = modFile
                }
            } else {
                val moduleFile = extra["modFile"]
                if (moduleFile is ModuleFile) {
                    moduleFile.map()
                    modFile = moduleFile
                }
            }
        }
        project.subprojects {
            apply(plugin = "kotlin")
            apply<MarxPlugin>()
        }
    }

    data class Registry(val urns: MutableMap<Urn, Project> = HashMap()) {
        val isEmpty: Boolean = urns.isEmpty()

        fun has(urn: Urn): Boolean = urns.containsKey(urn)

        operator fun get(urn: Urn): Project? = urns[urn]

        operator fun plusAssign(moduleFile: ModuleFile) {
            val urn = Urn.of(moduleFile)
            if (!urn.isEmpty) urns[urn] = moduleFile.project
        }

        operator fun plusAssign(project: Project) {
            val urn = Urn.of(project)
            if (!urn.isEmpty) urns[urn] = project
        }

        operator fun minusAssign(urn: Urn) {
            if (urns.containsKey(urn)) urns.remove(urn)
        }

        operator fun minusAssign(project: Project) {
            val urn = Urn.of(project)
            if (!urn.isEmpty)
                if (urns.containsKey(urn))
                    urns.remove(urn)
        }

        override fun toString(): String {
            return "Registry(modules=$urns)"
        }


    }

    companion object {
        val cachedRegistry: Registry = Registry()
    }
}
