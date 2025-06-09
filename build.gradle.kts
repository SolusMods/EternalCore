import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jreleaser.gradle.plugin.JReleaserExtension

plugins {
    // Architectury Loom - Gradle плагін для розробки модів Minecraft
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    // Architectury Plugin - Спрощує розробку мультиплатформних модів
    id("architectury-plugin") version "3.4-SNAPSHOT"
    // Shadow плагін - Для створення fat/uber JAR з усіма залежностями
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    // Lombok - Зменшує шаблонний код завдяки анотаціям
    id("io.freefair.lombok") version "8.10" apply false
    // BuildConfig - Генерує константи конфігурації збірки
    id("com.github.gmazzo.buildconfig") version "5.4.0" apply false
    // Інтеграція з IntelliJ IDEA
    idea
    // Стандартний плагін Java
    java
    // JReleaser - Для публікації релізів
    id("org.jreleaser") version "1.13.1" apply false
    // Додаткові розширення IDEA
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    kotlin("jvm") version "2.1.21"
    `kotlin-dsl`
}

// Отримання властивостей з gradle.properties - використовуємо делегати властивостей
val minecraft_version: String by project.extra
val maven_group: String by project.extra
val mod_version: String by project.extra
val enabled_platforms: String by project.extra
val archives_name: String by project.extra
val mod_display_name: String by project.extra
val fabric_loader_version: String by project.extra
val architectury_api_version: String by project.extra
val fabric_api_version: String by project.extra
val neoforge_version: String by project.extra

architectury {
    minecraft = minecraft_version
}

repositories {
    mavenCentral()
}

// Застосування загальної конфігурації до всіх проектів
allprojects {
    // Встановлення ідентифікатора групи та версії з властивостей кореневого проекту
    group = maven_group
    version = mod_version

}

