# Cloudflare R2

R2 is S3-compatible, so it works through the same `themeinerlp:s3` storage type as any other
S3-compatible provider. There's no separate `themeinerlp:r2` storage type; two small
R2-specific conveniences are built into the regular S3 config instead.

## Setup

1. Create a bucket in the Cloudflare dashboard under **R2**.
2. Under **Manage R2 API Tokens**, create a token with **Object Read & Write** permissions and
   copy the Access Key ID / Secret Access Key immediately, you won't see them again.
3. Copy your **Account ID** from the R2 overview page.
4. Create a storage config file:

   | Platform | Path |
   |---|---|
   | Spigot/Paper | `./plugins/BlueMap/storages/r2.conf` |
   | Sponge/Forge/Fabric | `./config/bluemap/storages/r2.conf` |
   | CLI | `./config/storages/r2.conf` |

   ```hocon
   storage-type: "themeinerlp:s3"
   compression: gzip
   bucket-name: "your-bucket-name"
   account-id: "your-account-id"
   access-key-id: "your-r2-access-key"
   secret-access-key: "your-r2-secret-key"
   ```

   Setting `account-id` derives the endpoint (`https://<account-id>.r2.cloudflarestorage.com`)
   and enables path-style access automatically, since R2 only supports path-style. Leave
   `endpoint-url` and `force-path-style` unset when using `account-id` this way.

   If you'd rather set the endpoint yourself (e.g. for a jurisdictional/EU-restricted bucket),
   skip `account-id` and use `endpoint-url`/`force-path-style` directly instead, same as any
   other S3-compatible provider:

   ```hocon
   storage-type: "themeinerlp:s3"
   compression: gzip
   bucket-name: "your-bucket-name"
   access-key-id: "your-r2-access-key"
   secret-access-key: "your-r2-secret-key"
   region: "auto"
   endpoint-url: "https://your-account-id.eu.r2.cloudflarestorage.com"
   force-path-style: true
   ```

5. Reference the storage in your main BlueMap config:

   ```hocon
   storages: {
     r2: "storages/r2.conf"
   }

   maps: [
     { id: "world", storage: "r2" }
   ]
   ```

6. Restart or reload BlueMap and confirm objects show up in the bucket.

## Cost note: directory listing cache

Directory listings (used for `mapIds()`, i.e. once per storage init/reload to enumerate
configured maps, not per-tile) are a billed "Class A" operation on R2. Since that list rarely
changes at runtime, you can cache it for a while:

```hocon
list-cache-ttl-seconds: 300
```

`0` (default) disables caching. This only affects map enumeration, not tile reads/writes.

## Troubleshooting

**`SignatureDoesNotMatch` / 403.** Almost always a copy-paste issue: regenerate the R2 API
token, make sure the secret key isn't wrapped in extra quotes or has trailing whitespace, and
double check the account ID is the exact 32-character hex string from the R2 overview page
(not the bucket name or a zone ID).

**`PutObject => 400` / tile saves failing silently.** Not R2-specific, see the
[`checksum-validation`](../README.md#configuration-options) option. R2 (like most non-AWS
S3-compatible stores) needs `checksum-validation: when_required` (the default).
