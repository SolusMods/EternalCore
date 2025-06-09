val includedProjects by extra {
    listOf(
        ":config",
        ":network",
        ":realm",
        ":storage",
        ":element",
        ":stage",
        ":spiritual_root",
//        ":keybind",
        ":entity",
        ":abilities",
        ":attributes"
    )
}

dependencies {
    includedProjects.forEach {
        common(project("$it-common", configuration= "namedElements"))
        implementation(project("$it-neoforge", configuration= "namedElements"))
    }
    implementation("thedarkcolour:kotlinforforge-neoforge:5.8.0")
}