# BlueMapS3Storage documentation

BlueMapS3Storage is a storage addon for [BlueMap](https://github.com/BlueMap-Minecraft/BlueMap). It saves your map data in S3-compatible object storage such as Cloudflare R2, AWS S3 or MinIO instead of on the server's disk.

## Learn

Start here if you have not used the addon before.

- [Put your map on Cloudflare R2](tutorial/put-your-map-on-cloudflare-r2.md): a guided first run that ends with tiles in a bucket.

## Do a task

Pick the page that matches what you want to get done.

Connect to a provider:

- [Use an EU-jurisdiction R2 bucket](how-to/use-an-eu-jurisdiction-r2-bucket.md)
- [Connect to AWS S3](how-to/connect-to-aws-s3.md)
- [Connect to MinIO](how-to/connect-to-minio.md)
- [Connect to other S3-compatible providers](how-to/connect-to-other-s3-compatible-providers.md)

Check, fix and tune:

- [Verify credentials with the AWS CLI](how-to/verify-credentials-with-the-aws-cli.md)
- [Fix 400 Bad Request on non-AWS providers](how-to/fix-400-bad-request-on-non-aws-providers.md)
- [Reduce R2 request costs](how-to/reduce-r2-request-costs.md)
- [Collect logs for a support request](how-to/collect-logs-for-a-support-request.md)

Build and maintain:

- [Build from source](how-to/build-from-source.md)
- [Regenerate the configuration reference](how-to/regenerate-the-configuration-reference.md)

## Look something up

- [Configuration](reference/configuration.md): every config key, its type, default and description (generated from the source code).
- [Storage type and file locations](reference/storage-types-and-paths.md)
- [Provider detection](reference/provider-detection.md)
- [Compatibility](reference/compatibility.md)
- [Errors and log messages](reference/errors.md)

## Understand

- [How provider profiles work](explanation/how-provider-profiles-work.md)
- [Why checksum validation exists](explanation/why-checksum-validation-exists.md)
- [Where R2 costs come from](explanation/r2-costs.md)
- [Scope and non-goals](explanation/scope-and-non-goals.md)
