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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.bluecolored.bluemap.core.storage.MapStorage;
import de.bluecolored.bluemap.core.storage.Storage;
import de.bluecolored.bluemap.core.storage.compression.Compression;
import de.bluecolored.bluemap.core.storage.file.FileMapStorage;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class S3Storage implements Storage {

    private final S3Configuration configuration;
    private final Compression compression;
    private FileSystem s3FileSystem;
    private boolean closed = false;
    private final LoadingCache<String, FileMapStorage> mapStorages;

    public S3Storage(S3Configuration configuration, Compression compression) {
        this.configuration = configuration;
        this.compression = compression;
        mapStorages = Caffeine.newBuilder().build(this::create);
    }

    @Override
    public void initialize() throws IOException {
        if (isClosed()) {
            throw new IOException("Storage is closed");
        }
        
        try {
            S3FileSystemFactory.S3Fs handle = S3FileSystemFactory.build(configuration);
            this.s3FileSystem = handle.fileSystem();
        } catch (Exception e) {
            throw new IOException("Failed to initialize S3 storage", e);
        }
    }

    private Path getRooPath() {
        return s3FileSystem.getPath(".").toAbsolutePath().normalize();
    }

    public FileMapStorage create(String mapId) {
        Path mapPath = getRooPath().resolve(mapId);
        return new FileMapStorage(mapPath, compression, false);
    }

    @Override
    public MapStorage map(String mapId) {
        if (isClosed()) {
            throw new IllegalStateException("Storage is closed");
        }
        
        if (mapId == null || mapId.isEmpty()) {
            throw new IllegalArgumentException("Map ID cannot be null or empty");
        }
        return mapStorages.get(mapId);
    }

    @Override
    public Stream<String> mapIds() throws IOException {
        if (isClosed()) {
            throw new IOException("Storage is closed");
        }
        
        // List all directories in the root path
        return Files.list(getRooPath())
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(Path::toString);
    }

    @Override
    public boolean isClosed() {
        return closed || (s3FileSystem != null && !s3FileSystem.isOpen());
    }

    @Override
    public void close() throws IOException {
        if (!isClosed() && s3FileSystem != null) {
            s3FileSystem.close();
            closed = true;
        }
    }
}
