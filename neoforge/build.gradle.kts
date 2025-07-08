plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath
}

loom {
    runs {
        create("data") {
            data()
            name = "Data Generation"
            // NeoForge використовує інші властивості, ніж Fabric
            property("neoforge.logging.markers", "REGISTRIES")
            // Generate resources directly to common module
            programArgs(
                "--mod", project.property("archives_name") as String,
                "--all",
                "--output", file("../common/src/generated/resources/").absolutePath,
                "--existing", file("../common/src/main/resources/").absolutePath
            )
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // mostly optional in a single mod project
        // but multi mod projects should define one per mod
        create(project.property("archives_name") as String) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Make sure generated resources are included
sourceSets.main.get().resources {
    // No need for local generated resources as they're in common now
    // Explicitly include common resources (both main and generated)
    srcDir(project(":common").sourceSets.main.get().resources.srcDirs)
    srcDir("${project(":common").projectDir}/src/generated/resources")
}

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    compileClasspath.get().extendsFrom(configurations.getByName("common"))
    runtimeClasspath.get().extendsFrom(configurations.getByName("common"))
    getByName("developmentNeoForge").extendsFrom(configurations.getByName("common"))

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

repositories {
    maven {
        name = "NeoForged"
        setUrl("https://maven.neoforged.net/releases")
    }
}

dependencies {
    val minecraft_version: String by project.extra
    val resourceful_config_version: String by project.extra

    neoForge("net.neoforged:neoforge:${project.property("neoforge_version")}")

    modImplementation("dev.architectury:architectury-neoforge:${project.property("architectury_api_version")}")

    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowBundle"(project(mapOf("path" to ":common", "configuration" to "transformProductionNeoForge")))
}

tasks.processResources {
    inputs.property("version", project.version)

    // Handle duplicate resources
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Include resources from common module
    from(project(":common").sourceSets.main.get().resources)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
    atAccessWideners.add("${project.property("archives_name")}.accesswidener")
}