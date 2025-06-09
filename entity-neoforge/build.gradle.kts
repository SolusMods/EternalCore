loom {
    accessWidenerPath = project(":entity-common").loom.accessWidenerPath
}

dependencies {
    implementation(project(":network-common", "transformProductionNeoForge"))
}