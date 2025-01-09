import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.20-Beta1"
//    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "pet.lily"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

//    // koin
//    implementation("io.insert-koin:koin-core:4.0.1")
//
//    // auto service
//    implementation("com.google.auto.service:auto-service-annotations:1.11.0")
//    ksp("com.google.auto.service:auto-service:1.11.0")

    // reflection
    implementation("org.reflections:reflections:0.10.2")

    // database
    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.flywaydb:flyway-core:11.1.1")
    implementation("org.xerial:sqlite-jdbc:3.47.2.0")

    // configuration
    implementation("com.akuleshov7:ktoml-core:0.5.1")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    runServer {
        minecraftVersion("1.21.3")
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

    withType<ShadowJar> {
        mergeServiceFiles()
    }
}