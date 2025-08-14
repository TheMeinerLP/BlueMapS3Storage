package dev.themeinerlp.bluemap.s3.storage;

import software.amazon.nio.spi.s3.S3FileSystemProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

final class S3FileSystemFactory {
    public record S3Fs(FileSystem fileSystem, Path bucketRoot, URI uri) {}

    private static final S3FileSystemProvider PROVIDER = new S3FileSystemProvider();

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
                uri = URI.create("s3://" + cfg.getBucketName() + "/");
            } else {
                // 3rd-Party (MinIO/Ceph/Wasabi, etc.) via s3x://
                var ep = Endpoint.parse(cfg.getEndpointUrl());
                String proto = pickProtocol(cfg, ep);
                System.setProperty("s3.spi.endpoint-protocol", proto);
                String userInfo = buildUserInfo(cfg);
                uri = new URI("s3x", userInfo, ep.host, ep.portOrDefault(proto), "/" + cfg.getBucketName() + "/", null, null);
            }

            FileSystem fs = getOrCreateFs(uri);
            return new S3Fs(fs, fs.getPath("/"), uri);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build S3 FileSystem", e);
        }
    }

    private static FileSystem getOrCreateFs(URI uri) {
        // Work explicitly through the S3FileSystemProvider (no global registry)
        try {
            return PROVIDER.getFileSystem(uri);
        } catch (FileSystemNotFoundException notFound) {
            try {
                return PROVIDER.newFileSystem(uri, Map.of());
            } catch (java.nio.file.FileSystemAlreadyExistsException exists) {
                return PROVIDER.getFileSystem(uri);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create S3 FileSystem for URI: " + uri, e);
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected error while creating S3 FileSystem for URI: " + uri, e);
            }
        }
    }

    private static String buildUserInfo(S3Configuration cfg) {
        if (cfg.getAccessKeyId() == null || cfg.getSecretAccessKey() == null) return null;
        return cfg.getAccessKeyId() + ":" + URLEncoder.encode(cfg.getSecretAccessKey(), StandardCharsets.UTF_8);
    }

    private static String pickProtocol(S3Configuration cfg, Endpoint ep) {
        String hint = cfg.getPathStyleAccessEnabled();
        if (hint != null && (hint.equalsIgnoreCase("http") || hint.equalsIgnoreCase("https"))) return hint.toLowerCase();
        if (ep.port != null && (ep.port == 9000 || ep.port == 9001)) return "http";
        return "https";
    }

    private record Endpoint(String host, Integer port) {
        static Endpoint parse(String raw) {
            String v = raw.trim();
            if (v.contains("://")) {
                URI u = URI.create(v);
                if (u.getHost() == null) throw new IllegalArgumentException("Invalid endpoint: " + raw);
                return new Endpoint(u.getHost(), u.getPort() == -1 ? null : u.getPort());
            }
            int idx = v.lastIndexOf(':');
            if (idx > -1 && idx == v.indexOf(':')) {
                try { return new Endpoint(v.substring(0, idx), Integer.parseInt(v.substring(idx + 1))); }
                catch (NumberFormatException e) { return new Endpoint(v.substring(0, idx), null); }
            }
            return new Endpoint(v, null);
        }
        int portOrDefault(String proto) { return port != null ? port : ("http".equalsIgnoreCase(proto) ? 80 : 443); }
    }
}
