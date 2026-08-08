plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Provided by the server at runtime - not shaded into the jar.
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    processResources {
        filteringCharset = "UTF-8"
    }

    // shadowJar bundles any (non-compileOnly) dependencies into one jar.
    // We have none right now, but this keeps the project future-proof
    // and guarantees a single, self-contained jar is produced.
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("WoodenSwordPlugin")
    }

    build {
        dependsOn(shadowJar)
    }
}
