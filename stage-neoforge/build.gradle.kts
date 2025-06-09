loom {
    accessWidenerPath = project(":stage-common").loom.accessWidenerPath
}

dependencies {
    implementation(project(":network-common", "transformProductionNeoForge"))
    implementation(project(":storage-common", "transformProductionNeoForge"))
}