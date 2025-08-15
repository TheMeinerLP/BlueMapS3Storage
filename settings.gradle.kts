rootProject.name = "BlueMapS3Storage"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.bluecolored.de/releases")
    }
    versionCatalogs {
        create("libs") {
            version("junit", "5.13.4")
            version("spotless", "7.2.1")
            version("aws-java-nio-spi-for-s3", "2.2.1")
            version("bluemap", "5.3")
            version("shadow", "9.0.2")

            library("nio-spi-s3", "software.amazon.nio.s3", "aws-java-nio-spi-for-s3").versionRef("aws-java-nio-spi-for-s3")
            library("bluemap.core", "de.bluecolored.bluemap", "BlueMapCore").versionRef("bluemap")
            library("bluemap.common", "de.bluecolored.bluemap", "BlueMapCommon").versionRef("bluemap")

            library("junit.bom", "org.junit","junit-bom").versionRef("junit")
            library("junit.jupiter", "org.junit.jupiter", "junit-jupiter").withoutVersion()
            library("junit.platform.launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()

            plugin("spotless", "com.diffplug.spotless").versionRef("spotless")
            plugin("shadow", "com.gradleup.shadow").versionRef("shadow")
        }
    }
}