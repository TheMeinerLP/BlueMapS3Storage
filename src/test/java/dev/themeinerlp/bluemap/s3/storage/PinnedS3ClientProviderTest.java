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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.s3.DelegatingS3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.crossregion.S3CrossRegionAsyncClient;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncClient;
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
        return new PinnedS3ClientProvider(configuration);
    }

    private static PinnedS3ClientProvider newAwsProvider() {
        var configuration = new S3NioSpiConfiguration()
                .withRegion("eu-central-1")
                .withBucketName(BUCKET)
                .withCredentials("a", "b");
        return new PinnedS3ClientProvider(configuration);
    }

    /** Every client in the delegation chain, outermost first. */
    private static List<SdkClient> delegationChain(S3AsyncClient client) {
        List<SdkClient> chain = new ArrayList<>();
        SdkClient current = client;
        chain.add(current);
        while (current instanceof DelegatingS3AsyncClient delegating) {
            current = delegating.delegate();
            chain.add(current);
        }
        return chain;
    }

    private static void assertNoCrtClient(S3AsyncClient client) {
        for (SdkClient link : delegationChain(client)) {
            assertFalse(
                    link instanceof S3CrtAsyncClient,
                    "the CRT client aborts the JVM on shutdown, but found " + link.getClass().getName());
        }
    }

    @Test
    void customEndpointClientIsNotTheCrtClient() {
        var provider = newProvider();
        try {
            assertNoCrtClient(provider.generateClient(BUCKET));
        } finally {
            provider.close();
        }
    }

    @Test
    void awsClientIsNotTheCrtClient() {
        var provider = newAwsProvider();
        try {
            assertNoCrtClient(provider.generateClient(BUCKET));
        } finally {
            provider.close();
        }
    }

    @Test
    void clientFollowsBucketRegionRedirectsLikeTheCrtClientDid() {
        var provider = newAwsProvider();
        try {
            boolean crossRegion = delegationChain(provider.generateClient(BUCKET)).stream()
                    .anyMatch(S3CrossRegionAsyncClient.class::isInstance);
            assertTrue(crossRegion, "cross-region access must stay enabled for buckets outside the configured region");
        } finally {
            provider.close();
        }
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
