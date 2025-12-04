plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}
group = "com.snoxmox.hyperium"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()

    // Paper API
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    // LiteCommands (Reposilite)
    maven {
        name = "reposilite-rollczi"
        url = uri("https://maven.reposilite.com/releases")
    }

    // Okaeri
    maven {
        name = "okaeri-releases"
        url = uri("https://storehouse.okaeri.eu/repository/maven-public")
    }
}

dependencies {
    // API Paper – tylko do kompilacji
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // LiteCommands (przykładowa wersja – możesz zmienić)
    implementation("dev.rollczi:litecommands-bukkit:3.10.6")

    // Okaeri Configs – wybierz wariant jaki chcesz
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.0.0-beta.23")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:6.0.0-beta.23")


    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2") // Jeśli używasz Instant/LocalDateTime

    // Jasync SQL - asynchroniczny MySQL
    implementation("com.github.jasync-sql:jasync-mysql:2.2.4")

    // Testy (opcjonalnie)
    //testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}


tasks {
    shadowJar {
        archiveClassifier.set("")
    }

    jar {
        enabled = false
    }

    register<Copy>("copyToServer") {
        dependsOn(shadowJar)

        from(shadowJar.map { it.archiveFile })

        into("E:/serwery/lobby/plugins")
    }

    build {
        dependsOn(shadowJar)
        // Kopiowanie wykona się automatycznie po zbudowaniu
        finalizedBy("copyToServer")
    }
}