dependencies {
    implementation(project(":network-common", "namedElements")) { isTransitive = false }
    implementation(project(":storage-common", "namedElements")) { isTransitive = false }
    implementation(project(":element-common", "namedElements")) { isTransitive = false }
}
