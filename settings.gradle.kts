rootProject.name = "BlueMapS3Storage"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("junit", "5.10.0")
            version("spotless", "7.2.1")
            version("aws-java-nio-spi-for-s3", "2.2.1")

            library("nio-spi-s3", "software.amazon.nio.s3", "aws-java-nio-spi-for-s3").versionRef("aws-java-nio-spi-for-s3")

            library("junit.bom", "org.junit","junit-bom").versionRef("junit")
            library("junit.jupiter", "org.junit.jupiter", "junit-jupiter").withoutVersion()
            library("junit.platform.launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()

            plugin("spotless", "com.diffplug.spotless").versionRef("spotless")
        }
    }
}