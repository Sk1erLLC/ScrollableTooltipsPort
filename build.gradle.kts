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

    if (project.platform.isLegacyForge) {
        runConfigs {
            "client" {
                programArgs("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
                programArgs("--mixin", "mixins.scrollabletooltips.json")
                property("mixin.debug.export", "true")
                property("mixin.debug.verbose", "true")
                property("mixin.dumpTargetOnFailure", "true")
            }
        }
    }

    if (project.platform.isForge) {
        forge {
            mixinConfig("mixins.scrollabletooltips.json")
        }
    }

    mixin.defaultRefmapName.set("mixins.scrollabletooltips.refmap.json")
}

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.essential.gg/repository/maven-public")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val embed by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    if (project.platform.isFabric) {
        implementation(include("gg.essential:vigilance:306")!!)
        modImplementation(include("gg.essential:universalcraft-${platform.mcVersionStr}-fabric:401")!!)
    } else if (project.platform.isForge || project.platform.isLegacyForge) {
        embed("gg.essential:vigilance:306")!!
        embed("gg.essential:universalcraft-${platform.mcVersionStr}-forge:401")!!
    }

    if (project.platform.isLegacyForge) {
        embed("org.spongepowered:mixin:0.7.11-SNAPSHOT")
    }

    val devAuthPlatform = when {
        platform.isFabric -> "fabric"
        platform.isLegacyForge -> "forge-legacy"
        platform.isForge -> "forge-latest"
        else -> error("Unable to determine platform")
    }

    modLocalRuntime("me.djtheredstoner:DevAuth-${devAuthPlatform}:1.2.1")
}

tasks {
    jar {
        from(embed.files.map { zipTree(it) })

        manifest.attributes(
            mapOf(
                "ModSide" to "CLIENT",
                "FMLCorePluginContainsFMLMod" to "Yes, yes it does",
                "MixinConfigs" to "mixins.scrollabletooltips.json",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "TweakOrder" to "0"
            )
        )
    }
}