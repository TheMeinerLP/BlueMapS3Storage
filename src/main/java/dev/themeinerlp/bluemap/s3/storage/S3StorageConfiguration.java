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

import de.bluecolored.bluemap.common.config.ConfigurationException;
import de.bluecolored.bluemap.common.config.storage.StorageConfig;
import de.bluecolored.bluemap.common.debug.DebugDump;
import de.bluecolored.bluemap.core.storage.Storage;
import de.bluecolored.bluemap.core.storage.compression.Compression;
import de.bluecolored.bluemap.core.util.Key;
import java.util.Optional;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class S3StorageConfiguration extends StorageConfig implements S3Configuration {

    /** Built-in endpoint-url default (a local MinIO); R2 detection treats it as "not set". */
    static final String DEFAULT_ENDPOINT_URL = "http://localhost:9000";
    /** Built-in region default; R2 detection treats it as "not set". */
    static final String DEFAULT_REGION = "Minio";
    /** Built-in access-key-id placeholder; R2 refuses to start with it. */
    static final String DEFAULT_ACCESS_KEY_ID = "bluemap";
    /** Built-in secret-access-key placeholder; R2 refuses to start with it. */
    static final String DEFAULT_SECRET_ACCESS_KEY = "bluemap-secret";
    
    @Comment("The name of the S3 bucket to use")
    private String bucketName = "bluemap-storage";
    
    @Comment("""
            The region used to sign requests. Generic S3: set the region of your bucket.
            Cloudflare R2: leave unset - R2 always uses "auto" (the defaults "Minio",
            "us-east-1" and an empty value are mapped to it, any other value is ignored with a warning).
            """)
    private String region = DEFAULT_REGION;
    
    @Comment("The AWS access key ID for authentication. Leave empty together with secret-access-key to use the default AWS credential chain (environment, profile, ECS task role, EC2 instance profile)")
    private String accessKeyId = DEFAULT_ACCESS_KEY_ID;
    
    @Comment("The AWS secret access key for authentication. Leave empty together with access-key-id to use the default AWS credential chain")
    @DebugDump(exclude = true)
    private String secretAccessKey = DEFAULT_SECRET_ACCESS_KEY;
    
    @Comment("""
            The endpoint URL for S3-compatible services (leave empty for AWS S3).
            Cloudflare R2: leave unset and set account-id instead, or set the full R2 endpoint
            (e.g. https://<account-id>.eu.r2.cloudflarestorage.com for the EU jurisdiction);
            an endpoint ending in .r2.cloudflarestorage.com selects the R2 profile automatically.
            """)
    @DebugDump(exclude = true)
    private String endpointUrl = DEFAULT_ENDPOINT_URL;

    @Comment("The compression type to use for storing data (default: gzip)")
    private String compression = "gzip"; // Default compression type

    @Comment("The root path in the S3 bucket (default: empty, meaning the root of the bucket)")
    private String rootPath = ".";

    @Comment("Force path style access for S3 (default: false, use virtual-hosted style)")
    private boolean forcePathStyle = false;

    @Comment("""
            Controls when the AWS SDK attaches/validates request and response checksums
            (aws.requestChecksumCalculation / aws.responseChecksumValidation). Since SDK 2.30
            the default is "when_supported", which adds a checksum to every request - many
            S3-compatible stores (Ceph RGW included) reject that with a bare 400 Bad Request
            and silently fail tile saves. Use "when_required" (default here) for those; set it
            back to "when_supported" if you're on real AWS S3 or a provider that handles it fine.
            """)
    private String checksumValidation = "when_required";

    @Comment("""
            Optional: explicitly picks which provider profile resolves this config, one of:
            "r2", "generic". Leave empty to auto-detect: setting account-id below, or an endpoint-url
            ending in .r2.cloudflarestorage.com, picks "r2"; otherwise "generic"
            (uses endpoint-url/region/force-path-style as-is).
            Only needed if you want to be explicit rather than rely on auto-detection.
            """)
    private String provider = "";

    @Comment("""
            Optional: Cloudflare account ID. If set and endpoint-url is empty or left at its
            default, the R2 profile is used: the endpoint becomes
            https://<account-id>.r2.cloudflarestorage.com, path-style access is enabled and the
            region is "auto". Leave empty for AWS S3 or any other S3-compatible provider.
            """)
    @DebugDump(exclude = true)
    private String accountId = "";

    @Comment("""
            How long (in seconds) to cache the result of listing available maps (mapIds()).
            0 disables caching (default). Directory listings are a billed "Class A" operation
            on R2 and similar providers, so a short cache here cuts down on repeated listing
            calls; this list rarely changes at runtime (only when a map is added/removed), so
            a stale cache for a few minutes is normally harmless.
            """)
    private int listCacheTtlSeconds = 0;

    @Override
    public Storage createStorage() throws ConfigurationException {
        // Validate required configuration
        if (bucketName == null || bucketName.isEmpty()) {
            throw new ConfigurationException("S3 bucket name is required");
        }
        
        // Resolve the provider profile up front so R2 misconfiguration is reported as a clean
        // ConfigurationException instead of a stack trace from S3FileSystemFactory.
        final ProviderProfile.ResolvedEndpoint resolved;
        try {
            resolved = ProviderProfiles.resolve(this);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }

        // R2 maps an empty region to "auto", so only the other profiles need one.
        if (!R2Profile.ID.equals(resolved.profileId()) && (region == null || region.isEmpty())) {
            throw new ConfigurationException("AWS region is required");
        }
        
        try {
            StaticCredentials.isConfigured(accessKeyId, secretAccessKey);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
        
        // Create and return the S3Storage instance with the configured compression
        return new S3Storage(this, getCompression());
    }

    public Compression getCompression() {
        return Compression.REGISTRY.get(Key.bluemap(compression));
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
        return Optional.ofNullable(rootPath)
                .map(String::trim)
                .filter(path -> !path.isEmpty())
                .orElse(".");
    }

    @Override
    public boolean forcePathStyle() {
        return forcePathStyle;
    }

    @Override
    public String getChecksumValidation() {
        return ChecksumValidationMode.parse(checksumValidation).propertyValue();
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public int getListCacheTtlSeconds() {
        return Math.max(0, listCacheTtlSeconds);
    }

    @Override
    public String getProvider() {
        return provider;
    }
}
