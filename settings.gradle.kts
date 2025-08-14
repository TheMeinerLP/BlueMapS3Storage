rootProject.name = "BlueMapS3Storage"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("junit", "5.10.0")
            version("spotless", "7.2.1")


            library("junit.bom", "org.junit","junit-bom").versionRef("junit")
            library("junit.jupiter", "org.junit.jupiter", "junit-jupiter").withoutVersion()
            library("junit.platform.launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()

            plugin("spotless", "com.diffplug.spotless").versionRef("spotless")
        }
    }
}