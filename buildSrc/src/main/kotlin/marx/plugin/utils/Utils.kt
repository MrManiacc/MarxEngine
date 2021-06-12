package marx.plugin.utils

import marx.plugin.*
import marx.plugin.registry.*
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.tomlj.*

fun TomlParseResult.getChildMap(key: String): MutableMap<String, String> {
    val array = getTable(key) ?: return mutableMapOf()
    val map = HashMap<String, String>()
    array.keySet().forEach {
        val value = array.getString(it)
        if (value != null)
            map[it] = value
    }
    return map
}

fun TomlParseResult.getStringList(key: String): List<String> {
    val array = getArray(key) ?: return emptyList()
    if (!array.containsStrings() || array.isEmpty) return emptyList()
    val output = ArrayList<String>()
    for (i in 0 until array.size())
        output.add(array.getString(i))
    return output
}

fun registryFor(project: Project): MarxRegistry.Registry =
    if (!project.rootProject.extra.has("registry")) MarxRegistry.cachedRegistry //Empty
    else project.rootProject.extra["registry"] as MarxRegistry.Registry



