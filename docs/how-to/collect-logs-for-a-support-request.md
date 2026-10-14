# Collect logs for a support request

Prepare a log that lets a maintainer reproduce your problem, without leaking secrets.

1. Stop the server and start it again completely, so that the log contains the whole startup of the addon.
2. Reproduce the problem, for example by running `/bluemap update <map-id>`.
3. Open `logs/latest.log` in the server directory.
4. Find the first error. Later errors are usually consequences of it.
5. Copy the first error with its whole stack trace, including every `Caused by:` line down to the last one. The last `Caused by:` is usually the real reason.
6. Copy the addon's startup line that lists profile, endpoint, bucket, region, path-style and checksum validation.
7. Redact secrets before sharing: replace the access key id, the secret access key and your Cloudflare account ID with placeholders. Check the endpoint URL and any quoted configuration too.
8. Paste the redacted text to [mclo.gs](https://mclo.gs) and copy the link it gives you.
9. Open an issue at <https://github.com/TheMeinerLP/BlueMapS3Storage/issues>. State the provider, the addon version, the BlueMap version, your storage configuration without secrets, what you expected, and add the mclo.gs link.

Related: [Verify credentials with the AWS CLI](verify-credentials-with-the-aws-cli.md) shows whether the problem is in the credentials or in BlueMap.
