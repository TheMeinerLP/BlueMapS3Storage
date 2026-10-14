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

- With the `r2` profile, a blank `region`, `Minio`, `auto` and `us-east-1` are accepted. Any other value is ignored with a logged warning, and `auto` is used.
- With the `generic` profile, a blank endpoint selects AWS S3. A non-blank endpoint selects the S3-compatible client; an endpoint that does not start with `https` uses plain HTTP.

## Validation

- `provider: "r2"` without `account-id` and without an R2 `endpoint-url` fails at startup with `Provider 'r2' needs either 'account-id' or an 'endpoint-url' ending in .r2.cloudflarestorage.com`.
- The `r2` profile with a blank or placeholder `access-key-id` or `secret-access-key` fails at startup with `Cloudflare R2 needs a real '<key>' (create an R2 API token); it is missing or still the built-in placeholder`, where `<key>` is the offending key.
- `bucket-name` must be non-empty for every profile. `region` must be non-empty for every profile except `r2`, which maps a blank region to `auto`.
- `access-key-id` and `secret-access-key` must be both set or both empty; the message is `Set both access-key-id and secret-access-key, or leave both empty to use the default AWS credential chain`. Both empty is valid for the `generic` profile, where the AWS default credential chain applies, but not for `r2`, which has no default credential chain.

## Startup log line

One INFO line is logged at startup:

```
S3 storage: profile=<id>, endpoint=<masked endpoint>, bucket=<bucket>, region=<region>, path-style=<true|false>, checksum-validation=<mode>
```

Example: `S3 storage: profile=r2, endpoint=https://***.r2.cloudflarestorage.com, bucket=bluemap-maps, region=auto, path-style=true, checksum-validation=when_required`. On an R2 endpoint the account ID is masked as `***`.

For the reasoning behind these rules see [How provider profiles work](../explanation/how-provider-profiles-work.md).
