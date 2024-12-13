import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    jacoco
    `jvm-test-suite`
}

configurations.all {
    // Some libraries leak junit4 into the project.
    // We don't want it to conflict with jUnit5 that we are using because the jUnit4/5 import statements
    // are very confusing if you have them both in your project.
    // Besides, if you use some jUnit4 annotations while your project uses jUnit5,
    // your tests will fail with this exception:
    // Caused by: org.gradle.api.tasks.testing.TestExecutionException:
    //  No matching tests found in any candidate test task.
    exclude(group = "junit", module = "junit")
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            testType.set(TestSuiteType.UNIT_TEST)

            targets{
                all {
                    testTask.configure{
                        finalizedBy(tasks.jacocoTestReport)
                    }
                }
            }
            sources {
                java {
                    setSrcDirs(listOf("src/test/java"))
                }
            }
        }
        register<JvmTestSuite>("integrationTest") {
            testType.set(TestSuiteType.INTEGRATION_TEST)

            dependencies {
                implementation(project())
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
            sources {
                java {
                    setSrcDirs(listOf("src/test/integration-test/java"))
                }
                resources{
                    srcDirs("src/test/integration-test/resources")
                }
            }
        }

        configureEach {
            if (this is JvmTestSuite) {
                useJUnitJupiter()
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks.withType<Test> {
    // Enables running of tests in parallel
    // See: https://docs.gradle.org/current/userguide/performance.html#execute_tests_in_parallel
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }

    // Disable test reports. See https://docs.gradle.org/current/userguide/performance.html#disable_reports
    reports {
        html.required.set(true)
        junitXml.required.set(false)
    }
}

// this has to be here after the testing section otherwise it is not recognised
configurations {
    configurations.getByName("integrationTestImplementation").apply {
        extendsFrom(configurations.compileOnly.get(), configurations.testImplementation.get())
    }
}
