# BlueMapS3Storage

A storage addon for [BlueMap](https://github.com/BlueMap-Minecraft/BlueMap) that enables storing map data in S3-compatible storage services.

## Overview

BlueMapS3Storage is an addon for BlueMap that provides the ability to store map data in S3-compatible storage services, such as:

- Amazon S3
- MinIO
- DigitalOcean Spaces
- Backblaze B2
- Any other S3-compatible storage service

This addon is particularly useful for:
- Servers with distributed architectures
- Environments where local storage is limited or not persistent
- Setups that require high availability and redundancy
- Multi-server networks that need to share the same map data

## Features

- Store BlueMap data in any S3-compatible storage service
- Configure custom endpoints for self-hosted S3 solutions
- Support for path-style access for compatibility with various S3 implementations (like MinIO)
- Seamless integration with BlueMap's storage system

## Requirements

- BlueMap 5.3 or higher
- Java 21 or higher
- Access to an S3-compatible storage service

## Installation

1. Download the latest release of BlueMapS3Storage from the [releases page](https://github.com/TheMeinerLP/BlueMapS3Storage/releases)
2. Place the JAR file in the `plugins/BlueMap/packs` directory of your server
3. Restart your server or reload BlueMap

## Configuration

To use S3 storage with BlueMap, you need to create or modify the S3 storage configuration file. Create a file named `s3.conf` in the `plugins/BlueMap/storages` directory with the following content:

```hocon
##                          ##
##         BlueMap          ##
##      Storage-Config      ##
##                          ##

# The storage-type of this storage.
# Depending on this setting, different config-entries are allowed/expected in this config file.
# Don't change this value! (If you want a different storage-type, check out the other example-configs)
storage-type: "themeinerlp:s3"

# The compression-type that bluemap will use to compress generated map-data.
# Available compression-types are:
#  - gzip
#  - zstd
#  - deflate
#  - none
# The default is: gzip
compression: gzip

# The S3 storage
bucket-name: "bluemap-storage"

# AWS region or "Minio" for MinIO
region: "Minio"

# S3 credentials
access-key-id: "your-access-key"
secret-access-key: "your-secret-key"

# Optional: Custom endpoint URL for S3-compatible services
# Leave empty for AWS S3
endpoint-url: "http://localhost:9000"

# Optional: The root path in the S3 bucket where BlueMap data will be stored
# Default is "." (root of the bucket)
root-path: "."

# Optional: Force path style access for S3 (needed for MinIO)
# Default is false (use virtual-hosted style)
force-path-style: true
```

### Configuration Options

| Option | Description | Default |
|--------|-------------|---------|
| `storage-type` | The storage type identifier (don't change this value) | `themeinerlp:s3` |
| `compression` | The compression type to use for storing data (options: "gzip", "zstd", "deflate", "none") | `gzip` |
| `bucket-name` | The name of the S3 bucket to use | `bluemap-storage` |
| `region` | The AWS region where the bucket is located | `Minio` |
| `access-key-id` | The AWS access key ID for authentication | `bluemap` |
| `secret-access-key` | The AWS secret access key for authentication | `bluemap-secret` |
| `endpoint-url` | Optional: The endpoint URL for S3-compatible services (leave empty for AWS S3) | `http://localhost:9000` |
| `root-path` | Optional: The root path in the S3 bucket where BlueMap data will be stored | `.` |
| `force-path-style` | Optional: Force path style access for S3 (needed for MinIO) | `false` |

## Usage Examples

### AWS S3

```hocon
##                          ##
##         BlueMap          ##
##      Storage-Config      ##
##                          ##

storage-type: "themeinerlp:s3"
compression: gzip
bucket-name: "my-bluemap-bucket"
region: "us-east-1"
access-key-id: "AKIAIOSFODNN7EXAMPLE"
secret-access-key: "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
endpoint-url: ""
root-path: "."
force-path-style: false
```

### MinIO

```hocon
##                          ##
##         BlueMap          ##
##      Storage-Config      ##
##                          ##

storage-type: "themeinerlp:s3"
compression: gzip
bucket-name: "bluemap"
region: "us-east-1"
access-key-id: "minioadmin"
secret-access-key: "minioadmin"
endpoint-url: "http://minio-server:9000"
root-path: "."
force-path-style: true
```

### DigitalOcean Spaces

```hocon
##                          ##
##         BlueMap          ##
##      Storage-Config      ##
##                          ##

storage-type: "themeinerlp:s3"
compression: gzip
bucket-name: "bluemap-maps"
region: "nyc3"
access-key-id: "your-spaces-key"
secret-access-key: "your-spaces-secret"
endpoint-url: "https://nyc3.digitaloceanspaces.com"
root-path: "."
force-path-style: false
```

## Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/TheMeinerLP/BlueMapS3Storage.git
   ```

2. Build the project using Gradle:
   ```
   ./gradlew shadowJar
   ```

3. The built JAR file will be located in the `build/libs` directory.

## License

This project is licensed under the [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE).

## Credits

- Developed by [TheMeinerLP](https://github.com/TheMeinerLP) and [contributors](https://github.com/TheMeinerLP/BlueMapS3Storage/graphs/contributors)
- Uses [BlueMap](https://github.com/BlueMap-Minecraft/BlueMap) by [Blue](https://github.com/TBlueF)
- Uses [AWS Java NIO SPI for S3](https://github.com/awslabs/aws-java-nio-spi-for-s3) for S3 filesystem integration

## Support

If you encounter any issues or have questions, please [open an issue](https://github.com/TheMeinerLP/BlueMapS3Storage/issues) on the GitHub repository.