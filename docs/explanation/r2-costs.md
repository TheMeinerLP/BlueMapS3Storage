# Where R2 costs come from

Cloudflare R2 bills by stored data and by operations. This page explains which operations the addon causes, to help you judge what matters. Current prices are on [Cloudflare's R2 pricing page](https://developers.cloudflare.com/r2/pricing/).

## Operation classes

R2 sorts requests into two classes:

- Class A: operations that change data or list it, such as writing an object and listing a bucket. They cost more per request.
- Class B: operations that read data, such as fetching an object. They cost less per request.

## What the addon does

- Saving a tile is a write, so a render is a stream of Class A operations. A first render of a large world produces the most of them.
- Viewing a map reads tiles through whatever serves the web app, which is outside this addon (see [Scope and non-goals](scope-and-non-goals.md)). Those reads are Class B.
- Enumerating the maps is a bucket listing, so it is a Class A operation. BlueMap does this when the storage starts, not for every tile.

## Why the listing cache exists

A listing is cheap in absolute terms, but it is a Class A operation and its result rarely changes: only when a map is added or removed. Caching it for a few minutes trades a short delay in noticing new maps for fewer billed operations. That is what `list-cache-ttl-seconds` controls. It does not touch the write path, which is where most operations come from.

## What the cache does not do

The cache does not reduce the cost of rendering. If the bill is dominated by writes, the levers are on the BlueMap side, such as how often and how much you render.

See [Reduce R2 request costs](../how-to/reduce-r2-request-costs.md) for the steps.
