/**
 * A simple storage implementation for bluemap to save data into s3 storage solution.
 * Copyright (C) 2026 TheMeinerLP and contributors
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.themeinerlp.bluemap.s3.docgen;

import de.bluecolored.bluemap.core.util.Key;
import dev.themeinerlp.bluemap.s3.S3StorageAddon;
import dev.themeinerlp.bluemap.s3.storage.S3StorageConfiguration;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Entry point of the Gradle tasks {@code generateConfigReference} and {@code checkConfigReference}.
 *
 * <p>Usage: {@code ConfigReferenceGenerator <output-file> [--check]}.
 */
public final class ConfigReferenceGenerator {

    private ConfigReferenceGenerator() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: ConfigReferenceGenerator <output-file> [--check]");
            System.exit(2);
        }
        Path target = Path.of(args[0]);
        boolean check = args.length > 1 && args[1].equals("--check");

        String generated = ConfigReferenceRenderer.render(S3StorageConfiguration.class.getSimpleName(), options());
        if (check) {
            String checkedIn = Files.exists(target) ? Files.readString(target, StandardCharsets.UTF_8) : null;
            Optional<String> drift = ReferenceDrift.check(args[0], generated, checkedIn);
            if (drift.isPresent()) {
                System.err.println(drift.get());
                System.exit(1);
            }
            return;
        }
        Files.createDirectories(target.toAbsolutePath().getParent());
        Files.writeString(target, generated, StandardCharsets.UTF_8);
    }

    private static List<ConfigOption> options() throws ReflectiveOperationException {
        String registeredKey = registeredStorageType();
        List<ConfigOption> options = new ArrayList<>();
        for (ConfigOption option : ConfigReferenceReader.read(S3StorageConfiguration.class)) {
            // The superclass initializer is BlueMap's file storage; this addon registers its own key.
            if (option.key().equals("storage-type")) {
                option = new ConfigOption(
                        option.key(),
                        option.javaType(),
                        registeredKey,
                        option.comment(),
                        option.excludedFromDebugDump());
            }
            options.add(option);
        }
        return options;
    }

    private static String registeredStorageType() throws ReflectiveOperationException {
        Class<?> typeClass = Class.forName(S3StorageAddon.class.getName() + "$S3StorageType");
        Constructor<?> constructor = typeClass.getDeclaredConstructor(S3StorageAddon.class);
        constructor.setAccessible(true);
        Object type = constructor.newInstance(new S3StorageAddon());
        Method getKey = typeClass.getDeclaredMethod("getKey");
        getKey.setAccessible(true);
        return ((Key) getKey.invoke(type)).getFormatted();
    }
}
