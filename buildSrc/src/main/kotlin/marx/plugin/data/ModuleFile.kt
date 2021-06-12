package marx.plugin.data

import org.gradle.api.*
import org.tomlj.*
import java.io.*
import java.lang.StringBuilder
import marx.plugin.utils.*
import marx.plugin.urn.Urn
import org.gradle.internal.os.*

/**
 * This will parse out all of the data associated with a mod.toml file
 */
class ModuleFile(val project: Project) {
    private val parsed: TomlParseResult = Toml.parse(File(project.projectDir, "mod.toml").toPath())
    val isValid: Boolean = !parsed.hasErrors()
    val errorMessage: String =
        with(StringBuilder()) { parsed.errors().forEach { append(it.message.toString()).append("\n") } }.toString()
    var id: String = parsed.getString("main.id") ?: "undefined"
        private set
    var version: String = parsed.getString("main.version") ?: "undefined"
        private set
    var sources: List<String> = parsed.getStringList("main.srcDirs")
        private set
    var assets: List<String> = parsed.getStringList("main.resDirs")
        private set
    var repos: List<String> = parsed.getStringList("build.repos")
        private set
    var implements: List<String> = parsed.getStringList("build.implements")
        private set
    var runtimes: List<String> = parsed.getStringList("build.runtimes")
        private set
    var platforms: List<String> = parsed.getStringList("build.platforms")
        private set
    var extensions: MutableMap<String, String> = parsed.getChildMap("ext")
        private set
    var sharedExtensions: MutableMap<String, String> = parsed.getChildMap("ext.shared")
        private set
    private var modules: List<String> = parsed.getStringList("build.modules")
    val imports: MutableMap<Urn, Project> = HashMap()
    val isRunnable: Boolean = parsed.contains("main.entryPoint")
    var entryPoint: String = parsed.getString("main.entryPoint") ?: "undefined"

    val platform: String
        get() = when (OperatingSystem.current()!!) {
            OperatingSystem.WINDOWS -> "windows"
            OperatingSystem.MAC_OS -> "macos"
            else -> "linux"
        }

    /**Allows us to map input values directly with a unit and a string return**/
    private val defaultExtensions: Map<String, () -> String> = mapOf(
        "__project_dir__" to { project.projectDir.path.replace("\\", "/") },
        "__root_dir__" to { project.rootDir.path.replace("\\", "/") },
        "__platform__" to { platform }
    )

    /**
     * This will update all of the variables with the correct extensions
     */
    fun map() {
        mapDefaultExtensions()
        mapExtensions(extensions)
        val registry = registryFor(project)
        registry += this
        mapGlobals(registry.globalExtensions)
    }

    fun postMap() {
        val registry = registryFor(project)
        imports.clear()
        modules.forEach {
            val urn = Urn.of(it)
            if (registry.has(urn))
                imports[urn] = registry[urn]!!
        }
        mapExtensions(registry.globalExtensions)
    }

    private fun mapDefaultExtensions() {
        for (ext in extensions) {
            val extValue = ext.value
            defaultExtensions.forEach { (key, value) ->
                if (extValue.contains(key))
                    extensions[ext.key] = extValue.replace(key, value())
            }
        }
    }

    private fun mapExtensions(map: Map<String, String>) {
        id = mapString(id, map)
        version = mapString(version, map)
        sources = mapStringList(sources, map)
        assets = mapStringList(assets, map)
        repos = mapStringList(repos, map)
        platforms = mapStringList(platforms, map)
        implements = mapStringList(implements, map)
        runtimes = mapStringList(runtimes, map)
        if (isRunnable) this.entryPoint = mapString(entryPoint, map)
    }

    /**
     * This will put all of our shared values in the global map
     */
    fun mapGlobals(map: MutableMap<String, String>) {
        for (entry in this.sharedExtensions) {
            val value = mapString(entry.value, extensions)
            map[entry.key] = value
        }
    }

    private fun mapString(stringIn: String, map: Map<String, String>): String {
        var string = stringIn
        map.forEach { (t, u) ->
            if (string.contains("$$t"))
                string = string.replace("$$t", u)
        }
        return string
    }

    private fun mapStringList(listIn: List<String>, map: Map<String, String>): List<String> {
        val output = ArrayList<String>()
        listIn.forEach {
            if (it.contains(",")) {
                val split = it.split(",")
                println(split)
                split.forEach { instance -> output.add(mapString(instance.trim(), map)) }
            } else output.add(mapString(it, map))
        }
        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModuleFile) return false

        if (isValid != other.isValid) return false
        if (errorMessage != other.errorMessage) return false
        if (id != other.id) return false
        if (version != other.version) return false
        if (sources != other.sources) return false
        if (assets != other.assets) return false
        if (repos != other.repos) return false
        if (implements != other.implements) return false
        if (runtimes != other.runtimes) return false
        if (extensions != other.extensions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isValid.hashCode()
        result = 31 * result + errorMessage.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + sources.hashCode()
        result = 31 * result + assets.hashCode()
        result = 31 * result + repos.hashCode()
        result = 31 * result + implements.hashCode()
        result = 31 * result + runtimes.hashCode()
        result = 31 * result + extensions.hashCode()
        return result
    }

    override fun toString(): String {
        return "ModuleFile(id='$id', version='$version', sources=$sources, assets=$assets, repos=$repos, implements=$implements, runtimes=$runtimes, extensions=$extensions)"
    }


}
