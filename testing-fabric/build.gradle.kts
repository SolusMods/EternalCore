val includedProjects by extra {
    listOf(
        ":config",
        ":network",
        ":realm",
        ":storage",
        ":element",
        ":stage",
        ":spiritual_root",
        ":keybind",
        ":entity",
        ":abilities",
        ":attributes"
    )
}

//loom {
//    mods {
//        includedProjects.forEach {
//            val moduleProject = project("$it-fabric")
//            register(moduleProject.name) {
//                sourceSet(sourceSets.main.get(), moduleProject)
//            }
//        }
//    }
//}


dependencies {
    includedProjects.forEach { projectPath ->
        common(project("$projectPath-common", configuration = "namedElements"))
        implementation(project("$projectPath-fabric", configuration = "namedElements")) {
            isTransitive = false
        }
    }
    modImplementation("com.electronwill.night-config:toml:3.8.1")
}