package dev.themeinerlp.bluemap.s3.storage;

import de.bluecolored.bluemap.common.config.ConfigurationException;
import de.bluecolored.bluemap.common.config.storage.StorageConfig;
import de.bluecolored.bluemap.core.storage.Storage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class S3StorageConfiguration extends StorageConfig implements S3Configuration {
    @Override
    public Storage createStorage() throws ConfigurationException {
        return null;
    }

    @Override
    public String getBucketName() {
        return "";
    }

    @Override
    public String getRegion() {
        return "";
    }

    @Override
    public String getAccessKeyId() {
        return "";
    }

    @Override
    public String getSecretAccessKey() {
        return "";
    }

    @Override
    public String getEndpointUrl() {
        return "";
    }

    @Override
    public String getPathStyleAccessEnabled() {
        return "";
    }
}
