package dev.themeinerlp.bluemap.s3.storage;

public sealed interface S3Configuration permits S3StorageConfiguration {

    String getBucketName();

    String getRegion();

    String getAccessKeyId();

    String getSecretAccessKey();

    String getEndpointUrl();

    String getPathStyleAccessEnabled();

}
