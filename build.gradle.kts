import gg.essential.gradle.util.noServerRunConfigs

plugins {
    kotlin("jvm")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

val modGroup: String by project
val modBaseName: String by project
group = modGroup
base.archivesName.set("$modBaseName-${platform.mcVersionStr}-${platform.loaderStr}")

loom {
    noServerRunConfigs()
}

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.essential.gg/repository/maven-public")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    implementation(include("gg.essential:vigilance:306")!!)

    val ucPlatform = when {
        platform.isFabric -> "fabric"
        platform.isForge -> "forge"
        platform.isNeoForge -> "neoforge"
        else -> error("Unable to determine platform")
    }
    modImplementation(include("gg.essential:universalcraft-${platform.mcVersionStr}-${ucPlatform}:401")!!)

    if (project.platform.isForge) {
        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
        implementation(include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
    }

    val devAuthPlatform = when {
        platform.isFabric -> "fabric"
        platform.isForge -> "forge-latest"
        platform.isNeoForge -> "neoforge"
        else -> error("Unable to determine platform")
    }

    modLocalRuntime("me.djtheredstoner:DevAuth-${devAuthPlatform}:1.2.1")
}