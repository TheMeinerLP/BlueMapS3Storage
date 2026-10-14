# Put your map on Cloudflare R2

In this tutorial you will make a BlueMap server on Paper save its map tiles in a Cloudflare R2 bucket. When you finish, you will have seen your own tiles appear in the bucket.

We use Cloudflare R2 because it needs the fewest settings: you give the addon an account ID and an access key, and it works out the rest.

You need:

- a Paper server with BlueMap 5.3 or newer installed, running Java 21 or newer
- a Cloudflare account
- the server's file system, so you can add files to `plugins/BlueMap/`

## 1. Create a bucket

1. Sign in to the Cloudflare dashboard and open **R2 Object Storage**.
2. Select **Create bucket**.
3. Name the bucket `bluemap-maps` and create it.

On the R2 overview page, find your **Account ID** (a 32-character hexadecimal string) and keep the page open. You will need the ID in step 4.

## 2. Create an API token

1. On the R2 overview page, select **Manage API tokens**, then create an **Account API token**.
2. Give the token the **Object Read & Write** permission.
3. Limit the token to the `bluemap-maps` bucket.
4. Create the token and copy the **Access Key ID** and the **Secret Access Key** right away. Cloudflare shows the secret only once.

## 3. Install the addon

1. Download the latest jar from the [releases page](https://github.com/TheMeinerLP/BlueMapS3Storage/releases).
2. Copy it into `plugins/BlueMap/packs/` on your server.

## 4. Write the storage configuration

Create the file `plugins/BlueMap/storages/r2.conf` with this content. Replace the three placeholder values with the ones you collected.

```hocon
storage-type: "themeinerlp:s3"
bucket-name: "bluemap-maps"
account-id: "<your 32-character account ID>"
access-key-id: "<your Access Key ID>"
secret-access-key: "<your Secret Access Key>"
```

Because you set `account-id` and left `endpoint-url` alone, the addon recognises Cloudflare R2. It builds the endpoint `https://<account-id>.r2.cloudflarestorage.com`, turns on path-style access and uses the region `auto` by itself. You do not set any of these.

## 5. Point a map at the storage

1. Open the configuration file of your overworld map in `plugins/BlueMap/maps/`.
2. Set its storage to the file name of the storage configuration, without `.conf`:

   ```hocon
   storage: "r2"
   ```

3. Save the file.

## 6. Restart the server

Stop the server completely and start it again.

## 7. Check that it works

1. Read the server log during startup. You will see one line from the addon, like this:

   ```
   S3 storage: profile=r2, endpoint=https://***.r2.cloudflarestorage.com, bucket=bluemap-maps, region=auto, path-style=true, checksum-validation=when_required
   ```

   Your account ID is masked as `***`.
2. Run `/bluemap update <map-id>` in the server console, replacing `<map-id>` with the file name of the map configuration you edited, without `.conf`. Wait a minute.
3. Open the `bluemap-maps` bucket in the Cloudflare dashboard and refresh. You will see objects for your map.

If you see the log line and the objects, your map is stored on R2.

## Where to go next

- Your bucket lives in the EU jurisdiction: [Use an EU-jurisdiction R2 bucket](../how-to/use-an-eu-jurisdiction-r2-bucket.md).
- Startup fails or no objects appear: [Verify credentials with the AWS CLI](../how-to/verify-credentials-with-the-aws-cli.md) and [Collect logs for a support request](../how-to/collect-logs-for-a-support-request.md).
- You want fewer billed requests: [Reduce R2 request costs](../how-to/reduce-r2-request-costs.md).
- You want to know what the addon does behind these five keys: [How provider profiles work](../explanation/how-provider-profiles-work.md).
