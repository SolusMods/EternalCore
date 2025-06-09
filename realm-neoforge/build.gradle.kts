loom {
    accessWidenerPath = project(":realm-common").loom.accessWidenerPath
}

dependencies {
    implementation(project(":network-common", "transformProductionNeoForge"))
    implementation(project(":storage-common", "transformProductionNeoForge"))
}