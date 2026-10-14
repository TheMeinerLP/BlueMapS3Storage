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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bluecolored.bluemap.common.debug.DebugDump;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

class ConfigReferenceReaderTest {

    @SuppressWarnings("unused")
    static class Base {
        private String inherited = "base";
    }

    @SuppressWarnings("unused")
    static final class Fixture extends Base {
        private static final String CONSTANT = "ignored";
        private transient String skipped = "ignored";

        @Comment("a comment")
        private String bucketName = "my-bucket";

        @Setting("custom-key")
        private int renamedField = 7;

        @DebugDump(exclude = true)
        private boolean secretFlag = true;
    }

    private static ConfigOption find(List<ConfigOption> options, String key) {
        return options.stream()
                .filter(o -> o.key().equals(key))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no key " + key + " in " + options));
    }

    @Test
    void camelCaseFieldBecomesLowerCaseDashedKey() {
        assertEquals(
                "my-bucket",
                find(ConfigReferenceReader.read(Fixture.class), "bucket-name").defaultValue(),
                "bucketName must map to bucket-name with its initializer value");
    }

    @Test
    void settingAnnotationOverridesTheKey() {
        assertEquals(
                7,
                find(ConfigReferenceReader.read(Fixture.class), "custom-key").defaultValue(),
                "@Setting value must be used as key");
    }

    @Test
    void commentTextIsRead() {
        assertEquals(
                "a comment",
                find(ConfigReferenceReader.read(Fixture.class), "bucket-name").comment(),
                "@Comment text must be carried over");
    }

    @Test
    void debugDumpExclusionIsRead() {
        List<ConfigOption> options = ConfigReferenceReader.read(Fixture.class);

        assertTrue(find(options, "secret-flag").excludedFromDebugDump(), "excluded field must be flagged");
        assertFalse(find(options, "bucket-name").excludedFromDebugDump(), "plain field must not be flagged");
    }

    @Test
    void staticAndTransientFieldsAreSkipped() {
        List<String> keys =
                ConfigReferenceReader.read(Fixture.class).stream().map(ConfigOption::key).toList();

        assertFalse(keys.contains("constant"), "static field must be skipped: " + keys);
        assertFalse(keys.contains("skipped"), "transient field must be skipped: " + keys);
    }

    @Test
    void superclassFieldsComeFirst() {
        List<ConfigOption> options = ConfigReferenceReader.read(Fixture.class);

        assertEquals("inherited", options.get(0).key(), "superclass field must be listed first: " + options);
    }
}
