# Connect to other S3-compatible providers

This page covers DigitalOcean Spaces, Backblaze B2 and Ceph. For any other provider that speaks the S3 API, follow the same steps with the values from the provider's documentation.

Prerequisites: a bucket and an access key that may list, read, write and delete objects.

1. Create a storage configuration next to the `packs` directory (see [Storage type and file locations](../reference/storage-types-and-paths.md)).
2. Set `endpoint-url` to the provider's S3 endpoint, including `https://`.
3. Set `region` to the region name the provider expects.
4. Set `force-path-style` to what the provider requires (`false` for virtual-hosted style, `true` for path style).
5. Set `bucket-name`, `access-key-id` and `secret-access-key`.
6. Leave `checksum-validation` at its default. If saves fail with `400 Bad Request`, see [Fix 400 Bad Request on non-AWS providers](fix-400-bad-request-on-non-aws-providers.md).
7. Reference the storage from a map configuration with `storage: "<file name without .conf>"` and restart the server completely.

## DigitalOcean Spaces

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "bluemap-maps"
region: "nyc3"
endpoint-url: "https://nyc3.digitaloceanspaces.com"
access-key-id: "<spaces key>"
secret-access-key: "<spaces secret>"
force-path-style: false
```

Replace `nyc3` in both `region` and `endpoint-url` with your Space's region.

## Backblaze B2

Use the S3-compatible endpoint shown for your bucket in the B2 console (it has the form `https://s3.<region>.backblazeb2.com`) and the region from the same page. Create an application key that is allowed to access the bucket.

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "bluemap-maps"
region: "<b2 region, for example eu-central-003>"
endpoint-url: "https://s3.<b2 region>.backblazeb2.com"
access-key-id: "<application key id>"
secret-access-key: "<application key>"
force-path-style: false
```

## Ceph (RADOS Gateway)

Use the URL of your RADOS Gateway as the endpoint. Path-style addressing is the safe choice unless your gateway is configured for virtual-hosted buckets.

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "bluemap"
region: "us-east-1"
endpoint-url: "https://rgw.example.com"
access-key-id: "<rgw access key>"
secret-access-key: "<rgw secret key>"
force-path-style: true
```

Ceph rejects the flexible checksums that `checksum-validation: when_supported` enables. Leave the key at `when_required`. Background: [Why checksum validation exists](../explanation/why-checksum-validation-exists.md).

See also: [Configuration reference](../reference/configuration.md), [Verify credentials with the AWS CLI](verify-credentials-with-the-aws-cli.md).
