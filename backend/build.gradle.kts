plugins {
    application
}

group = "io.github.pandier"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("io.github.pandier.multisnake.App")
}
