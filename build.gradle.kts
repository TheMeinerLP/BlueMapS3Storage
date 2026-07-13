plugins {
    id("java")
    alias(libs.plugins.spotless)
    alias(libs.plugins.shadow)
}

// gradle.properties carries the version as `1.4.7 # x-release-please-version` - the trailing
// comment is release-please's generic-updater annotation, needed inline on the value line for
// it to bump this file. `#` mid-line isn't a real Properties comment, so it's part of the raw
// value; strip it back off here before Gradle uses it anywhere.
version = (version as String).substringBefore('#').trim()

dependencies {
    implementation(libs.nio.spi.s3)
    compileOnly(libs.bluemap.core)
    compileOnly(libs.bluemap.common)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks {
    test {
        useJUnitPlatform()
    }
    shadowJar {
        archiveClassifier.set("")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        removeWildcardImports()

        formatAnnotations()

        licenseHeaderFile(rootProject.file(".spotless/Copyright.java"))
    }
}