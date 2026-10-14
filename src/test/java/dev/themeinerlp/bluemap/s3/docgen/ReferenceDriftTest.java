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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReferenceDriftTest {

    @Test
    void identicalContentIsNoDrift() {
        assertTrue(ReferenceDrift.check("f.md", "a\nb\n", "a\nb\n").isEmpty(), "identical content must pass");
    }

    @Test
    void differentLineEndingsAreNoDrift() {
        assertTrue(
                ReferenceDrift.check("f.md", "a\nb\n", "a\r\nb\r\n").isEmpty(),
                "CRLF checkout must not count as drift");
    }

    @Test
    void changedContentIsDriftAndNamesTheFix() {
        String message = ReferenceDrift.check("docs/f.md", "new", "old").orElseThrow();

        assertTrue(message.contains("docs/f.md"), "message must name the file: " + message);
        assertTrue(message.contains("./gradlew generateConfigReference"), "message must name the fix: " + message);
    }

    @Test
    void missingFileIsDrift() {
        assertTrue(ReferenceDrift.check("f.md", "new", null).isPresent(), "a missing file must be reported");
    }
}
