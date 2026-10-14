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

// Documentation generator: reads S3StorageConfiguration by reflection and renders
// docs/reference/configuration.md. Not part of the shipped jar.
val docgen: SourceSet by sourceSets.creating {
    val main = sourceSets.main.get()
    compileClasspath += main.output + main.compileClasspath
    runtimeClasspath += output + compileClasspath + main.runtimeClasspath
}

sourceSets.test {
    compileClasspath += docgen.output + docgen.compileClasspath
    runtimeClasspath += docgen.output + docgen.compileClasspath
}

val configReferenceFile = layout.projectDirectory.file("docs/reference/configuration.md")

tasks {
    register<JavaExec>("generateConfigReference") {
        group = "documentation"
        description = "Regenerates docs/reference/configuration.md from S3StorageConfiguration."
        classpath = docgen.runtimeClasspath
        mainClass.set("dev.themeinerlp.bluemap.s3.docgen.ConfigReferenceGenerator")
        args(configReferenceFile.asFile.path)
    }
    val checkConfigReference = register<JavaExec>("checkConfigReference") {
        group = "verification"
        description = "Fails if docs/reference/configuration.md differs from S3StorageConfiguration."
        classpath = docgen.runtimeClasspath
        mainClass.set("dev.themeinerlp.bluemap.s3.docgen.ConfigReferenceGenerator")
        args(configReferenceFile.asFile.path, "--check")
    }
    check {
        dependsOn(checkConfigReference)
    }
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