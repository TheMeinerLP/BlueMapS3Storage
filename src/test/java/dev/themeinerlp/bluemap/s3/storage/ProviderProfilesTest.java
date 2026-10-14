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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProviderProfilesTest {

    private static final String EU_ENDPOINT = "https://acc123.eu.r2.cloudflarestorage.com";

    private static ProviderProfile.ResolvedEndpoint resolve(TestConfig cfg) {
        return ProviderProfiles.resolve(cfg);
    }

    @Test
    void accountIdOnlySelectsR2WithDerivedEndpointAndAutoRegion() {
        var r = resolve(new TestConfig().accountId("acc123"));
        assertEquals("r2", r.profileId());
        assertEquals("https://acc123.r2.cloudflarestorage.com", r.endpointUrl());
        assertEquals("auto", r.region());
        assertTrue(r.forcePathStyle(), "R2 requires path-style");
        assertTrue(r.warnings().isEmpty(), "defaults must not warn");
    }

    @Test
    void accountIdWithBlankEndpointSelectsR2() {
        var r = resolve(new TestConfig().accountId("acc123").endpointUrl(" "));
        assertEquals("r2", r.profileId());
        assertEquals("https://acc123.r2.cloudflarestorage.com", r.endpointUrl());
    }

    @Test
    void accountIdWithNullEndpointSelectsR2() {
        var r = resolve(new TestConfig().accountId("acc123").endpointUrl(null));
        assertEquals("r2", r.profileId());
    }

    @Test
    void explicitEuEndpointWithoutAccountIdSelectsR2() {
        var r = resolve(new TestConfig().endpointUrl(EU_ENDPOINT));
        assertEquals("r2", r.profileId());
        assertEquals(EU_ENDPOINT, r.endpointUrl());
        assertTrue(r.forcePathStyle(), "R2 requires path-style");
        assertEquals("auto", r.region());
    }

    @Test
    void r2HostMatchIsCaseInsensitive() {
        var r = resolve(new TestConfig().endpointUrl("https://ACC.EU.R2.CloudflareStorage.com"));
        assertEquals("r2", r.profileId());
        assertEquals("https://ACC.EU.R2.CloudflareStorage.com", r.endpointUrl());
    }

    @Test
    void explicitR2EndpointWinsOverDerivedEndpointWhenAccountIdAlsoSet() {
        var r = resolve(new TestConfig().accountId("other").endpointUrl(EU_ENDPOINT));
        assertEquals(EU_ENDPOINT, r.endpointUrl());
    }

    @Test
    void r2HostSuffixMustMatchHostNotPath() {
        var r = resolve(new TestConfig().endpointUrl("http://minio.local:9000/.r2.cloudflarestorage.com"));
        assertEquals("generic", r.profileId());
    }

    @Test
    void defaultRegionMapsToAuto() {
        assertEquals("auto", resolve(new TestConfig().accountId("a").region("Minio")).region());
        assertEquals("auto", resolve(new TestConfig().accountId("a").region("minio")).region());
    }

    @Test
    void blankRegionMapsToAuto() {
        assertEquals("auto", resolve(new TestConfig().accountId("a").region(" ")).region());
        assertEquals("auto", resolve(new TestConfig().accountId("a").region(null)).region());
    }

    @Test
    void autoAndUsEast1RegionsMapToAutoWithoutWarning() {
        var auto = resolve(new TestConfig().accountId("a").region("auto"));
        var us = resolve(new TestConfig().accountId("a").region("us-east-1"));
        assertEquals("auto", auto.region());
        assertEquals("auto", us.region());
        assertTrue(auto.warnings().isEmpty(), "auto is valid");
        assertTrue(us.warnings().isEmpty(), "us-east-1 is an accepted alias");
    }

    @Test
    void otherRegionStillUsesAutoAndSignalsWarning() {
        var r = resolve(new TestConfig().accountId("a").region("eu-west-1"));
        assertEquals("auto", r.region());
        assertEquals(1, r.warnings().size(), "exactly one warning expected");
        assertTrue(r.warnings().get(0).contains("eu-west-1"), "warning names the ignored region");
        assertTrue(r.warnings().get(0).contains("auto"), "warning names the accepted value");
    }

    @Test
    void genericEndpointStaysGenericAndUnchanged() {
        var r = resolve(new TestConfig().endpointUrl("http://minio:9000").region("Minio").forcePathStyle(true));
        assertEquals("generic", r.profileId());
        assertEquals("http://minio:9000", r.endpointUrl());
        assertEquals("Minio", r.region());
        assertTrue(r.forcePathStyle());
    }

    @Test
    void genericKeepsPathStyleFalseAsConfigured() {
        var r = resolve(new TestConfig().endpointUrl("http://minio:9000"));
        assertFalse(r.forcePathStyle());
    }

    @Test
    void defaultsWithoutAccountIdStayGeneric() {
        var r = resolve(new TestConfig());
        assertEquals("generic", r.profileId());
        assertEquals(S3StorageConfiguration.DEFAULT_ENDPOINT_URL, r.endpointUrl());
    }

    @Test
    void explicitGenericOverridesAccountId() {
        var r = resolve(new TestConfig().provider("generic").accountId("acc").region("Minio"));
        assertEquals("generic", r.profileId());
        assertEquals(S3StorageConfiguration.DEFAULT_ENDPOINT_URL, r.endpointUrl());
        assertEquals("Minio", r.region());
    }

    @Test
    void explicitGenericOverridesR2Endpoint() {
        var r = resolve(new TestConfig().provider("generic").endpointUrl(EU_ENDPOINT).region("x"));
        assertEquals("generic", r.profileId());
        assertEquals("x", r.region());
    }

    @Test
    void explicitR2WithoutAccountIdOrR2EndpointFailsNamingBothKeys() {
        var e = assertThrows(IllegalArgumentException.class, () -> resolve(new TestConfig().provider("r2")));
        assertTrue(e.getMessage().contains("account-id"), e.getMessage());
        assertTrue(e.getMessage().contains("endpoint-url"), e.getMessage());
    }

    @Test
    void explicitR2WithAccountIdResolves() {
        var r = resolve(new TestConfig().provider("R2").accountId("acc"));
        assertEquals("https://acc.r2.cloudflarestorage.com", r.endpointUrl());
    }

    @Test
    void placeholderAccessKeyWithR2FailsNamingKey() {
        var e = assertThrows(
                IllegalArgumentException.class,
                () -> resolve(new TestConfig().accountId("a").accessKeyId("bluemap")));
        assertTrue(e.getMessage().contains("access-key-id"), e.getMessage());
    }

    @Test
    void blankAccessKeyWithR2FailsNamingKey() {
        var e = assertThrows(
                IllegalArgumentException.class,
                () -> resolve(new TestConfig().accountId("a").accessKeyId(" ")));
        assertTrue(e.getMessage().contains("access-key-id"), e.getMessage());
    }

    @Test
    void placeholderSecretWithR2FailsNamingKeyWithoutLeakingIt() {
        var e = assertThrows(
                IllegalArgumentException.class,
                () -> resolve(new TestConfig().accountId("a").secretAccessKey("bluemap-secret")));
        assertTrue(e.getMessage().contains("secret-access-key"), e.getMessage());
        assertFalse(e.getMessage().contains("bluemap-secret"), "secret must not be printed");
    }

    @Test
    void blankSecretWithR2FailsNamingKey() {
        var e = assertThrows(
                IllegalArgumentException.class,
                () -> resolve(new TestConfig().accountId("a").secretAccessKey("")));
        assertTrue(e.getMessage().contains("secret-access-key"), e.getMessage());
    }

    @Test
    void placeholderCredentialsAreAcceptedForGeneric() {
        var r = resolve(new TestConfig().accessKeyId("bluemap").secretAccessKey("bluemap-secret"));
        assertEquals("generic", r.profileId());
    }

    @Test
    void maskEndpointHidesAccountIdOfR2Host() {
        assertEquals("https://***.eu.r2.cloudflarestorage.com", R2Profile.maskEndpoint(EU_ENDPOINT));
        assertEquals(
                "https://***.r2.cloudflarestorage.com",
                R2Profile.maskEndpoint("https://acc123.r2.cloudflarestorage.com"));
    }

    @Test
    void maskEndpointKeepsPortAndLeavesOtherHostsUntouched() {
        assertEquals("http://minio:9000", R2Profile.maskEndpoint("http://minio:9000"));
        assertEquals(
                "https://***.r2.cloudflarestorage.com:8443",
                R2Profile.maskEndpoint("https://acc.r2.cloudflarestorage.com:8443"));
    }

    @Test
    void maskEndpointToleratesNullAndBlank() {
        assertEquals("", R2Profile.maskEndpoint(null));
        assertEquals("", R2Profile.maskEndpoint(" "));
    }
}
