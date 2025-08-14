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
- Support for path-style access for compatibility with various S3 implementations
- Seamless integration with BlueMap's storage system

## Requirements

- BlueMap 5.3 or higher
- Java 21 or higher
- Access to an S3-compatible storage service

## Installation

1. Download the latest release of BlueMapS3Storage from the [releases page](https://github.com/TheMeinerLP/BlueMapS3Storage/releases)
2. Place the JAR file in the `plugins/BlueMap/addons` directory of your server
3. Restart your server or reload BlueMap

## Configuration

To use S3 storage with BlueMap, you need to modify your BlueMap configuration to use the S3 storage type. Add the following to your `bluemap.conf` file:

```hocon
storage {
  type: "themeinerlp:s3"
  
  # S3 bucket name
  bucketName: "bluemap-storage"
  
  # AWS region or "Minio" for MinIO
  region: "us-east-1"
  
  # S3 credentials
  accessKeyId: "your-access-key"
  secretAccessKey: "your-secret-key"
  
  # Optional: Custom endpoint URL for S3-compatible services
  # Leave empty for AWS S3
  endpointUrl: "http://localhost:9000"
  
  # Optional: Enable path-style access instead of virtual-hosted style
  # Set to "true" for MinIO and some other S3-compatible services
  pathStyleAccessEnabled: "true"
  
  # Optional: Compression type to use for storing data
  # Options include: "gzip" (default), "none", or other compression types supported by BlueMap
  compression: "gzip"
}
```

### Configuration Options

| Option | Description | Default |
|--------|-------------|---------|
| `bucketName` | The name of the S3 bucket to use | `bluemap-storage` |
| `region` | The AWS region where the bucket is located | `Minio` |
| `accessKeyId` | The AWS access key ID for authentication | `bluemap` |
| `secretAccessKey` | The AWS secret access key for authentication | `bluemap-secret` |
| `endpointUrl` | Optional: The endpoint URL for S3-compatible services (leave empty for AWS S3) | `http://localhost:9000` |
| `pathStyleAccessEnabled` | Optional: Enable path-style access instead of virtual-hosted style (true/false) | `true` |
| `compression` | Optional: The compression type to use for storing data (options: "gzip", "none", or other types supported by BlueMap) | `gzip` |

## Usage Examples

### AWS S3

```hocon
storage {
  type: "themeinerlp:s3"
  bucketName: "my-bluemap-bucket"
  region: "us-east-1"
  accessKeyId: "AKIAIOSFODNN7EXAMPLE"
  secretAccessKey: "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
  endpointUrl: ""
  pathStyleAccessEnabled: "false"
  compression: "gzip"
}
```

### MinIO

```hocon
storage {
  type: "themeinerlp:s3"
  bucketName: "bluemap"
  region: "Minio"
  accessKeyId: "minioadmin"
  secretAccessKey: "minioadmin"
  endpointUrl: "http://minio-server:9000"
  pathStyleAccessEnabled: "true"
  compression: "gzip"
}
```

### DigitalOcean Spaces

```hocon
storage {
  type: "themeinerlp:s3"
  bucketName: "bluemap-maps"
  region: "nyc3"
  accessKeyId: "your-spaces-key"
  secretAccessKey: "your-spaces-secret"
  endpointUrl: "https://nyc3.digitaloceanspaces.com"
  pathStyleAccessEnabled: "false"
  compression: "gzip"
}
```

## Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/TheMeinerLP/BlueMapS3Storage.git
   ```

2. Build the project using Gradle:
   ```
   ./gradlew build
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