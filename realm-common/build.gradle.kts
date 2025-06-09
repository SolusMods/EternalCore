dependencies {
    implementation(project(":network-common", "namedElements")) { isTransitive = false }
    implementation(project(":storage-common", "namedElements")) { isTransitive = false }
    implementation(project(":stage-common", "namedElements")) { isTransitive = false }
}