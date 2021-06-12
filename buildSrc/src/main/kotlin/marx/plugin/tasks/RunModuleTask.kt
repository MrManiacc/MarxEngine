package marx.plugin.tasks

import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import marx.plugin.data.ModuleFile

open class RunModuleTask : JavaExec() {
    init {
        group = "marx"
        if (project.extra.has("modFile")) {
            val modFile = project.extra["modFile"] as ModuleFile
            val sourceSets: SourceSetContainer = project.extensions.getByType(SourceSetContainer::class.java)
            val mainSourceSet: SourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            if (modFile.isRunnable) {
                configure<JavaExec> {
                    main = modFile.entryPoint
                    classpath = mainSourceSet.runtimeClasspath
                }
            }
        }
    }
}
