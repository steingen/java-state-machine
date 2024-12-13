plugins {
    java
    id("io.freefair.lombok")
}


repositories {
    mavenCentral()
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.jar {
    // Creates a MANIFEST.MF file.
    // The alternative way is to manually create the file within the resources/META-INF folder
    manifest {
        attributes(
            // Add the JPMS module name as a property of the manifest
            "Automatic-Module-Name" to "$group.statemachine"
        )
    }
}

tasks.processResources {
    // Needed especially in this project because the proto files are in the build folder are copied twice,
    // first time in the generateProto task and second time in the processResources task.
    // If you don't exclude duplicates, then the processResources task fails because it finds the protos already there
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    //-parameters compiler argument is needed so that method parameter names can be compiled into the app
    // as opposed to the arbitrary 'arg0', 'arg1'... names.
    //This is useful so that we can get the argument name when throwing ConstraintViolationException exceptions
    //so that we can give a more meaningful error message to the user
    options.compilerArgs.add("-parameters")
    //enable compilation in a separate daemon process
    options.isFork = true
}

// Since this library is included as a jar in the consuming projects, we want the
// jar to be built reproducibly. For more on Reproducible builds, refer to:
// https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}