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

import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigReferenceRendererTest {

    private static ConfigOption option(String key, Object defaultValue, String comment, boolean sensitive) {
        return new ConfigOption(key, "String", defaultValue, comment, sensitive);
    }

    private static String render(ConfigOption... options) {
        return ConfigReferenceRenderer.render("SomeConfig", List.of(options));
    }

    @Test
    void bannerNamesSourceClassAndGenerateCommand() {
        String out = render();

        assertTrue(
                out.contains("Generated from `SomeConfig` by `./gradlew generateConfigReference`"),
                "banner must name the source class and the command: " + out);
        assertTrue(out.contains("do not edit by hand"), "banner must warn against manual edits: " + out);
    }

    @Test
    void everyOptionAppearsInSummaryTableAndAsSection() {
        String out = render(option("bucket-name", "x", "c", false));

        assertTrue(out.contains("| [`bucket-name`](#bucket-name) | `String` | `\"x\"` |"), "summary row missing: " + out);
        assertTrue(out.contains("\n## bucket-name\n"), "section heading missing: " + out);
    }

    @Test
    void stringDefaultIsQuoted() {
        assertEquals("`\"gzip\"`", ConfigReferenceRenderer.formatDefault("gzip"));
    }

    @Test
    void emptyStringDefaultIsShownAsEmptyQuotes() {
        assertEquals("`\"\"`", ConfigReferenceRenderer.formatDefault(""));
    }

    @Test
    void nonStringDefaultIsNotQuoted() {
        assertEquals("`false`", ConfigReferenceRenderer.formatDefault(false));
        assertEquals("`0`", ConfigReferenceRenderer.formatDefault(0));
    }

    @Test
    void nullDefaultIsShownAsNull() {
        assertEquals("`null`", ConfigReferenceRenderer.formatDefault(null));
    }

    @Test
    void multiLineCommentIsCollapsedToOneParagraph() {
        String out = render(option("k", "v", "first line\n   second line\n", false));

        assertTrue(out.contains("first line second line\n"), "comment must be joined on one line: " + out);
    }

    @Test
    void optionWithoutCommentSaysSo() {
        String out = render(option("k", "v", "", false));

        assertTrue(out.contains("No description in the source."), "missing-comment notice absent: " + out);
    }

    @Test
    void sensitiveOptionIsMarked() {
        String out = render(option("secret", "v", "c", true));

        assertTrue(out.contains("Sensitive: excluded from BlueMap debug dumps"), "sensitive marker absent: " + out);
    }

    @Test
    void nonSensitiveOptionIsNotMarked() {
        String out = render(option("plain", "v", "c", false));

        assertFalse(out.contains("Sensitive"), "unexpected sensitive marker: " + out);
    }

    @Test
    void optionsKeepTheirGivenOrder() {
        String out = render(option("zeta", "1", "c", false), option("alpha", "2", "c", false));

        assertTrue(out.indexOf("## zeta") < out.indexOf("## alpha"), "order must follow input order: " + out);
    }

    @Test
    void sameInputRendersIdenticalOutput() {
        ConfigOption[] options = {option("a", "1", "c", false)};

        assertEquals(render(options), render(options), "rendering must be deterministic");
    }
}
