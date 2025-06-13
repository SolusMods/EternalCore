plugins {
    kotlin("jvm")
}

repositories {
    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":network-common", configuration="namedElements")) {isTransitive = false}
    implementation("org.spongepowered:mixin:0.8.7")
}