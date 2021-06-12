package marx.plugin.registry

import org.gradle.api.*
import marx.plugin.urn.Urn
import marx.plugin.MarxPlugin
import org.gradle.kotlin.dsl.*
import marx.plugin.data.ModuleFile
import java.io.*
import marx.plugin.utils.*

class MarxRegistry : Plugin<Project> {
    /**
     * Apply this plugin to the given target object.
     *
     * @param project The target object
     */
    override fun apply(project: Project) {
        project.subprojects {
            var modFile: ModuleFile? = null
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
            modFile?.mapGlobals(registryFor(this).globalExtensions)
            apply(plugin = "kotlin")
            apply<MarxPlugin>()
        }
    }

    data class Registry(val urns: MutableMap<Urn, Project> = HashMap()) {
        val isEmpty: Boolean = urns.isEmpty()
        val globalExtensions: MutableMap<String, String> = HashMap()

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
