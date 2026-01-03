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

    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "reposilite-rollczi"
        url = uri("https://maven.reposilite.com/releases")
    }

    maven {
        name = "okaeri-releases"
        url = uri("https://storehouse.okaeri.eu/repository/maven-public")
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("dev.rollczi:litecommands-bukkit:3.10.6")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.0.0-beta.23")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:6.0.0-beta.23")


    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    implementation("com.github.jasync-sql:jasync-mysql:2.2.4")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.11.5")
}


tasks {
    shadowJar {
        archiveClassifier.set("")
        destinationDirectory.set(file("E:\\serwery\\lobby\\plugins"))
    }
    build {
        dependsOn(shadowJar)
    }
}