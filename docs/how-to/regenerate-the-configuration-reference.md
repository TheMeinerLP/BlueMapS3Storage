# Regenerate the configuration reference

Update `docs/reference/configuration.md` after you changed a field, its default or its `@Comment` in `S3StorageConfiguration`.

1. Run:

   ```sh
   ./gradlew generateConfigReference
   ```

2. Review the diff of `docs/reference/configuration.md` and commit it together with your code change.

Never edit the file by hand. `./gradlew check` (part of `./gradlew build`) runs `checkConfigReference`, which fails when the checked-in file differs from freshly generated output. Line-ending differences are ignored.

How the generator reads the keys: [Configuration reference](../reference/configuration.md) (see its introduction).
