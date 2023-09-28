plugins {
    id("fabric-loom") version "1.2-SNAPSHOT"
    id("io.github.juuxel.loom-vineflower") version "1.+"
    id("io.github.p03w.machete") version "1.+"
    id("org.cadixdev.licenser") version "0.6.+"
}

apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/7fc230a4e87b94d533ca3696cc1887bd2650a0ce/publishing.gradle.kts")
apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/7fc230a4e87b94d533ca3696cc1887bd2650a0ce/misc.gradle.kts")

val mod_version: String by project

group = "io.github.jamalam360"
version = mod_version

repositories {
    val mavenUrls = mapOf(
            Pair("https://maven.terraformersmc.com/releases", listOf("com.terraformersmc")),
            Pair("https://maven.quiltmc.org/repository/release/", listOf("org.quiltmc", "org.quiltmc.unpick")),
            Pair("https://maven.quiltmc.org/repository/snapshot/", listOf("org.quiltmc", "org.quiltmc.unpick")),
            Pair("https://api.modrinth.com/maven", listOf("maven.modrinth")),
    )

    for (mavenPair in mavenUrls) {
        maven {
            url = uri(mavenPair.key)
            content {
                for (group in mavenPair.value) {
                    includeGroup(group)
                }
            }
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.mappings) {
        classifier("intermediary-v2")
    })

    modImplementation(libs.bundles.fabric)
    modApi(libs.bundles.required)
    modImplementation(libs.bundles.optional)
    modLocalRuntime(libs.bundles.runtime)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType(Javadoc::class.java) {
    exclude("**/mixin/**")
}

sourceSets {
    val main = this.getByName("main")

    create("testmod") {
        this.compileClasspath += main.compileClasspath
        this.compileClasspath += main.output
        this.runtimeClasspath += main.runtimeClasspath
        this.runtimeClasspath += main.output
    }
}

loom {
    runtimeOnlyLog4j.set(true)

    runs {
        create("testClient") {
            client()
            name("Testmod Client")
            source(sourceSets.getByName("testmod"))
            runDir("run/test/client")
        }

        create("testServer") {
            server()
            name("Testmod Server")
            source(sourceSets.getByName("testmod"))
            runDir("run/test/server")
        }

        getByName("client") {
            runDir("run/client")
        }

        getByName("server") {
            runDir("run/server")
        }
    }
}
