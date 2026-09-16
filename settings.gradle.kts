plugins {
    // Lets Gradle fetch a toolchain it cannot find locally, so the build does not depend on
    // which JDKs happen to be installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "DigimonFractal"
