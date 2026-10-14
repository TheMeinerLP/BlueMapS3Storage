# Why checksum validation exists

The `checksum-validation` key exists because a default in the AWS SDK changed and many S3-compatible services did not follow.

## What changed

From version 2.30 of the AWS SDK for Java, the default for `aws.requestChecksumCalculation` and `aws.responseChecksumValidation` is `when_supported`. The SDK then attaches a flexible checksum, for example a CRC32, to every upload and validates one on every download. For streaming uploads the checksum travels as a trailer, in a request body encoded as `aws-chunked`.

AWS S3 handles this. Several other implementations do not. Cloudflare R2 and Ceph RADOS Gateway reject the chunked trailer form, with responses such as `400 Bad Request` or a message that a header implies functionality that is not implemented.

## Why the failure looks mysterious

BlueMap saves tiles in the background. A rejected upload shows up as a failed save in the log, not as a startup error, and some providers answer with a bare `400` that does not name the checksum. So the setup seems fine until tiles are missing.

## What the addon does

The addon uses `aws-java-nio-spi-for-s3` 2.5.0, which runs the CRT-based S3 client and its transfer manager under the hood. The addon passes `checksum-validation` to the SDK as both system properties before it builds the file system.

- With `when_required` (the addon's default), the SDK sends a checksum only when an operation demands one. Uploads then go out with `x-amz-content-sha256: UNSIGNED-PAYLOAD` and without `aws-chunked` encoding.
- With `when_supported`, the SDK's own default applies. Use it where you know the provider handles it, such as AWS S3.

Any other value of the key is treated as `when_required`.

## Trade-off

`when_required` gives up automatic end-to-end integrity checks that the flexible checksums provide. Map tiles can be rendered again, so the addon favours compatibility over the extra check.

See [`checksum-validation`](../reference/configuration.md#checksum-validation) and, for a fix, [Fix 400 Bad Request on non-AWS providers](../how-to/fix-400-bad-request-on-non-aws-providers.md).
