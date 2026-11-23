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

import software.amazon.awssdk.services.s3.S3AsyncClient;
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
            client = super.generateClient(bucket);
        }
        return client;
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
