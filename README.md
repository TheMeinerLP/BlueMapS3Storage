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

1. Download the latest jar from the [releases page](https://github.com/TheMeinerLP/BlueMapS3Storage/releases) and put it into BlueMap's `packs` directory.
2. Create a storage configuration with `storage-type: "themeinerlp:s3"`, a bucket, an account ID or endpoint, and an access key.
3. Set `storage` in a map configuration to that storage and restart the server. The [tutorial](docs/tutorial/put-your-map-on-cloudflare-r2.md) walks through it end to end.

## Documentation

Start at the [documentation index](docs/index.md), or jump straight in:

- [Tutorial](docs/tutorial/put-your-map-on-cloudflare-r2.md): learn by putting a map on Cloudflare R2.
- [How-to guides](docs/how-to/): solve a specific task, such as connecting to AWS S3 or MinIO.
- [Reference](docs/reference/): look up configuration keys, file locations and error messages.
- [Explanation](docs/explanation/): understand why the addon works the way it does.

## License

[GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE)

## Credits

- Developed by [TheMeinerLP](https://github.com/TheMeinerLP) and [contributors](https://github.com/TheMeinerLP/BlueMapS3Storage/graphs/contributors)
- Uses [BlueMap](https://github.com/BlueMap-Minecraft/BlueMap) by [Blue](https://github.com/TBlueF)
- Uses [AWS Java NIO SPI for S3](https://github.com/awslabs/aws-java-nio-spi-for-s3) for S3 filesystem integration

## Support

[Open an issue](https://github.com/TheMeinerLP/BlueMapS3Storage/issues) on GitHub.
