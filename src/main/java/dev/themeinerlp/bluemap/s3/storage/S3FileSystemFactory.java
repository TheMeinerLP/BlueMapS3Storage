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
                // Credentials travel in the URI's userInfo below, scoped to this one
                // filesystem - deliberately NOT also written to the aws.accessKeyId/
                // aws.secretAccessKey system properties here. Those are process-wide mutable
                // state; any other credential resolution in this JVM that reads the default
                // AWS credentials chain (e.g. an async/background client the nio-spi-s3
                // library spins up internally, distinct from the client that honors the URI)
                // would see whatever this storage's config last wrote there, independent of
                // which storage's requests are actually in flight at that moment. Observed in
                // production as intermittent, unpredictable 403s specifically on the async
                // read-ahead path (S3ReadAheadByteChannel) against a third-party store (Ceph
                // RGW), with the rejected requests showing no identifiable user at all -
                // consistent with a credential resolution racing against a value this method
                // (or another instance of it) had already overwritten.
                String userInfo = buildUserInfo(cfg);
                uri = new URI("s3x", userInfo, url.getHost(), url.getPort(), "/" + cfg.getBucketName(), null, null);
            } else {
                // AWS S3 – the provider uses the default region/credentials chain, which has
                // no URI-embedded alternative for this code path, so these two properties are
                // the only way to hand it credentials.
                System.setProperty("aws.accessKeyId", cfg.getAccessKeyId());
                System.setProperty("aws.secretAccessKey", cfg.getSecretAccessKey());
                PROVIDER = new S3FileSystemProvider();

                uri = new URI("s3","/" + cfg.getBucketName(), null, null);
            }
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
