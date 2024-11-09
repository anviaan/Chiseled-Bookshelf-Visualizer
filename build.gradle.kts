plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

val MOD_VERSION = project.property("mod_version")
val ARCHIVE_NAME = project.property("archives_base_name")
val COMPATIBLE_VERSIONS = "[1.21, 1.21.1]"

base {
    archivesName.set(project.property("archives_base_name") as String)
}

repositories {
    maven {
        name = "Mod Menu"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "WispForest"
        url = uri("https://maven.wispforest.io/releases")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    val apiModules = listOf(
        "fabric-api-base", "fabric-networking-api-v1", "fabric-lifecycle-events-v1", "fabric-resource-loader-v0"
    )

    apiModules.forEach {
        modImplementation(fabricApi.module(it, project.property("fabric_version") as String))
    }

    modLocalRuntime("com.terraformersmc:modmenu:${project.property("modmenu_version")}")

    annotationProcessor("io.wispforest:owo-lib:${project.property("owo_version")}")
    modImplementation("io.wispforest:owo-lib:${project.property("owo_version")}")
    include("io.wispforest:owo-sentinel:${project.property("owo_version")}")
    implementation("com.github.fracpete:romannumerals4j:0.0.1")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version")
        )
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

tasks.register("printEnv") {
    doLast {
        val envFile = File(System.getenv("GITHUB_ENV"))
        envFile.appendText("MOD_VERSION=$MOD_VERSION\n")
        envFile.appendText("RELEASE_NAME=$ARCHIVE_NAME-$MOD_VERSION\n")
        envFile.appendText("GAME_VERSIONS=$COMPATIBLE_VERSIONS\n")
    }
}