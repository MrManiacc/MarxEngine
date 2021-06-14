import marx.plugin.*
import marx.plugin.registry.*

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.5.10"
}

/**We want the marx plugin to be applied to only the root.
 * This allows us to control all of the child marx plugins**/
apply<MarxRegistry>()
repositories {
    mavenCentral()

}
group = "marx.plugin"
version = "1.0-SNAPSHOT"

subprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io") // should be the last entry
    }
    dependencies {
        testImplementation("io.mockk:mockk:1.11.0")
        testImplementation("org.assertj:assertj-core:3.19.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

}