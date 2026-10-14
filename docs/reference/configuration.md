<!-- Generated from S3StorageConfiguration by `./gradlew generateConfigReference`. Do not edit by hand. -->

# Configuration reference

> Generated from `S3StorageConfiguration` by `./gradlew generateConfigReference` — do not edit by hand.

Keys are the field names of `S3StorageConfiguration` in lower-case dashed form, which is how BlueMap's configuration loader maps them. Defaults are the field initializers of a freshly constructed instance. Descriptions are the `@Comment` annotations of the fields.

| Key | Type | Default |
|-----|------|---------|
| [`storage-type`](#storage-type) | `String` | `"themeinerlp:s3"` |
| [`bucket-name`](#bucket-name) | `String` | `"bluemap-storage"` |
| [`region`](#region) | `String` | `"Minio"` |
| [`access-key-id`](#access-key-id) | `String` | `"bluemap"` |
| [`secret-access-key`](#secret-access-key) | `String` | `"bluemap-secret"` |
| [`endpoint-url`](#endpoint-url) | `String` | `"http://localhost:9000"` |
| [`compression`](#compression) | `String` | `"gzip"` |
| [`root-path`](#root-path) | `String` | `"."` |
| [`force-path-style`](#force-path-style) | `boolean` | `false` |
| [`checksum-validation`](#checksum-validation) | `String` | `"when_required"` |
| [`provider`](#provider) | `String` | `""` |
| [`account-id`](#account-id) | `String` | `""` |
| [`list-cache-ttl-seconds`](#list-cache-ttl-seconds) | `int` | `0` |

## storage-type

- Type: `String`
- Default: `"themeinerlp:s3"`

No description in the source.

## bucket-name

- Type: `String`
- Default: `"bluemap-storage"`

The name of the S3 bucket to use

## region

- Type: `String`
- Default: `"Minio"`

The region used to sign requests. Generic S3: set the region of your bucket. Cloudflare R2: leave unset - R2 always uses "auto" (the defaults "Minio", "us-east-1" and an empty value are mapped to it, any other value is ignored with a warning).

## access-key-id

- Type: `String`
- Default: `"bluemap"`

The AWS access key ID for authentication. Leave empty together with secret-access-key to use the default AWS credential chain (environment, profile, ECS task role, EC2 instance profile)

## secret-access-key

- Type: `String`
- Default: `"bluemap-secret"`
- Sensitive: excluded from BlueMap debug dumps (`@DebugDump(exclude = true)`)

The AWS secret access key for authentication. Leave empty together with access-key-id to use the default AWS credential chain

## endpoint-url

- Type: `String`
- Default: `"http://localhost:9000"`
- Sensitive: excluded from BlueMap debug dumps (`@DebugDump(exclude = true)`)

The endpoint URL for S3-compatible services (leave empty for AWS S3). Cloudflare R2: leave unset and set account-id instead, or set the full R2 endpoint (e.g. https://<account-id>.eu.r2.cloudflarestorage.com for the EU jurisdiction); an endpoint ending in .r2.cloudflarestorage.com selects the R2 profile automatically.

## compression

- Type: `String`
- Default: `"gzip"`

The compression type to use for storing data (default: gzip)

## root-path

- Type: `String`
- Default: `"."`

The root path in the S3 bucket (default: empty, meaning the root of the bucket)

## force-path-style

- Type: `boolean`
- Default: `false`

Force path style access for S3 (default: false, use virtual-hosted style)

## checksum-validation

- Type: `String`
- Default: `"when_required"`

Controls when the AWS SDK attaches/validates request and response checksums (aws.requestChecksumCalculation / aws.responseChecksumValidation). Since SDK 2.30 the default is "when_supported", which adds a checksum to every request - many S3-compatible stores (Ceph RGW included) reject that with a bare 400 Bad Request and silently fail tile saves. Use "when_required" (default here) for those; set it back to "when_supported" if you're on real AWS S3 or a provider that handles it fine.

## provider

- Type: `String`
- Default: `""`

Optional: explicitly picks which provider profile resolves this config, one of: "r2", "generic". Leave empty to auto-detect: setting account-id below, or an endpoint-url ending in .r2.cloudflarestorage.com, picks "r2"; otherwise "generic" (uses endpoint-url/region/force-path-style as-is). Only needed if you want to be explicit rather than rely on auto-detection.

## account-id

- Type: `String`
- Default: `""`
- Sensitive: excluded from BlueMap debug dumps (`@DebugDump(exclude = true)`)

Optional: Cloudflare account ID. If set and endpoint-url is empty or left at its default, the R2 profile is used: the endpoint becomes https://<account-id>.r2.cloudflarestorage.com, path-style access is enabled and the region is "auto". Leave empty for AWS S3 or any other S3-compatible provider.

## list-cache-ttl-seconds

- Type: `int`
- Default: `0`

How long (in seconds) to cache the result of listing available maps (mapIds()). 0 disables caching (default). Directory listings are a billed "Class A" operation on R2 and similar providers, so a short cache here cuts down on repeated listing calls; this list rarely changes at runtime (only when a map is added/removed), so a stale cache for a few minutes is normally harmless.
