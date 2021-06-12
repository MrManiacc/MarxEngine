import marx.plugin.*
import marx.plugin.registry.*

/**We want the marx plugin to be applied to only the root.
 * This allows us to control all of the child marx plugins**/
apply<MarxRegistry>()

group = "marx.plugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
    }
}