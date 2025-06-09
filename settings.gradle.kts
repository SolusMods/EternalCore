pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://files.minecraftforge.net/maven/") }
        gradlePluginPortal()
        google() // необов'язково, але корисно
    }
    plugins {
        kotlin("jvm") version "2.1.21"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "eternalcore"

listOf("config", "network", "realm", "storage", "testing", "stage", "spiritual_root", "element", "abilities", "entity", "keybind", "attributes").forEach { module ->
    include(":$module-common")
    include(":$module-fabric")
    include(":$module-neoforge")
}