package marx.plugin.data

import org.gradle.api.*
import org.tomlj.*
import java.io.*
import java.lang.StringBuilder
import marx.plugin.utils.*
import marx.plugin.urn.Urn
import org.gradle.internal.os.*
import java.lang.Exception
import java.util.function.*

/**
 * This will parse out all of the data associated with a mod.toml file
 */
class ModuleFile(val project: Project) {
    private var parsed: TomlParseResult? = try {
        Toml.parse(File(project.projectDir, "mod.toml").toPath())
    } catch (ex: Exception) {
        project.logger.warn("Couldn't find module file for ${project.name}, this is likely because it's a library")
        null
    }

    /**
     * @return true when [parsed] is invalid. This means we have child projects
     */
    val isLibrary: Boolean = parsed == null &&
            project.projectDir.containsRecursive { it.endsWith(".toml") }

    val isValid: Boolean = (parsed?.hasErrors() == false) || isLibrary

    val errorMessage: String =
        with(StringBuilder()) {
            if (!isLibrary)
                parsed?.errors()?.forEach { append(it.message.toString()).append("\n") }
            else append("we are a library (contains child module with mod.toml)").append("\n")
        }.toString()
    var id: String = parsed?.getString("main.id") ?: "undefined"
        private set
    var version: String = parsed?.getString("main.version") ?: "undefined"
        private set
    var mainSources: List<String> = parsed?.getStringList("build.main.sources") ?: emptyList()
        private set
    var mainAssets: List<String> = parsed?.getStringList("build.main.assets") ?: emptyList()
        private set
    var testSources: List<String> = parsed?.getStringList("build.test.sources") ?: emptyList()
        private set
    var testAssets: List<String> = parsed?.getStringList("build.test.assets") ?: emptyList()
        private set
    var repos: List<String> = parsed?.getStringList("build.repos") ?: emptyList()
        private set
    var implements: List<String> = parsed?.getStringList("build.implements") ?: emptyList()
        private set
    var testImplements: List<String> = parsed?.getStringList("build.testImplements") ?: emptyList()
        private set
    var runtimes: List<String> = parsed?.getStringList("build.runtimes") ?: emptyList()
        private set
    var platforms: List<String> = parsed?.getStringList("build.platforms") ?: emptyList()
        private set
    var extensions: MutableMap<String, String> = parsed?.getChildMap("ext") ?: HashMap()
        private set
    var sharedExtensions: MutableMap<String, String> = parsed?.getChildMap("ext.shared") ?: HashMap()
        private set
    private var modules: List<String> = parsed?.getStringList("build.modules") ?: emptyList()
    val imports: MutableMap<Urn, Project> = HashMap()
    val isRunnable: Boolean = parsed?.contains("main.entryPoint") ?: false
    var entryPoint: String = parsed?.getString("main.entryPoint") ?: "undefined"

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
     * This will take a given file and recursively apply the predicate. This allows for
     * recursive matching of a given predicate
     */
    private fun File.containsRecursive(predicate: Predicate<File>): Boolean = findRecursive(predicate) != null

    /**
     * This will take a given file and recursively apply the predicate. This allows for
     * recursive matching of a given predicate
     */
    private fun File.findRecursive(predicate: Predicate<File>): File? {
        if (this.isFile && predicate.test(this)) return this
        val children = this.listFiles() ?: return null
        for (child in children)
            if (child.containsRecursive(predicate))
                return child
        return null
    }

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
        mainSources = mapStringList(mainSources, map)
        mainAssets = mapStringList(mainAssets, map)
        testAssets = mapStringList(testAssets, map)
        testSources = mapStringList(testSources, map)
        repos = mapStringList(repos, map)
        platforms = mapStringList(platforms, map)
        implements = mapStringList(implements, map)
        testImplements = mapStringList(testImplements, map)
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

    private fun mapString(
        stringIn: String,
        map: Map<String, String>
    ): String {
        var string = stringIn
        map.forEach { (t, u) ->
            if (string.contains("$$t"))
                string = string.replace("$$t", u)
        }
        return string
    }

    private fun mapStringList(
        listIn: List<String>,
        map: Map<String, String>
    ): List<String> {
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
        if (mainSources != other.mainSources) return false
        if (mainAssets != other.mainAssets) return false
        if (testAssets != other.testAssets) return false
        if (testSources != other.testSources) return false
        if (repos != other.repos) return false
        if (implements != other.implements) return false
        if (testImplements != other.testImplements) return false
        if (runtimes != other.runtimes) return false
        if (extensions != other.extensions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isValid.hashCode()
        result = 31 * result + errorMessage.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + mainSources.hashCode()
        result = 31 * result + mainAssets.hashCode()
        result = 31 * result + testAssets.hashCode()
        result = 31 * result + testSources.hashCode()
        result = 31 * result + repos.hashCode()
        result = 31 * result + implements.hashCode()
        result = 31 * result + testImplements.hashCode()
        result = 31 * result + runtimes.hashCode()
        result = 31 * result + extensions.hashCode()
        return result
    }

    override fun toString(): String {
        return "ModuleFile(id='$id', version='$version', sources=$mainSources, assets=$mainAssets, tests=$testAssets, repos=$repos, implements=$implements, runtimes=$runtimes, extensions=$extensions)"
    }


}
