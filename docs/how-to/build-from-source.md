# Build from source

Prerequisites: Git and a JDK that Gradle can use to provision Java 21 (the build declares a Java 21 toolchain).

1. Clone the repository:

   ```sh
   git clone https://github.com/TheMeinerLP/BlueMapS3Storage.git
   cd BlueMapS3Storage
   ```

2. Build the jar:

   ```sh
   ./gradlew shadowJar
   ```

3. Take the jar from `build/libs/` and copy it to your BlueMap `packs` directory (see [Storage type and file locations](../reference/storage-types-and-paths.md)).

To run the full verification that CI runs, including the tests and the documentation drift check, use:

```sh
./gradlew clean build test
```

If the build fails because `docs/reference/configuration.md` is out of date, see [Regenerate the configuration reference](regenerate-the-configuration-reference.md).
