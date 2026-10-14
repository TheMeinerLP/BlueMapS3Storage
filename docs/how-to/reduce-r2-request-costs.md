# Reduce R2 request costs

Cache the list of maps so the addon lists the bucket less often.

1. In your storage configuration, set `list-cache-ttl-seconds` to the number of seconds a listing may be reused, for example `300`.
2. Restart the server completely.

```hocon
list-cache-ttl-seconds: 300
```

A value of `0` turns the cache off. The cache only affects the list of available maps. Reads and writes of tiles are not cached.

Because the cache keeps the list for the whole time-to-live, a map you add or remove can take up to that long to show up.

See also: [`list-cache-ttl-seconds`](../reference/configuration.md#list-cache-ttl-seconds), [Where R2 costs come from](../explanation/r2-costs.md).
