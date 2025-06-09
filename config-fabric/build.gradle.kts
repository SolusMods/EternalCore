dependencies {
    include(implementation("com.electronwill.night-config:core:3.8.1")!!)
    include(implementation("com.electronwill.night-config:toml:3.8.1")!!)
    implementation(project(":network-common", "transformProductionFabric")) {
        isTransitive = false
    }
}