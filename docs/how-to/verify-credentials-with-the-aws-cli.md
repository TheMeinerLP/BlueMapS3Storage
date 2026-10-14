# Verify credentials with the AWS CLI

Test your access key outside BlueMap. A key that can list a bucket may still lack write permission, so check both.

Prerequisites: the [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) on any machine, and the same endpoint, bucket and keys as in your storage configuration.

1. Export the credentials. For R2 and most other providers, also export the region your provider expects (`auto` for R2) and ask the CLI to send checksums only when required:

   ```sh
   export AWS_ACCESS_KEY_ID="<access-key-id>"
   export AWS_SECRET_ACCESS_KEY="<secret-access-key>"
   export AWS_DEFAULT_REGION="auto"
   export AWS_REQUEST_CHECKSUM_CALCULATION=when_required
   export AWS_RESPONSE_CHECKSUM_VALIDATION=when_required
   ```

2. Check read access by listing the bucket. Omit `--endpoint-url` for AWS S3.

   ```sh
   aws s3api list-objects-v2 --bucket <bucket-name> --max-items 5 \
     --endpoint-url https://<account-id>.r2.cloudflarestorage.com
   ```

3. Check write access by uploading a small file, then deleting it:

   ```sh
   echo test > bluemap-write-test.txt
   aws s3 cp bluemap-write-test.txt s3://<bucket-name>/bluemap-write-test.txt \
     --endpoint-url https://<account-id>.r2.cloudflarestorage.com
   aws s3 rm s3://<bucket-name>/bluemap-write-test.txt \
     --endpoint-url https://<account-id>.r2.cloudflarestorage.com
   ```

4. Read the result:
   - All three commands succeed: the key works. The problem is in the BlueMap configuration, so compare each key with the [Configuration reference](../reference/configuration.md).
   - The listing works and the upload fails with `AccessDenied`: the key is read-only or scoped to another bucket. Create a token with write permission for this bucket.
   - `SignatureDoesNotMatch`: the access key id or the secret is wrong or contains stray characters. See [Errors and log messages](../reference/errors.md#signaturedoesnotmatch).

For an EU-jurisdiction R2 bucket, use the `https://<account-id>.eu.r2.cloudflarestorage.com` endpoint. For a provider with a self-chosen region, set `AWS_DEFAULT_REGION` to that region.
