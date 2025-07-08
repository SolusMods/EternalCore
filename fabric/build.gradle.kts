plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath
}

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    compileClasspath.get().extendsFrom(configurations.getByName("common"))
    runtimeClasspath.get().extendsFrom(configurations.getByName("common"))
    getByName("developmentFabric").extendsFrom(configurations.getByName("common"))

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

dependencies {
    val resourceful_config_version: String by project.extra
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation("dev.architectury:architectury-fabric:${property("architectury_api_version")}")

    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowBundle"(project(path = ":common", configuration = "transformProductionFabric"))

    modImplementation("me.lucko:fabric-permissions-api:${property("fabric_permissions_api_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    configurations = listOf(project.configurations["shadowBundle"])
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
}