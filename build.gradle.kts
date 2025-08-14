plugins {
    id("java")
    alias(libs.plugins.spotless)
}

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