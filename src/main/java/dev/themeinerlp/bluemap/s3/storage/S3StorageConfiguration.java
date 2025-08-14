package dev.themeinerlp.bluemap.s3.storage;

import de.bluecolored.bluemap.common.config.ConfigurationException;
import de.bluecolored.bluemap.common.config.storage.StorageConfig;
import de.bluecolored.bluemap.core.storage.Storage;
import de.bluecolored.bluemap.core.storage.compression.Compression;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class S3StorageConfiguration extends StorageConfig implements S3Configuration {
    
    @Comment("The name of the S3 bucket to use")
    private String bucketName = "";
    
    @Comment("The AWS region where the bucket is located")
    private String region = "";
    
    @Comment("The AWS access key ID for authentication")
    private String accessKeyId = "";
    
    @Comment("The AWS secret access key for authentication")
    private String secretAccessKey = "";
    
    @Comment("Optional: The endpoint URL for S3-compatible services (leave empty for AWS S3)")
    private String endpointUrl = "";
    
    @Comment("Optional: Enable path-style access instead of virtual-hosted style (true/false)")
    private String pathStyleAccessEnabled = "false";
    
    @Override
    public Storage createStorage() throws ConfigurationException {
        // Validate required configuration
        if (bucketName == null || bucketName.isEmpty()) {
            throw new ConfigurationException("S3 bucket name is required");
        }
        
        if (region == null || region.isEmpty()) {
            throw new ConfigurationException("AWS region is required");
        }
        
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            throw new ConfigurationException("AWS access key ID is required");
        }
        
        if (secretAccessKey == null || secretAccessKey.isEmpty()) {
            throw new ConfigurationException("AWS secret access key is required");
        }
        
        // Create and return the S3Storage instance
        // Using null for Compression for now
        // In a real implementation, we would need to find the proper way to create a Compression instance
        return new S3Storage(this, null);
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
    public String getPathStyleAccessEnabled() {
        return pathStyleAccessEnabled;
    }
}
