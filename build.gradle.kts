import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.kotlin.dsl.configure
import org.jreleaser.gradle.plugin.JReleaserExtension

plugins {
    idea
    java
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("io.freefair.lombok") version "8.7.1" apply false
    id("com.github.gmazzo.buildconfig") version "5.4.0" apply false
    id("org.jreleaser") version "1.13.1" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}


architectury {
    minecraft = project.property("minecraft_version").toString()
}

allprojects {
    group = project.property("maven_group").toString()
    version = project.property("mod_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "java")
    apply(plugin = "org.jreleaser")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    base.archivesName.set("${project.property("archives_name")}-${project.name}")

    configure<LoomGradleExtensionAPI> {
        enableTransitiveAccessWideners.set(true)
        interfaceInjection {
            enableDependencyInterfaceInjection.set(true)
        }
        runs {
            configureEach {
                ideConfigGenerated(true)
                vmArg("-Dmixin.env.remapRefMap=true")
                vmArg("-Dmixin.env.refMapRemappingFile=${projectDir}/build/createSrgToMcp/output.srg")
            }
        }
        // Налаштування кожної конфігурації запуску
        runConfigs.forEach { runConfig ->
            when {
                !project.name.startsWith("neoforge") -> {
                    // Вимкнення генерації IDE конфігурацій для не-тестових модулів
                    runConfig.isIdeConfigGenerated = false
                }
                else -> {
                    // Встановлення директорії запуску для тестових модулів
                    runConfig.runDir = "../run/${runConfig.environment}"
                }
            }
        }
    }



    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            // Location of the maven that hosts Team Resourceful's jars.
            name = "Resourceful Bees Maven"
            setUrl("https://nexus.resourcefulbees.com/repository/maven-public/")
        }
    }

    tasks.named<Jar>("jar") {
        // Ensure you're not excluding the API packages
        from(sourceSets["main"].output)
        // Check if you need to specifically include the API classes
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    dependencies {
        "minecraft"("net.minecraft:minecraft:${project.property("minecraft_version")}")
        "mappings"(loom.officialMojangMappings())

    }

    java {
        withSourcesJar()
        withJavadocJar()
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.named<Javadoc>("javadoc") {
        options {
            (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }



    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    // Enable parallel build for all projects
    tasks.withType<JavaCompile> {
        options.isFork = true
    }

    // Configure incremental compilation
    tasks.withType<JavaCompile> {
        options.isIncremental = true
    }

    if (!project.name.startsWith("common")) {
        val copyJar by tasks.registering(Copy::class) {
            from(tasks.named("remapJar"))
            into(layout.buildDirectory.dir("../build/${project.name}"))
            dependsOn("remapJar")
        }

        // Додавання copyJar до залежностей збірки
        tasks.named("build") {
            dependsOn(copyJar)
        }
    }

    // Створення задачі API JAR
    val apiJar by tasks.registering(Jar::class) {
        archiveClassifier.set("api")
        include("io/github/solusmods/${
            project.property("archives_name")}/api/**/*")
        include("io/github/solusmods/${project.property("archives_name")}/impl/**/*")
        from(sourceSets["main"].output)
    }

    // Додавання API JAR до артефактів
    artifacts {
        add("archives", apiJar)
    }

    configure<JReleaserExtension> {
        gitRootSearch.set(true)

        signing {
            active.set(org.jreleaser.model.Active.ALWAYS)
            armored.set(true)
        }

        deploy {
            maven {
                mavenCentral {
                    active.set(org.jreleaser.model.Active.ALWAYS)
                    // Additional configuration may be required here depending on your JReleaser setup
                }
            }
        }
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = base.archivesName.get()
                from(components["java"])

                // Закоментований артефакт API JAR
                tasks.findByName("apiJar")?.let { artifact(it) }

                // Налаштування метаданих POM
                pom {
                    name.set("${project.property("mod_display_name")}")
                    description.set("Бібліотека для модифікацій Minecraft з відкритим кодом")

                    // Інформація про ліцензію
                    licenses {
                        license {
                            name.set("GNU General Public License 3")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        }
                    }

                    // Інформація про розробника
                    developers {
                        developer {
                            id.set("Skillfi")
                            name.set("Alex")
                        }
                    }
                }
            }
        }
        // Налаштування репозиторіїв публікації
        repositories {
            maven("https://maven.cloudsmith.io/solusmods/eternalcore/") {
                name = "cloudsmith"
                // Облікові дані автентифікації з змінних середовища
                credentials {
                    username = System.getenv("CLOUDSMITH_LOGIN") ?: "££££"
                    password = System.getenv("CLOUDSMITH_API") ?: "££££"
                }
            }
        }
    }

    // Створення директорії JReleaser перед публікацією
    tasks.named("publish") {
        doFirst {
            file("$buildDir/jreleaser").mkdirs()
        }
    }

    tasks.register<Delete>("refreshIdea") {
        delete(project.file(".idea"))
        delete(project.file("*.iml"))
        delete(project.file("*.ipr"))
        delete(project.file("*.iws"))
    }

    tasks.named<ProcessResources>("processResources") {
        if (project.name.startsWith("neoforge")) {
            val placeholders = mapOf(
                "version" to project.version,
                "mod_id" to project.property("archives_name"),
                "mod_name" to project.property("mod_display_name"),
                "architectury_version" to project.property("architectury_api_version"),
                "minecraft_version" to project.property("minecraft_version"),
                "license" to "GPLv3"
            )

            inputs.properties(placeholders)

            filesMatching("META-INF/neoforge.mods.toml") {
                expand(placeholders)
            }
        } else if (project.name.startsWith("fabric")) {
            val placeholders = mapOf(
                "version" to project.version,
                "mod_id" to project.property("archives_name"),
                "mod_name" to project.property("mod_display_name"),
                "architectury_version" to project.property("architectury_api_version"),
                "minecraft_version" to project.property("minecraft_version"),
                "fabric_loader_version" to project.property("fabric_loader_version"),
                "license" to "GPLv3"
            )

            inputs.properties(placeholders)

            filesMatching("fabric.mod.json") {
                expand(placeholders)
            }
        }
    }
}