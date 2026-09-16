plugins {
    application
    id("com.diffplug.spotless") version "8.10.2"
    id("net.ltgt.errorprone") version "5.1.1"
}

group = "com.tagadvance"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    errorprone("com.google.errorprone:error_prone_core:2.50.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "com.tagadvance.digimon.Main"
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "DigimonFractal",
            "Implementation-Version" to version,
            "Main-Class" to application.mainClass,
        )
    }
}

spotless {
    java {
        googleJavaFormat("1.36.1")
        formatAnnotations()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
        // google-java-format strips the blank line before a type's closing brace and cannot be
        // configured not to, so put it back. Only the last brace in a file is reached; nested
        // types are left to the author.
        replaceRegex("blank line before the closing brace", "\\n\\s*\\}\\s*\\z", "\n\n}\n")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // The tests draw onto a BufferedImage and never need a display.
    systemProperty("java.awt.headless", "true")
}
