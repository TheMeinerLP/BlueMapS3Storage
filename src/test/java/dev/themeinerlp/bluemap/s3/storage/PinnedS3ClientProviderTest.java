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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.nio.spi.s3.CacheableS3Client;
import software.amazon.nio.spi.s3.config.S3NioSpiConfiguration;

class PinnedS3ClientProviderTest {

    private static final String BUCKET = "bucket";

    private static PinnedS3ClientProvider newProvider() {
        // Standard (non-CRT) client: builds without native libraries and makes no network call.
        var configuration = new S3NioSpiConfiguration()
                .withEndpoint("localhost:9")
                .withEndpointProtocol("http")
                .withRegion("us-east-1")
                .withBucketName(BUCKET)
                .withCredentials("a", "b");
        var provider = new PinnedS3ClientProvider(configuration);
        provider.setCustomHeadersEnabled(true);
        return provider;
    }

    @Test
    void repeatedCallsReturnTheSameClient() {
        var provider = newProvider();
        try {
            S3AsyncClient first = provider.generateClient(BUCKET);
            S3AsyncClient second = provider.generateClient(BUCKET);
            assertSame(first, second, "the pinned client must not be replaced on later calls");
        } finally {
            provider.close();
        }
    }

    @Test
    void closeClosesThePinnedClient() {
        var provider = newProvider();
        var client = (CacheableS3Client) provider.generateClient(BUCKET);
        provider.close();
        assertTrue(client.isClosed(), "close() must close the pinned client");
    }

    @Test
    void closeIsIdempotent() {
        var provider = newProvider();
        provider.generateClient(BUCKET);
        provider.close();
        assertDoesNotThrow(provider::close, "a second close() must be a no-op");
    }

    @Test
    void generateClientAfterCloseThrows() {
        var provider = newProvider();
        provider.generateClient(BUCKET);
        provider.close();
        assertThrows(IllegalStateException.class, () -> provider.generateClient(BUCKET));
    }
}
