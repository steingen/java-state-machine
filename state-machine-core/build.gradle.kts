plugins {
    id("app-base")
    id("test-suites")
    `maven-publish`
}

group = "io.github.steingen"
version = "1.0-SNAPSHOT"


publishing {
    publications {
        create<MavenPublication>("maven"){
            from(components["java"])

            pom {
                name.set("Java State Machine")
            }
        }

        //Alternative way: Using Shadow-jar plugin. It creates a fat jar and publishes it.
        //If you use this, you'll have to import transitive dependencies separately in the consuming project,
        // that's because the fat jar does not expose this library's dependencies to consuming projects
        /*val publication = create<MavenPublication>("shadow")
        project.shadow.component(publication)*/
    }
    repositories {
        maven {
            credentials {
                username = "$usr"
                password = "$pwd"
            }

            url = uri("https://maven.pkg.jetbrains.space/mycompany/p/projectkey/my-maven-repo")
        }
    }
}

dependencies {
    implementation(platform(springboot.bom))

    implementation("jakarta.annotation:jakarta.annotation-api")
}

