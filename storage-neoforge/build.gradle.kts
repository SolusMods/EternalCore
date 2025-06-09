loom {
    accessWidenerPath = project(":storage-common").loom.accessWidenerPath
}

dependencies {
    implementation(project(":network-common", "transformProductionNeoForge"))
}