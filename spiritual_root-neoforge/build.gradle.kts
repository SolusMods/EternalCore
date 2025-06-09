loom {
    accessWidenerPath = project(":spiritual_root-common").loom.accessWidenerPath
}

dependencies {
    implementation(project(":network-common", "transformProductionNeoForge"))
    implementation(project(":storage-common", "transformProductionNeoForge"))
}