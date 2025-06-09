dependencies {
    implementation(project(":network-common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(project(":storage-common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(project(":entity-common", configuration = "namedElements")) {
        isTransitive = false
    }
}