package dev.themeinerlp.bluemap.s3.storage;

import software.amazon.nio.spi.s3.S3FileSystemProvider;
import software.amazon.nio.spi.s3.S3XFileSystemProvider;

import java.net.URI;
import java.nio.file.FileSystem;
import java.util.Objects;

final class S3FileSystemFactory {
    public record S3Fs(FileSystem fileSystem, URI uri) {}

    private static S3FileSystemProvider PROVIDER;

    private S3FileSystemFactory() {}

    public static S3Fs build(S3Configuration cfg) {
        Objects.requireNonNull(cfg, "cfg");
        if (cfg.getBucketName() == null || cfg.getBucketName().isBlank()) {
            throw new IllegalArgumentException("bucketName is required");
        }

        final boolean thirdParty = cfg.getEndpointUrl() != null && !cfg.getEndpointUrl().isBlank();
        try {
            final URI uri;
            if (!thirdParty) {
                // AWS S3 – the provider uses the default region/credentials chain
                PROVIDER = new S3FileSystemProvider();
                System.setProperty("aws.region", cfg.getRegion() != null ? cfg.getRegion() : "us-east-1");
                uri = URI.create("s3://" + cfg.getBucketName());
            } else {
                PROVIDER = new S3XFileSystemProvider();
                var url = URI.create(cfg.getEndpointUrl());
                System.setProperty("s3.spi.endpoint-protocol", url.toString().startsWith("https") ? "https" : "http");
                System.setProperty("aws.region", cfg.getRegion() != null ? cfg.getRegion() : "us-east-1");
                String userInfo = buildUserInfo(cfg);
                uri = new URI("s3x", userInfo, url.getHost(), url.getPort(), "/" + cfg.getBucketName(), null, null);
            }
            System.out.println("Using S3 FileSystem Provider: " + PROVIDER.getClass().getName());
            System.out.println("Using S3 URI: " + uri);
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
