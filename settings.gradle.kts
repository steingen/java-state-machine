rootProject.name = "state-machine"

include("state-machine-core")
include("state-machine-demo")

dependencyResolutionManagement {
    versionCatalogs {
        create("springboot") {
            library("bom", "org.springframework.boot:spring-boot-dependencies:3.4.0")
        }
    }
}
