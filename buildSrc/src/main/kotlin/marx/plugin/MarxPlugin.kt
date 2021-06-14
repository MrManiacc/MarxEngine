package marx.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import marx.plugin.data.ModuleFile
import org.gradle.jvm.tasks.*

/*
 * This configures all of the modules for a quicker build.
 */
class MarxPlugin : Plugin<Project> {
    //    private  var modFile: ModuleFile

    private fun getModFile(project: Project): ModuleFile {
        return if (project.extra.has("modFile"))
            project.extra["modFile"] as ModuleFile
        else {
            val modFile = ModuleFile(project) //TODO: needs map
            project.extra["modFile"] = modFile
            modFile
        }
    }

    private fun forEach(
        input: String,
        callback: (string: String) -> Unit
    ) {
        if (!input.contains(",")) callback(input)
        else {
            val split = input.split(",")
            split.forEach {
                callback(it.trim())
            }
        }
    }

    /*
   NumberApply this plugin to the given target object.
     *
   Number@param target The target object
     */
    override fun apply(target: Project) {
        target.beforeEvaluate {
            preEval(target)

        }
        val modFile = getModFile(target)

        target.afterEvaluate {
            modFile.postMap()
            target.group = modFile.id
            target.version = modFile.version

            modFile.repos.forEach {
                forEach(it) { str ->
                    target.repositories.maven(str)
                }
            }

            modFile.platforms.forEach {
                forEach(it) { str ->
                    target.configurations.getByName("compile").dependencies.add(
                        project.dependencies.platform(str)
                    )
                }

            }

            modFile.implements.forEach {
                forEach(it) { str ->
                    target.configurations.getByName("implementation").dependencies.add(
                        target.dependencies.add("implementation", str)
                    )
                }
            }

            modFile.testImplements.forEach {
                forEach(it) { str ->
                    target.configurations.getByName("testImplementation").dependencies.add(
                        target.dependencies.add("testImplementation", str)
                    )
                }
            }

            modFile.runtimes.forEach {
                forEach(it) { str ->
                    target.configurations.getByName("runtime").dependencies.add(
                        target.dependencies.add("runtime", str)
                    )
                }
            }

            modFile.imports.forEach { (_, project) ->
                target.configurations.getByName("compile").dependencies.add(
                    target.dependencies.project(project.path)
                )
            }
            val mainSpec = target.the<SourceSetContainer>().named("main").get()

            if (modFile.isRunnable) {
                target.tasks.create("export", Jar::class) {
                    group = "marx"
                    manifest {
                        attributes(
                            "Implementation-Title" to modFile.id,
                            "Implementation-Version" to modFile.version,
                            "Main-Class" to modFile.entryPoint
                        )
                    }
                    baseName = "${target.name}-all"
                    from(mainSpec.runtimeClasspath)
                    //                    with(mainSpec.output)
                }
                target.tasks.create("execute", JavaExec::class) {
                    group = "marx"
                    main = modFile.entryPoint
                    classpath = mainSpec.runtimeClasspath
                }
            }
        }
    }

    private fun preEval(target: Project) {
        val modFile = getModFile(target)
        modFile.map()
        val main = target.the<SourceSetContainer>().named("main").get()
        main.java.setSrcDirs(modFile.mainSources)
        main.resources.setSrcDirs(modFile.mainAssets)

        val test = target.the<SourceSetContainer>().named("test").get()
        test.java.setSrcDirs(modFile.testSources)
        test.resources.setSrcDirs(modFile.testAssets)
        test.runtimeClasspath += main.output + test.output

    }


}
