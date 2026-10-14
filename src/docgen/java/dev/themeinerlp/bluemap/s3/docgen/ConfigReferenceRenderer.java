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

import java.util.List;

/** Turns a list of {@link ConfigOption} into the Markdown of the configuration reference. Pure function. */
public final class ConfigReferenceRenderer {

    public static final String COMMAND = "./gradlew generateConfigReference";

    private ConfigReferenceRenderer() {}

    public static String render(String sourceClassName, List<ConfigOption> options) {
        StringBuilder out = new StringBuilder();
        out.append("<!-- Generated from ")
                .append(sourceClassName)
                .append(" by `")
                .append(COMMAND)
                .append("`. Do not edit by hand. -->\n\n");
        out.append("# Configuration reference\n\n");
        out.append("> Generated from `")
                .append(sourceClassName)
                .append("` by `")
                .append(COMMAND)
                .append("` — do not edit by hand.\n\n");
        out.append("Keys are the field names of `")
                .append(sourceClassName)
                .append("` in lower-case dashed form, which is how BlueMap's configuration loader maps them. ")
                .append("Defaults are the field initializers of a freshly constructed instance. ")
                .append("Descriptions are the `@Comment` annotations of the fields.\n\n");

        out.append("| Key | Type | Default |\n");
        out.append("|-----|------|---------|\n");
        for (ConfigOption option : options) {
            out.append("| [`")
                    .append(option.key())
                    .append("`](#")
                    .append(option.key())
                    .append(") | `")
                    .append(option.javaType())
                    .append("` | ")
                    .append(formatDefault(option.defaultValue()))
                    .append(" |\n");
        }

        for (ConfigOption option : options) {
            out.append("\n## ").append(option.key()).append("\n\n");
            out.append("- Type: `").append(option.javaType()).append("`\n");
            out.append("- Default: ").append(formatDefault(option.defaultValue())).append('\n');
            if (option.excludedFromDebugDump()) {
                out.append("- Sensitive: excluded from BlueMap debug dumps (`@DebugDump(exclude = true)`)\n");
            }
            out.append('\n');
            String comment = normalize(option.comment());
            out.append(comment.isEmpty() ? "No description in the source.\n" : comment + "\n");
        }
        return out.toString();
    }

    static String formatDefault(Object value) {
        if (value == null) {
            return "`null`";
        }
        if (value instanceof CharSequence text) {
            return text.length() == 0 ? "`\"\"`" : "`\"" + text + "\"`";
        }
        return "`" + value + "`";
    }

    static String normalize(String comment) {
        return comment == null ? "" : comment.strip().replaceAll("\\s+", " ");
    }
}
