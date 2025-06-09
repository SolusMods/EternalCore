val includedProjects by extra {
    listOf(
        ":config",
        ":network",
        ":realm",
        ":storage",
        ":element",
        ":stage",
        ":spiritual_root",
        ":entity",
        ":abilities",
        ":attributes"
    )
}

loom {
    mods {
        includedProjects.forEach {
            val moduleProject = project("$it-common")
            register(moduleProject.name) {
                sourceSet("main", moduleProject)
            }
        }
    }
}

dependencies {
    includedProjects.forEach {
        implementation(project("$it-common", "namedElements")) {
            isTransitive = false
        }
    }
}