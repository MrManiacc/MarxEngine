package marx.plugin.data

import org.gradle.api.*
import org.gradle.kotlin.dsl.*

/**
 * This object will take in a module file and map it accordingly
 */
object ModuleManager {
    internal const val ModuleFileName = "modFile"

    /*This will check to see if the module has a mod.toml file.*/
    fun hasModule(project: Project): Boolean =
        project.extra.has(ModuleFileName)

//    /**
//     * This will first first attempt to retrieve a given module file via the extra, if not found it will
//     * then be forced to make it.
//     */
//    fun makeModule(project: Project): ModuleFile {
//
//    }

}