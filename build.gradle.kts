import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20-RC2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("com.gradleup.shadow") version "9.0.0-beta8"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "pet.lily"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.charleskorn.kaml:kaml:0.72.0")

    implementation("org.reflections:reflections:0.10.2")

    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.flywaydb:flyway-core:11.1.1")
    implementation("org.xerial:sqlite-jdbc:3.47.2.0")

    implementation("org.incendo:cloud-core:2.0.0")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation("org.incendo:cloud-kotlin-extensions:2.0.0")
    implementation("org.incendo:cloud-kotlin-coroutines:2.0.0")
    implementation("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0")
}

val targetJavaVersion = 23
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }

    build {
        dependsOn("shadowJar")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-java-parameters")
        }
    }

    withType<ShadowJar> {
        mergeServiceFiles()
    }
}