dependencies {
    implementation(project(":network-common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }
}