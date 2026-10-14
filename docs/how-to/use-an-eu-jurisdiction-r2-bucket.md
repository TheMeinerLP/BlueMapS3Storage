# Use an EU-jurisdiction R2 bucket

Buckets created with the EU jurisdiction are served from a different hostname than default buckets. Set that hostname as the endpoint.

Prerequisites: a working R2 setup as in [Put your map on Cloudflare R2](../tutorial/put-your-map-on-cloudflare-r2.md), with an EU-jurisdiction bucket and an API token for it.

1. In your storage configuration, set `endpoint-url` to `https://<account-id>.eu.r2.cloudflarestorage.com`, using your own account ID.
2. Keep `account-id` set or remove it. The host ends in `.r2.cloudflarestorage.com`, so the R2 profile is selected either way. See [Provider detection](../reference/provider-detection.md).
3. Leave `region` and `force-path-style` alone. The R2 profile sets both itself.
4. Restart the server completely. `/bluemap reload` does not apply storage changes.
5. Check the startup log line: it shows the endpoint with the account ID masked, and the profile `r2`.

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "bluemap-maps"
endpoint-url: "https://<account-id>.eu.r2.cloudflarestorage.com"
access-key-id: "<your Access Key ID>"
secret-access-key: "<your Secret Access Key>"
```

If the credentials are rejected, test them outside BlueMap with [Verify credentials with the AWS CLI](verify-credentials-with-the-aws-cli.md), using the same EU endpoint.

See also: [`endpoint-url`](../reference/configuration.md#endpoint-url), [`account-id`](../reference/configuration.md#account-id).
