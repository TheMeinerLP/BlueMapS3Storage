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

import java.util.Optional;

/** Compares a checked-in reference with freshly generated output. Line endings do not count as drift. */
public final class ReferenceDrift {

    private ReferenceDrift() {}

    /**
     * @param path the checked-in file, named in the message
     * @param generated freshly rendered content
     * @param checkedIn content of the checked-in file, {@code null} if it does not exist
     * @return a message naming the fix when the two differ, otherwise empty
     */
    public static Optional<String> check(String path, String generated, String checkedIn) {
        if (checkedIn != null && unixLines(generated).equals(unixLines(checkedIn))) {
            return Optional.empty();
        }
        return Optional.of(path + " is out of date with its source. Run `" + ConfigReferenceRenderer.COMMAND
                + "` and commit the result.");
    }

    private static String unixLines(String text) {
        return text.replace("\r\n", "\n");
    }
}
