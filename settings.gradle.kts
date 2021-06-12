rootProject.name = "marx"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.5.10"
        kotlin("plugin.serialization") version "1.5.10"
    }
}

rootDir.walk().forEach {
    if (it.list { _, name -> name.endsWith(".toml") }?.isNotEmpty() == true) {
        val root = rootDir.path
        val name = it.path.replace(root, "").replace("\\", ":")
        include(name)
    }
}


