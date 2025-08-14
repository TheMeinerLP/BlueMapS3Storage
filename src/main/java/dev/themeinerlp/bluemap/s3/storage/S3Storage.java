package dev.themeinerlp.bluemap.s3.storage;

import de.bluecolored.bluemap.core.storage.MapStorage;
import de.bluecolored.bluemap.core.storage.Storage;
import de.bluecolored.bluemap.core.storage.compression.Compression;

import java.io.IOException;
import java.util.stream.Stream;

public final class S3Storage implements Storage {

    private final S3Configuration configuration;
    private final Compression compression;

    public S3Storage(S3Configuration configuration, Compression compression) {
        this.configuration = configuration;
        this.compression = compression;
    }

    @Override
    public void initialize() throws IOException {

    }

    @Override
    public MapStorage map(String s) {
        return null;
    }

    @Override
    public Stream<String> mapIds() throws IOException {
        return Stream.empty();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
