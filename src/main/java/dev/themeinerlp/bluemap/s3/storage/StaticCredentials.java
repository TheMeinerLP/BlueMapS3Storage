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
package dev.themeinerlp.bluemap.s3.storage;

/**
 * Static S3 credentials from the storage config. When both keys are blank, no static credentials
 * are used and the AWS SDK falls back to its default credential chain (environment variables,
 * profiles, ECS task roles, EC2 instance profiles, ...).
 */
final class StaticCredentials {

    static final String ACCESS_KEY_ID_PROPERTY = "aws.accessKeyId";
    static final String SECRET_ACCESS_KEY_PROPERTY = "aws.secretAccessKey";

    private StaticCredentials() {}

    /**
     * Returns true when both keys are set, false when both are blank.
     *
     * @throws IllegalArgumentException when only one of the keys is set
     */
    static boolean isConfigured(String accessKeyId, String secretAccessKey) {
        boolean hasAccessKeyId = !isBlank(accessKeyId);
        boolean hasSecretAccessKey = !isBlank(secretAccessKey);
        if (hasAccessKeyId != hasSecretAccessKey) {
            throw new IllegalArgumentException(
                    "Set both access-key-id and secret-access-key, or leave both empty to use the default AWS credential chain");
        }
        return hasAccessKeyId;
    }

    /**
     * Sets the AWS SDK system properties to the configured keys, or clears them so the SDK's default
     * credential chain is used. Clearing matters on reload: stale properties would otherwise take
     * precedence over every other credential source.
     */
    static void applyAwsSystemProperties(String accessKeyId, String secretAccessKey) {
        if (isConfigured(accessKeyId, secretAccessKey)) {
            System.setProperty(ACCESS_KEY_ID_PROPERTY, accessKeyId);
            System.setProperty(SECRET_ACCESS_KEY_PROPERTY, secretAccessKey);
        } else {
            System.clearProperty(ACCESS_KEY_ID_PROPERTY);
            System.clearProperty(SECRET_ACCESS_KEY_PROPERTY);
        }
    }

    /** Returns the {@code key:secret} user info for S3-compatible endpoint URIs, or null when unset. */
    static String userInfo(String accessKeyId, String secretAccessKey) {
        return isConfigured(accessKeyId, secretAccessKey) ? accessKeyId + ":" + secretAccessKey : null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
