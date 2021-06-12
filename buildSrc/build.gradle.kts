plugins {
    `java-gradle-plugin`
    `kotlin-dsl` version "2.1.4"
    kotlin("plugin.serialization") version "1.5.10"
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.tomlj:tomlj:1.0.0")
}