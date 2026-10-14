# Connect to AWS S3

Prerequisites: an S3 bucket, and an IAM user whose access key may list, read, write and delete objects in that bucket.

1. Create `storages/s3.conf` next to the `packs` directory (see [Storage type and file locations](../reference/storage-types-and-paths.md)).
2. Set `storage-type`, `bucket-name`, `region`, `access-key-id` and `secret-access-key`.
3. Set `endpoint-url` to an empty string. An empty endpoint makes the addon use AWS S3 directly.
4. Set `force-path-style` to `false`.
5. Set `checksum-validation` to `when_supported` only if you want the SDK's newer checksum behaviour. AWS S3 accepts either value. See [Why checksum validation exists](../explanation/why-checksum-validation-exists.md).
6. In a map configuration, set `storage: "s3"`.
7. Restart the server completely.

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "my-bluemap-bucket"
region: "us-east-1"
access-key-id: "<access key id>"
secret-access-key: "<secret access key>"
endpoint-url: ""
force-path-style: false
```

Use the region the bucket was created in as `region`.

If you get `AccessDenied` or `SignatureDoesNotMatch`, check the key outside BlueMap: [Verify credentials with the AWS CLI](verify-credentials-with-the-aws-cli.md).

See also: [Configuration reference](../reference/configuration.md).
