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

import java.net.URI;
import java.nio.file.FileSystem;
import java.util.Objects;
import software.amazon.nio.spi.s3.S3FileSystemProvider;
import software.amazon.nio.spi.s3.S3XFileSystemProvider;

final class S3FileSystemFactory {
    private static final String AWS_REGION_KEY = "aws.region";
    public static final String DEFAULT_AWS_REGION = "us-east-1";

    public record S3Fs(FileSystem fileSystem, URI uri) {}

    private static S3FileSystemProvider PROVIDER;

    private S3FileSystemFactory() {}

    public static S3Fs build(S3Configuration cfg) {
        Objects.requireNonNull(cfg, "cfg");
        if (cfg.getBucketName() == null || cfg.getBucketName().isBlank()) {
            throw new IllegalArgumentException("bucketName is required");
        }

        final boolean thirdParty = cfg.getEndpointUrl() != null && !cfg.getEndpointUrl().isBlank();
        try {
            final URI uri;
            System.setProperty(AWS_REGION_KEY, cfg.getRegion() != null ? cfg.getRegion() : DEFAULT_AWS_REGION);
            System.setProperty("aws.accessKeyId", cfg.getAccessKeyId());
            System.setProperty("aws.secretAccessKey", cfg.getSecretAccessKey());
            if (thirdParty) {
                var url = URI.create(cfg.getEndpointUrl());
                if (!url.toString().startsWith("https")) {
                    System.setProperty("s3.spi.endpoint-protocol", "http");
                }
                if (cfg.forcePathStyle()) {
                    System.setProperty("s3.spi.force-path-style", "true");
                }
                PROVIDER = new S3XFileSystemProvider();
                String userInfo = buildUserInfo(cfg);
                uri = new URI("s3x", userInfo, url.getHost(), url.getPort(), "/" + cfg.getBucketName(), null, null);
            } else {
                // AWS S3 – the provider uses the default region/credentials chain
                PROVIDER = new S3FileSystemProvider();

                uri = new URI("s3","/" + cfg.getBucketName(), null, null);
            }
            System.out.println("Using S3 FileSystem Provider: " + PROVIDER.getClass().getName());
            System.out.println("S3 URI: " + uri);
            FileSystem fs =  PROVIDER.getFileSystem(uri);
            return new S3Fs(fs, uri);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build S3 FileSystem", e);
        }
    }
    private static String buildUserInfo(S3Configuration cfg) {
        if (cfg.getAccessKeyId() == null || cfg.getSecretAccessKey() == null) return null;
        return cfg.getAccessKeyId() + ":" + cfg.getSecretAccessKey();
    }
}
