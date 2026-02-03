import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "it.ethereallabs"
version = "1.0.3"

repositories {
    mavenCentral()
    maven {
        name = "hMReleases"
        url = uri("https://maven.hytale-mods.dev/releases")
    }
    maven {
        name = "hytale-release"
        url = uri("https://maven.hytale.com/release")
    }
    maven {
        name = "hytale-pre-release"
        url = uri("https://maven.hytale.com/pre-release")
    }
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.01.22-6f8bdbdc4")
    compileOnly("com.buuz135:MultipleHUD:1.0.3")
}

tasks.processResources {
    filesMatching("manifest.json") {
        expand(
            "version" to project.version,
            "name" to project.name
        )
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
    }
}

tasks.jar {
    archiveClassifier.set("plain")
}

val env = Properties().apply {
    val envFile = file(".env")
    if (envFile.exists()) {
        load(FileInputStream(envFile))
    }
}

val modsPath = env.getProperty("HYTALE_MODS_DIR") ?: "build/libs"

tasks.shadowJar {
    archiveClassifier.set("")
    destinationDirectory.set(file(modsPath))
}