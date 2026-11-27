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

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.nio.spi.s3.CacheableS3Client;
import software.amazon.nio.spi.s3.S3ClientProvider;
import software.amazon.nio.spi.s3.config.S3NioSpiConfiguration;

/**
 * Keeps one S3 client per file system for its whole lifetime.
 * <p>
 * The nio-spi library caches its clients for one hour without closing the expired ones, and every
 * CRT client owns native event-loop threads, so the library leaks threads on a long-running server
 * (upstream: <a href="https://github.com/awslabs/aws-java-nio-spi-for-s3/issues/784">awslabs/aws-java-nio-spi-for-s3#784</a>).
 * This provider hands out the first client forever and closes it together with the storage.
 * Remove it once the library closes or stops expiring its clients.
 * <p>
 * The client is always the standard (Netty) one, never the library's default CRT client. A CRT
 * client finishes closing on a native thread after {@code close()} has returned; on a stopping
 * server the plugin class loader is closed by then, the native callback fails to load a class and
 * the CRT aborts the whole JVM while worlds are still being saved. The standard client is fully
 * closed when {@code close()} returns.
 */
final class PinnedS3ClientProvider extends S3ClientProvider implements AutoCloseable {

    private S3AsyncClient client;
    private boolean closed;

    PinnedS3ClientProvider(S3NioSpiConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected synchronized S3AsyncClient generateClient(String bucket) {
        if (closed) {
            throw new IllegalStateException("S3 client provider is closed");
        }
        if (client == null) {
            client = new CacheableS3Client(standardClientBuilder().build());
        }
        return client;
    }

    /** Same settings the library applies to its CRT client, on the standard client builder. */
    private S3AsyncClientBuilder standardClientBuilder() {
        var builder = S3AsyncClient.builder().crossRegionAccessEnabled(true);
        var endpointUri = configuration.endpointUri();
        if (endpointUri != null) {
            builder.endpointOverride(endpointUri);
        }
        var credentialsProvider = configuration.getCredentialsProvider();
        if (credentialsProvider != null) {
            builder.credentialsProvider(credentialsProvider);
        }
        var region = configuration.getRegion();
        if (region != null) {
            builder.region(Region.of(region));
        }
        return builder.forcePathStyle(configuration.getForcePathStyle());
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
