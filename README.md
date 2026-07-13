# BlueMapS3Storage

A storage addon for [BlueMap](https://github.com/BlueMap-Minecraft/BlueMap) that stores map data
in S3-compatible storage services (Amazon S3, Cloudflare R2, MinIO, DigitalOcean Spaces,
Backblaze B2, or anything else that speaks the S3 API) instead of the local filesystem.

Useful for distributed server architectures, environments without persistent local storage, and
multi-server networks that need to share the same map data.

## Requirements

- BlueMap 5.3 or higher
- Java 21 or higher
- Access to an S3-compatible storage service

## Quick start

1. Download the latest release from the [releases page](https://github.com/TheMeinerLP/BlueMapS3Storage/releases)
   and drop the jar in `./plugins/BlueMap/packs/` (Spigot/Paper), `./config/bluemap/packs/`
   (Sponge/Forge/Fabric), or `./config/packs/` (CLI).
2. Create a storage config file with at least `storage-type`, `bucket-name`, `access-key-id`,
   `secret-access-key`, and `endpoint-url` (leave empty for AWS S3).
3. Reference the storage from your BlueMap config and restart/reload.

See the **[Wiki](https://github.com/TheMeinerLP/BlueMapS3Storage/wiki)** for everything else:

- [Installation](https://github.com/TheMeinerLP/BlueMapS3Storage/wiki/Installation) — exact paths per platform
- [Configuration](https://github.com/TheMeinerLP/BlueMapS3Storage/wiki/Configuration) — full option reference
- [Provider Examples](https://github.com/TheMeinerLP/BlueMapS3Storage/wiki/Provider-Examples) — AWS S3, MinIO, DigitalOcean Spaces
- [Cloudflare R2](https://github.com/TheMeinerLP/BlueMapS3Storage/wiki/Cloudflare-R2) — R2-specific setup, cost notes, troubleshooting
- [Building From Source](https://github.com/TheMeinerLP/BlueMapS3Storage/wiki/Building-From-Source)

## License

[GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE)

## Credits

- Developed by [TheMeinerLP](https://github.com/TheMeinerLP) and [contributors](https://github.com/TheMeinerLP/BlueMapS3Storage/graphs/contributors)
- Uses [BlueMap](https://github.com/BlueMap-Minecraft/BlueMap) by [Blue](https://github.com/TBlueF)
- Uses [AWS Java NIO SPI for S3](https://github.com/awslabs/aws-java-nio-spi-for-s3) for S3 filesystem integration

## Support

[Open an issue](https://github.com/TheMeinerLP/BlueMapS3Storage/issues) on GitHub.