// Налаштування всіх підпроектів (платформенних модулів)
subprojects {

    // Застосування основних плагінів до всіх підпроектів
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "java")
    apply(plugin = "kotlin")

    // Отримання Loom extension через typed accessor
    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    // Застосування плагінів публікації, крім тестових модулів
    if (!project.name.startsWith("testing")) {
        apply(plugin = "org.jreleaser")
        apply(plugin = "maven-publish")
    }
    // Визначення властивостей розширення для найменування модулів та ідентифікації
    val nameSeparatorIndex = project.path.lastIndexOf('-')
    val nameOffset = if (project.path.startsWith(':') && nameSeparatorIndex > 0) 1 else 0
    // Використовуємо extension properties замість extra properties для type safety
    val moduleName = project.path.substring(
        nameOffset,
        if (nameSeparatorIndex == -1) project.path.length else nameSeparatorIndex
    )
    // Конструювання ідентифікатора мода (наприклад, "eternalcore_core")
    val modId = "${archives_name}_$moduleName"
    val kotlinNeo = "kotlinforforge"
    val kotlinNeoRange = "[5.8,)"

    // Витягнення типу платформи (наприклад, "fabric" з "core-fabric")
    val moduleType = project.name.substring(project.name.lastIndexOf('-') + 1)

    // Налаштування IDEA для завантаження JavaDoc
    // configure<IdeaModel> {
    //     module {
    //         isDownloadJavadoc = true
    //     }
    // }

    // Застосування Shadow плагіну до платформо-специфічних проектів, BuildConfig до загальних модулів
    when {
        !project.name.endsWith("common") -> apply(plugin = "com.github.johnrengelman.shadow")
        else -> apply(plugin = "com.github.gmazzo.buildconfig")
    }

    // Налаштування плагіну Architectury для кожного підпроекту на основі його типу
    architectury {
        when {
            !project.name.endsWith("common") -> {
                // Для платформо-специфічних модулів, налаштування інтеграції IDE
                platformSetupLoomIde()
            }
            else -> {
                // Для загальних модулів, вказуємо які платформи увімкнені
                common(enabled_platforms.split(','))
            }
        }

        // Налаштування платформо-специфічних параметрів
        when {
            project.name.endsWith("fabric") -> fabric()
            project.name.endsWith("neoforge") -> neoForge()
        }
    }

    // Налаштування Loom (розробка модів Minecraft)
    configure<LoomGradleExtensionAPI> {
        enableTransitiveAccessWideners.set(true)

        interfaceInjection {
            enableDependencyInterfaceInjection.set(true)
        }

        // Налаштування конфігурацій запуску для всіх запусків
        runs {
            configureEach {
                // Генерування IDE конфігурацій
                ideConfigGenerated(true)
                // Налаштування середовища Mixin для розробки
                vmArg("-Dmixin.env.remapRefMap=true")
                vmArg("-Dmixin.env.refMapRemappingFile=${projectDir}/build/createSrgToMcp/output.srg")
            }
        }

        // Налаштування кожної конфігурації запуску
        runConfigs.forEach { runConfig ->
            when {
                !project.name.startsWith("testing") -> {
                    // Вимкнення генерації IDE конфігурацій для не-тестових модулів
                    runConfig.isIdeConfigGenerated = false
                }
                else -> {
                    // Встановлення директорії запуску для тестових модулів
                    runConfig.runDir = "../run/${runConfig.environment}"
                }
            }
        }

        // Налаштування процесора анотацій Mixin
        mixin {
            useLegacyMixinAp.set(false)
        }

        // Увімкнення впровадження інтерфейсів залежностей
        interfaceInjection {
            enableDependencyInterfaceInjection.set(true)
        }
    }

    if (project.name.endsWith("common")) {
        if (!project.name.startsWith("testing")) {
            configure<LoomGradleExtensionAPI> {
                accessWidenerPath.set(file("src/main/resources/${project.property("archives_name")}_$moduleName.accesswidener"))
            }
        }
    }

    if (project.name.endsWith("neoforge")) {
        if (!project.name.startsWith("testing")) {
            configure<LoomGradleExtensionAPI> {
                accessWidenerPath.set(project(":$moduleName-common").extensions.getByType<LoomGradleExtensionAPI>().accessWidenerPath)
            }
        }
    }

    if (project.name.endsWith("fabric")) {
        if (!project.name.startsWith("testing")) {
            configure<LoomGradleExtensionAPI> {
                accessWidenerPath.set(project(":$moduleName-common").extensions.getByType<LoomGradleExtensionAPI>().accessWidenerPath)
            }
        }
    }

    // Налаштування імен архівів для всіх виходів
    tasks.withType<AbstractArchiveTask>().configureEach {
        archiveBaseName.set("$archives_name-${rootProject.name}-${project.name}")
    }

    // Налаштування залежностей для платформо-специфічних модулів
    if (!project.name.endsWith("common")) {
        configurations {
            // Загальна конфігурація для залежностей із загального модуля
            val common by creating {
                isCanBeResolved = true
                isCanBeConsumed = false
            }

            // Розширення загальної конфігурації для шляхів компіляції та виконання
            named("compileClasspath") { extendsFrom(common) }
            named("runtimeClasspath") { extendsFrom(common) }

            // Платформо-специфічні конфігурації розробки
            when {
                project.name.endsWith("fabric") ->
                    named("developmentFabric") { extendsFrom(common) }
                project.name.endsWith("neoforge") ->
                    named("developmentNeoForge") { extendsFrom(common) }
            }

            // Конфігурація для затінених залежностей
            val shadowBundle by creating {
                isCanBeResolved = true
                isCanBeConsumed = false
            }
        }
    }

    // Налаштування репозиторіїв для розв'язання залежностей
    repositories {
        // Репозиторій NeoForged додається лише за потреби
        if (project.name.endsWith("neoforge")) {
            maven("https://maven.neoforged.net/releases") {
                name = "NeoForged"
            }
            // KFF
            maven {
                name = "Kotlin for Forge"
                setUrl("https://thedarkcolour.github.io/KotlinForForge/")
            }
        }

        // Налаштування репозиторію Modrinth для модів Minecraft
        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven") {
                    name = "Modrinth"
                }
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
    }

    // Налаштування залежностей
    dependencies {
        // Minecraft як залежність
        "minecraft"("net.minecraft:minecraft:$minecraft_version")

        // Використання офіційних відображень Mojang для деобфускації
        "mappings"(loom.officialMojangMappings())

        // Залежності загального модуля
        if (project.name.endsWith("common")) {
            // Fabric Loader API для завантаження модів
            "modImplementation"("net.fabricmc:fabric-loader:$fabric_loader_version")
            // Architectury API для кросплатформенних абстракцій
            "modImplementation"("dev.architectury:architectury:$architectury_api_version")
        }

        // Платформо-специфічні залежності Fabric
        if (project.name.endsWith("fabric")) {
            "modImplementation"("net.fabricmc:fabric-loader:$fabric_loader_version")
            "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
            "modImplementation"("dev.architectury:architectury-fabric:$architectury_api_version")

            // Включення загального модуля для цього компонента
            "common"(project(path = ":$moduleName-common", configuration = "namedElements")) {
                isTransitive = false
            }
            "shadowBundle"(project(path = ":$moduleName-common", configuration = "transformProductionFabric"))
            // Fabric Kotlin
            "modImplementation"("net.fabricmc:fabric-language-kotlin:1.13.3+kotlin.2.1.21")
        }

        // Платформо-специфічні залежності NeoForge
        if (project.name.endsWith("neoforge")) {
            "neoForge"("net.neoforged:neoforge:$neoforge_version")
            "modImplementation"("dev.architectury:architectury-neoforge:$architectury_api_version")

            // Включення загального модуля для цього компонента
            "common"(project(path = ":$moduleName-common", configuration = "namedElements")) {
                isTransitive = false
            }
            "shadowBundle"(project(path = ":$moduleName-common", configuration = "transformProductionNeoForge"))

            "implementation"("thedarkcolour:kotlinforforge-neoforge:5.8.0")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }

    // Налаштування параметрів компіляції Java
    java {
        // Генерування JAR з вихідними кодами та JavaDoc
        withSourcesJar()
        withJavadocJar()
        // Встановлення сумісності з Java 21
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    // Вимкнення помилок перевірки Javadoc
    tasks.withType<Javadoc>().configureEach {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    // Налаштування параметрів компілятора Java
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.isIncremental = true
        options.isFork = true
    }

    // Обробка ресурсів для платформо-специфічних модулів
    if (!project.name.endsWith("common")) {
        tasks.named<ProcessResources>("processResources") {
            // Заміна змінних у NeoForge mod.toml
            when {
                project.name.endsWith("neoforge") -> {
                    val placeholders = mapOf(
                        "modLoader" to kotlinNeo,
                        "loaderVersion" to kotlinNeoRange,
                        "version" to project.version.toString(),
                        "mod_id" to modId,
                        "mod_name" to "$mod_display_name - ${moduleName.replaceFirstChar { it.uppercase() }}",
                        "architectury_version" to architectury_api_version,
                        "minecraft_version" to minecraft_version,
                        "license" to "GPLv3"
                    )

                    inputs.properties(placeholders)

                    // Обробка файлу метаданих NeoForge
                    filesMatching("META-INF/neoforge.mods.toml") {
                        expand(placeholders)
                    }
                }
                project.name.endsWith("fabric") -> {
                    val placeholders = mapOf(
                        "version" to project.version.toString(),
                        "mod_id" to modId,
                        "mod_name" to "$mod_display_name - ${moduleName.replaceFirstChar { it.uppercase() }}",
                        "architectury_version" to architectury_api_version,
                        "minecraft_version" to minecraft_version,
                        "fabric_loader_version" to fabric_loader_version,
                        "license" to "GPLv3"
                    )

                    inputs.properties(placeholders)

                    // Обробка файлу метаданих Fabric
                    filesMatching("fabric.mod.json") {
                        expand(placeholders)

                        // Додавання access widener для не-тестових модулів
                        if (!project.name.startsWith("testing")) {
                            filter { line ->
                                if (line.contains("\"mixins\":")) {
                                    "  \"accessWidener\": \"$modId.accesswidener\",\n$line"
                                } else {
                                    line
                                }
                            }
                        }
                    }
                }
            }
        }

        // Налаштування задачі Shadow JAR для затінення залежностей
        tasks.named<ShadowJar>("shadowJar") {
            configurations = listOf(project.configurations.getByName("shadowBundle"))
            archiveClassifier.set("dev-shadow")
            mergeServiceFiles()
        }

        // Налаштування задачі перевідображення JAR для трансформації байткоду
        tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
            inputFile.set(tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
            dependsOn("shadowJar")
        }

        configurations {
            create("named")
        }
    }

    // Генерування констант збірки для загальних модулів
    if (project.name.endsWith("common")) {
        configure<com.github.gmazzo.buildconfig.BuildConfigExtension> {
            className("ModuleConstants")
            packageName("$maven_group.${rootProject.name}.$moduleName")
            useJavaOutput()
            buildConfigField("String", "MOD_ID", "\"$modId\"")
        }
    }

    // Копіювання фінального JAR до директорії збірки для платформо-специфічних модулів
    if (!project.name.endsWith("common")) {
        val copyJar by tasks.registering(Copy::class) {
            from(tasks.named("remapJar"))
            into(layout.buildDirectory.dir("../build/$moduleType"))
            dependsOn("remapJar")
        }

        // Додавання copyJar до залежностей збірки
        tasks.named("build") {
            dependsOn(copyJar)
        }
    }

    // Створення задачі API JAR
    val apiJar by tasks.registering(Jar::class) {
        archiveClassifier.set("api")
        include("io/github/solusmods/$archives_name/$moduleName/api/**/*")
        include("io/github/solusmods/$archives_name/$moduleName/impl/**/*")
        from(sourceSets["main"].output)
    }

    // Додавання API JAR до артефактів
    artifacts {
        add("archives", apiJar)
    }

    // Налаштування JReleaser для не-тестових модулів
    if (!project.name.startsWith("testing")) {
        extensions.configure<JReleaserExtension> {
            gitRootSearch.set(true)

            // Налаштування підписування для релізів
            signing {
                active.set(org.jreleaser.model.Active.ALWAYS)
                armored.set(true)
            }

            // Налаштування розгортання Maven Central
            // deploy {
            //     maven {
            //         mavenCentral {
            //             sonatype {
            //                 active.set(org.jreleaser.model.Active.ALWAYS)
            //                 url.set("https://central.sonatype.com/api/v1/publisher")
            //                 stagingRepository("build/staging-deploy")
            //             }
            //         }
            //     }
            // }
        }

        // Налаштування параметрів публікації Maven
        configure<PublishingExtension> {
            publications {
                // Основна публікація Maven
                create<MavenPublication>("mavenJava") {
                    artifactId = base.archivesName.get()
                    from(components["java"])

                    // Закоментований артефакт API JAR
                    tasks.findByName("apiJar")?.let { artifact(it) }

                    // Налаштування метаданих POM
                    pom {
                        name.set("$mod_display_name - ${moduleName.replaceFirstChar { it.uppercase() }}")
                        description.set("Бібліотека для модифікацій Minecraft з відкритим кодом")

                        // Інформація про ліцензію
                        licenses {
                            license {
                                name.set("GNU General Public License 3")
                                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                            }
                        }

                        // Інформація про розробника
                        developers {
                            developer {
                                id.set("Skillfi")
                                name.set("Alex")
                            }
                        }
                    }
                }
            }

            // Налаштування репозиторіїв публікації
            repositories {
                maven("https://maven.cloudsmith.io/solusmods/eternalcore/") {
                    name = "cloudsmith"
                    // Облікові дані автентифікації з змінних середовища
                    credentials {
                        username = System.getenv("CLOUDSMITH_LOGIN") ?: "££££"
                        password = System.getenv("CLOUDSMITH_API") ?: "££££"
                    }
                }
            }
        }

        // Створення директорії JReleaser перед публікацією
        tasks.named("publish") {
            doFirst {
                layout.buildDirectory.dir("jreleaser").get().asFile.mkdirs()
            }
        }
    }
}