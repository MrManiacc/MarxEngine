rootProject.name = "marx"


rootDir.walk().forEach {
    if (it.list { _, name -> name.endsWith(".toml") }?.isNotEmpty() == true) {
        include(it.toRelativeString(rootDir).replace("\\", ":").replace("/", ":"))
    }
}

