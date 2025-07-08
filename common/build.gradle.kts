plugins {
    java
}

architectury {
    common(rootProject.property("enabled_platforms").toString().split(";"))
    platformSetupLoomIde()
}

val archives_name: String by project.extra
val resourceful_config_version: String by project.extra

loom {
    accessWidenerPath.set(file("src/main/resources/${archives_name}.accesswidener"))
    interfaceInjection {
        enableDependencyInterfaceInjection = true
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

dependencies {
    val fabric_loader_version: String by project.extra
    val architectury_api_version: String by project.extra
    val minecraft_version: String by project.extra

    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    compileOnly("org.jetbrains:annotations:24.0.1")

    annotationProcessor("org.jetbrains:annotations:24.0.1")
    implementation("org.spongepowered:mixin:0.8.7")

    // JavaPoet для генерації коду
    annotationProcessor("com.squareup:javapoet:1.13.0")
    implementation("com.squareup:javapoet:1.13.0")

    compileOnly("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")

    modImplementation("dev.architectury:architectury:$architectury_api_version")
}