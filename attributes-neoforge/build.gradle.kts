plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(mapOf("path" to ":network-common", "configuration" to "transformProductionNeoForge")))
}