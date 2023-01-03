import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

allprojects {
    group = "io.github.x.tractor"
    version = System.getenv("VERSION") ?: "DEV-SNAPSHOT"
    description = "An application property generator framework that validates your property values on runtime."
    repositories(RepositoryHandler::mavenCentral)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(rootProject.libs.mockk)
    testImplementation(rootProject.libs.junit.jupiter)
    testImplementation(rootProject.libs.kotest.assertions.core)
    testImplementation(rootProject.libs.kotest.runner.junit5)

}

tasks {
    test {
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = VERSION_17.toString()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}