# Errors and log messages

Messages as they appear in the server log, what they mean, and the task page that resolves them. Provider messages can differ slightly in wording between providers.

## SignatureDoesNotMatch

The provider could not verify the request signature, or answered 403. Usual causes: a wrong or mistyped access key id or secret, extra quotes or trailing whitespace around a value, or a wrong `account-id` (it must be the 32-character hexadecimal account ID, not a bucket name or zone ID).

Resolve with: [Verify credentials with the AWS CLI](../how-to/verify-credentials-with-the-aws-cli.md).

## AccessDenied

The credentials were accepted, but are not allowed to perform the operation. Usual causes: a read-only token, or a token scoped to a different bucket.

Resolve with: [Verify credentials with the AWS CLI](../how-to/verify-credentials-with-the-aws-cli.md).

## InvalidRegionName

The endpoint did not accept the configured `region`. For Cloudflare R2 the addon sets the region itself; for other providers `region` must be a value the provider knows.

See: [Provider detection](provider-detection.md), [Connect to other S3-compatible providers](../how-to/connect-to-other-s3-compatible-providers.md).

## 400 Bad Request

Logged for example as `PutObject => 400`, with tile saves failing. Usual cause on non-AWS providers: a request that carries a checksum the provider does not accept.

Resolve with: [Fix 400 Bad Request on non-AWS providers](../how-to/fix-400-bad-request-on-non-aws-providers.md).

## A header you provided implies functionality that is not implemented

Also seen as `STREAMING-UNSIGNED-PAYLOAD-TRAILER not implemented` or `not implemented` for an `x-amz-checksum-*` header. The provider rejects the checksum trailer or header of a `when_supported` request.

Resolve with: [Fix 400 Bad Request on non-AWS providers](../how-to/fix-400-bad-request-on-non-aws-providers.md). Background: [Why checksum validation exists](../explanation/why-checksum-validation-exists.md).

## Connection refused (localhost:9000)

The addon connected to `http://localhost:9000`, which is the built-in default of `endpoint-url`. Cause: no `endpoint-url` was set, and no `account-id` selected the R2 profile, so the default endpoint was used.

Resolve with: set `endpoint-url` (or `account-id` for R2) as in [Connect to MinIO](../how-to/connect-to-minio.md), [Connect to AWS S3](../how-to/connect-to-aws-s3.md) or [Put your map on Cloudflare R2](../tutorial/put-your-map-on-cloudflare-r2.md).

## Provider 'r2' needs either 'account-id' or an 'endpoint-url' ending in .r2.cloudflarestorage.com

`provider: "r2"` is set, but neither `account-id` nor an R2 `endpoint-url` is. BlueMap reports it as a configuration error.

Resolve with: [Put your map on Cloudflare R2](../tutorial/put-your-map-on-cloudflare-r2.md) or [Use an EU-jurisdiction R2 bucket](../how-to/use-an-eu-jurisdiction-r2-bucket.md). See [Provider detection](provider-detection.md).

## Cloudflare R2 needs a real '<key>' (create an R2 API token); it is missing or still the built-in placeholder

`<key>` is `access-key-id` or `secret-access-key`. The value is blank or still the built-in default.

Resolve with: [Put your map on Cloudflare R2](../tutorial/put-your-map-on-cloudflare-r2.md) (step 2 creates the token).

## Unknown provider '...', expected one of: r2, generic

The `provider` key holds a value that is not a profile id. See [Provider detection](provider-detection.md).

## S3 bucket name is required / AWS region is required / AWS access key ID is required / AWS secret access key is required

A required key is empty. See the [Configuration reference](configuration.md).

## Failed to build S3 FileSystem

Wrapper message. The reason is in the `Caused by:` lines below it. See [Collect logs for a support request](../how-to/collect-logs-for-a-support-request.md).
