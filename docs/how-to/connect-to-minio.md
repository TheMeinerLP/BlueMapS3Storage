# Connect to MinIO

Prerequisites: a running MinIO server and a bucket for BlueMap. MinIO does not create the bucket for you.

1. Create `storages/minio.conf` next to the `packs` directory (see [Storage type and file locations](../reference/storage-types-and-paths.md)).
2. Set `endpoint-url` to the MinIO API address, for example `http://minio-server:9000`. An `http://` endpoint is used without TLS.
3. Set `bucket-name` to your bucket.
4. Set `access-key-id` and `secret-access-key` to a MinIO user's credentials.
5. Set `force-path-style` to `true`.
6. Leave `region` at its default (`Minio`) or set a region name such as `us-east-1`.
7. In a map configuration, set `storage: "minio"`.
8. Restart the server completely.

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "bluemap"
endpoint-url: "http://minio-server:9000"
access-key-id: "<minio user>"
secret-access-key: "<minio password>"
force-path-style: true
```

## Try it against a local MinIO

The repository contains `docker-local-minio/compose.yml`, which starts MinIO with the API on port 9000 and the web console on port 9001. The root user is `bluemap` and the password is `bluemap_s3_impl`.

1. Run `docker compose up -d` in `docker-local-minio/`.
2. Open `http://localhost:9001`, sign in, and create a bucket.
3. Use `http://localhost:9000` as `endpoint-url`, the root user and password as keys, and your bucket name as `bucket-name`.

If startup ends in `Connection refused` for `localhost:9000`, see [Errors and log messages](../reference/errors.md#connection-refused-localhost9000).

See also: [Configuration reference](../reference/configuration.md).
