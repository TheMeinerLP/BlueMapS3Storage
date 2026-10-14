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

/** Plain-value {@link S3Configuration} so tests need no BlueMap classes. Each test builds its own. */
final class TestConfig implements S3Configuration {

    String bucketName = "bucket";
    String region = S3StorageConfiguration.DEFAULT_REGION;
    String accessKeyId = "real-key";
    String secretAccessKey = "real-secret";
    String endpointUrl = S3StorageConfiguration.DEFAULT_ENDPOINT_URL;
    boolean forcePathStyle = false;
    String accountId = "";
    String provider = "";

    TestConfig region(String v) {
        region = v;
        return this;
    }

    TestConfig accessKeyId(String v) {
        accessKeyId = v;
        return this;
    }

    TestConfig secretAccessKey(String v) {
        secretAccessKey = v;
        return this;
    }

    TestConfig endpointUrl(String v) {
        endpointUrl = v;
        return this;
    }

    TestConfig forcePathStyle(boolean v) {
        forcePathStyle = v;
        return this;
    }

    TestConfig accountId(String v) {
        accountId = v;
        return this;
    }

    TestConfig provider(String v) {
        provider = v;
        return this;
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    @Override
    public String getEndpointUrl() {
        return endpointUrl;
    }

    @Override
    public String getRootPath() {
        return ".";
    }

    @Override
    public boolean forcePathStyle() {
        return forcePathStyle;
    }

    @Override
    public String getChecksumValidation() {
        return "when_required";
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public int getListCacheTtlSeconds() {
        return 0;
    }

    @Override
    public String getProvider() {
        return provider;
    }
}
