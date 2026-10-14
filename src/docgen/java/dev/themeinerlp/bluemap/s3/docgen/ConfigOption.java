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

/**
 * One configuration key as read from a configuration class.
 *
 * @param key the key as written in the storage config file
 * @param javaType the simple Java type name of the backing field
 * @param defaultValue the value of the field initializer; {@code null} renders as {@code null}
 * @param comment the {@code @Comment} text, or an empty string when the field has none
 * @param excludedFromDebugDump whether the field carries {@code @DebugDump(exclude = true)}
 */
public record ConfigOption(
        String key, String javaType, Object defaultValue, String comment, boolean excludedFromDebugDump) {}
