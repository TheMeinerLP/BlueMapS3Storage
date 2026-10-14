# Provider detection

The addon resolves every storage configuration through one of two provider profiles: `r2` or `generic`.

## Selection

1. If `provider` is non-blank, the profile with that id is used. The value is trimmed and compared case-insensitively. An unknown value fails with `Unknown provider '<value>', expected one of: r2, generic`.
2. Otherwise the profiles are tried in this order, and the first match is used:
   1. `r2`
   2. `generic` (always matches)

## Profile `r2` is selected automatically when

- `account-id` is non-blank and `endpoint-url` is blank or equal to the built-in default `http://localhost:9000`, or
- the host of `endpoint-url` ends with `.r2.cloudflarestorage.com`.

## Resolved connection settings

| Setting | Profile `r2` | Profile `generic` |
|---------|--------------|-------------------|
| Endpoint | The R2 `endpoint-url` if one is set, otherwise `https://<account-id>.r2.cloudflarestorage.com` | `endpoint-url` as configured |
| Path-style access | Always on | `force-path-style` as configured |
| Region | Always `auto` | `region` as configured; `us-east-1` when blank |

- With the `r2` profile, a warning is logged when `region` is set to anything other than blank, `Minio`, `auto` or `us-east-1`.
- With the `generic` profile, a blank endpoint selects AWS S3. A non-blank endpoint selects the S3-compatible client; an endpoint that does not start with `https` uses plain HTTP.

## Validation

- `provider: "r2"` without `account-id` and without an R2 `endpoint-url` fails at startup.
- The `r2` profile with a blank or placeholder `access-key-id` or `secret-access-key` fails at startup and names the key.
- `bucket-name`, `region`, `access-key-id` and `secret-access-key` must be non-empty for every profile.

## Startup log line

One INFO line is logged at startup. It contains the profile, the endpoint (the account ID masked as `***`), the bucket, the region, the path-style flag and the checksum validation mode.

For the reasoning behind these rules see [How provider profiles work](../explanation/how-provider-profiles-work.md).
