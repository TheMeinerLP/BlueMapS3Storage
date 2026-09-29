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

        final ProviderProfile.ResolvedEndpoint resolved = ProviderProfiles.resolve(cfg);
        final boolean thirdParty = resolved.endpointUrl() != null && !resolved.endpointUrl().isBlank();
        try {
            final URI uri;
            String region = resolved.region() != null && !resolved.region().isBlank() ? resolved.region() : DEFAULT_AWS_REGION;
            System.setProperty(AWS_REGION_KEY, region);
            // AWS SDK for Java 2.30.0+ defaults to attaching a flexible checksum (e.g. a CRC32
            // trailer) to every PutObject and validating one on every GetObject. Many
            // third-party S3-compatible stores (Ceph RGW included) don't handle that request
            // shape and reject it with a bare "400 Bad Request", silently failing tile saves.
            // Configurable per-target since real AWS S3 (and some other providers) are fine
            // with the new default.
            System.setProperty("aws.requestChecksumCalculation", cfg.getChecksumValidation());
            System.setProperty("aws.responseChecksumValidation", cfg.getChecksumValidation());
            if (thirdParty) {
                var url = URI.create(resolved.endpointUrl());
                if (!url.toString().startsWith("https")) {
                    System.setProperty("s3.spi.endpoint-protocol", "http");
                }
                if (resolved.forcePathStyle()) {
                    System.setProperty("s3.spi.force-path-style", "true");
                }
                PROVIDER = new S3XFileSystemProvider();
                // Credentials go through the URI userInfo instead of the global
                // aws.accessKeyId/aws.secretAccessKey properties - see PR #83. No userInfo
                // when the keys are empty, so the SDK's default credential chain applies.
                String userInfo = StaticCredentials.userInfo(cfg.getAccessKeyId(), cfg.getSecretAccessKey());
                uri = new URI("s3x", userInfo, url.getHost(), url.getPort(), "/" + cfg.getBucketName(), null, null);
            } else {
                // AWS S3: no URI-embedded credentials, so these are the only way to set them.
                // With empty keys the properties are cleared and the SDK's default credential
                // chain (environment, profile, ECS task role, instance profile) applies.
                StaticCredentials.applyAwsSystemProperties(cfg.getAccessKeyId(), cfg.getSecretAccessKey());
                PROVIDER = new S3FileSystemProvider();

                // The bucket must be the URI host (s3://bucket); the provider rejects
                // s3:///bucket with "Bucket name cannot be null".
                uri = new URI("s3", cfg.getBucketName(), null, null);
            }
            FileSystem fs =  PROVIDER.getFileSystem(uri);
            return new S3Fs(fs, uri);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build S3 FileSystem", e);
        }
    }
}
