import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`

    // Apply the application plugin to add support for building a jar
    java
    id("org.jetbrains.dokka") version "1.5.31"
}

repositories {
    // Use mavenCentral
    mavenCentral()

    maven(url = "https://jitpack.io")
    maven(url = "https://repo.spongepowered.org/maven")
    maven(url = "https://repo.minestom.com/repository/maven-public/")
    maven(url = "https://repo.velocitypowered.com/snapshots/")
}
dependencies {
    // Align versions of all Kotlin components
    compileOnly(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    compileOnly(kotlin("stdlib"))

    // Use the Kotlin reflect library.
    compileOnly(kotlin("reflect"))

    // Compile Minestom into project
    compileOnly("com.github.Minestom", "Minestom", "7867313290")

    // KStom
    compileOnly("com.github.Project-Cepi", "KStom","82f7000079")

    // import kotlinx serialization
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // add Particable
    compileOnly("com.github.Project-Cepi:Particable:acea414be2")

    // add EnergyExtension
    compileOnly("com.github.Project-Cepi:EnergyExtension:c856364913")

    // Kepi
    compileOnly("com.github.Project-Cepi:Kepi:3d57f76a43")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        minimize()

    }

    processResources {
        filesMatching("META-INF/extension.json") {
            expand(project.properties)
        }
    }

    test { useJUnitPlatform() }

    build {
        dependsOn(shadowJar)
    }

}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
