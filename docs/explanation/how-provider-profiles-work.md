# How provider profiles work

The addon has one storage type, `themeinerlp:s3`, for every S3-compatible service. Providers differ in small but unforgiving ways, so the addon puts those differences into provider profiles instead of into the S3 engine.

## The problem profiles solve

An S3 connection needs an endpoint, an addressing style (path-style or virtual-hosted) and a region. For AWS S3 the SDK derives all three. For other services you must supply them, and each provider has its own conventions:

- Cloudflare R2 has an endpoint that contains your account ID, expects the region name `auto`, and the addon addresses its buckets by path.
- MinIO and Ceph usually need path-style access and accept almost any region name.
- DigitalOcean Spaces and Backblaze B2 use virtual-hosted style and a region that must match the endpoint.

A user who only wants to use R2 should not have to know any of that. Without profiles the answer would be a long configuration where one wrong value gives an error like `InvalidRegionName` or a `400`.

## Why R2 got its own profile

R2 is the provider where the correct values are fully determined by one piece of information, the account ID. The `r2` profile therefore derives the endpoint from it, always turns on path-style access, and always uses the region `auto`. Overriding the region would only create failures, so the profile ignores a different value and logs a warning.

The profile is also picked when the endpoint is an R2 hostname, so that jurisdiction-specific endpoints such as `<account-id>.eu.r2.cloudflarestorage.com` get the same treatment without extra keys.

## Why the built-in endpoint default is treated as "not set"

A storage configuration written by BlueMap's defaults carries `endpoint-url: "http://localhost:9000"`, which is the address of a local MinIO. A user who adds only an `account-id` has not chosen that endpoint. Treating the untouched default like a blank value lets `account-id` alone be enough.

## Why the generic profile exists

Everything else is the generic profile: it takes `endpoint-url`, `force-path-style` and `region` exactly as written. It always matches, so a provider without a profile still works. You can also choose a profile explicitly with `provider`, which is mainly useful for tooling that generates configurations.

## Trade-offs

- Explicit-looking keys (`region`, `force-path-style`) are silently overruled in the `r2` profile. The rules are documented in [Provider detection](../reference/provider-detection.md) and the startup log line shows the values that are really used.
- A profile for a new provider is a small class. Providers whose conventions are just three configurable values do not need one.
