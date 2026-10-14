# Fix 400 Bad Request on non-AWS providers

Use this when tile saves fail with `400 Bad Request` (often logged as `PutObject => 400`) on R2, Ceph or another S3-compatible store, or when the provider answers that a header or a checksum feature is not implemented.

1. Open your storage configuration.
2. Look for `checksum-validation`. If it is set to `when_supported`, remove the line or set it to `when_required`. Any value the addon does not recognise also falls back to `when_required`.
3. Restart the server completely. `/bluemap reload` does not apply storage changes.
4. Check the startup log line: it prints the active `checksum-validation` value.
5. Trigger a save, for example with `/bluemap render`, and confirm that the error is gone.

If the error remains with `when_required`, find out whether the credentials may write: [Verify credentials with the AWS CLI](verify-credentials-with-the-aws-cli.md). If they can, [collect logs](collect-logs-for-a-support-request.md) and open an issue.

See also: [`checksum-validation`](../reference/configuration.md#checksum-validation), [Why checksum validation exists](../explanation/why-checksum-validation-exists.md).
