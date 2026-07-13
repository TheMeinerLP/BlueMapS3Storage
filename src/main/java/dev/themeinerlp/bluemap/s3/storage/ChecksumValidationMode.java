/**
 * A simple storage implementation for bluemap to save data into s3 storage solution.
 * Copyright (C) 2025 TheMeinerLP and contributors
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
package dev.themeinerlp.bluemap.s3.storage;

/**
 * The two values the AWS SDK accepts for {@code aws.requestChecksumCalculation} /
 * {@code aws.responseChecksumValidation}. Kept as our own enum (rather than referencing the SDK's
 * equivalent) since we only ever need the exact property string, and {@code nio-spi-s3} is our
 * only declared S3 dependency - its own SDK version is an implementation detail we don't want to
 * couple to directly.
 */
enum ChecksumValidationMode {
    WHEN_REQUIRED("when_required"),
    WHEN_SUPPORTED("when_supported");

    private final String propertyValue;

    ChecksumValidationMode(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    String propertyValue() {
        return propertyValue;
    }

    static ChecksumValidationMode parse(String value) {
        for (ChecksumValidationMode mode : values()) {
            if (mode.propertyValue.equalsIgnoreCase(value)) return mode;
        }
        return WHEN_REQUIRED;
    }
}
