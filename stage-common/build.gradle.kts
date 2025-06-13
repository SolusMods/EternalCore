plugins {
    kotlin("jvm")
}
dependencies {
    implementation(project(":network-common", configuration = "namedElements")) { isTransitive = false }
    implementation(project(path = ":storage-common", configuration = "namedElements")) { isTransitive = false }
    implementation(project(path = ":entity-common", configuration = "namedElements")) { isTransitive = false }
}