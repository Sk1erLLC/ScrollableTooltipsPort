import gg.essential.gradle.util.versionFromBuildIdAndBranch

plugins {
    kotlin("jvm") version "1.9.0" apply false
    id("gg.essential.multi-version.root")
}

version = versionFromBuildIdAndBranch()

preprocess {
    strictExtraMappings.set(true)
    val forge11602 = createNode("1.16.2-forge", 11602, "srg")

    // legacy forge
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")

    forge11202.link(forge11602, file("versions/1.16.2-1.12.2.txt"))
    forge10809.link(forge11202)

    // Fabric
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11900 = createNode("1.19-fabric", 11900, "yarn")
    val fabric11801 = createNode("1.18.1-fabric", 11801, "yarn")
    val fabric11701 = createNode("1.17.1-fabric", 11701, "yarn")
    val fabric11602 = createNode("1.16.2-fabric", 11602, "yarn")

    fabric11602.link(forge11602)
    fabric11701.link(fabric11602)
    fabric11801.link(fabric11701)
    fabric11900.link(fabric11801)
    fabric11902.link(fabric11900)

    // Modern Forge
    val forge11902 = createNode("1.19.2-forge", 11902, "srg")
    val forge11701 = createNode("1.17.1-forge", 11701, "srg")

    forge11701.link(forge11602)
    forge11902.link(forge11701)
}