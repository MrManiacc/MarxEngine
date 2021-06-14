rootProject.name = "marx"


rootDir.walk().forEach {
    if (it.list { _, name -> name.endsWith(".toml") }?.isNotEmpty() == true) {
        val root = rootDir.path
        include(it.path.replace(root, "").replace("\\", ":").replace("/", ":"))
    }
}


