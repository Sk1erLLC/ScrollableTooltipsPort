pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
    }
    plugins {
        val egtVersion = "0.6.7"
        id("gg.essential.multi-version.root") version egtVersion
    }
}

listOf(
    "1.8.9-forge",
    "1.12.2-forge",
    "1.16.2-forge",
    "1.16.2-fabric",
    "1.17.1-fabric",
    "1.17.1-forge",
    "1.18.1-fabric",
    "1.19-fabric",
    "1.19.2-fabric",
    "1.19.2-forge",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}

rootProject.buildFileName = "root.gradle.kts"