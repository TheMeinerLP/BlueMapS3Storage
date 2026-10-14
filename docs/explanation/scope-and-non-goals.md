# Scope and non-goals

## What the addon is

BlueMapS3Storage is a BlueMap storage type. It stores and loads the data BlueMap produces (map tiles and related metadata) in an S3-compatible bucket instead of on the local disk. This helps distributed server setups, hosts without persistent disk, and networks of servers that share map data.

## What the addon does not do

- It does not host the BlueMap web app. The web app is the HTML and JavaScript that shows the map in a browser. A running BlueMap integration serves it while the Minecraft server is up. If the server is offline, nobody serves the app, so viewing a map then requires hosting the web app separately and pointing it at the data. Storing tiles in S3 alone does not make a map viewable without a server.
- It does not render maps. Rendering is BlueMap's job and still happens on the machine that runs BlueMap.
- It does not manage buckets, users, lifecycle rules or permissions. Create and secure them with your provider's tools.
- It does not apply storage changes at runtime. A storage configuration is read at startup, so changing it needs a full server restart. `/bluemap reload` is not supported for this.
- It does not add a separate storage type per provider. All providers use `themeinerlp:s3`; provider differences are handled by [profiles](how-provider-profiles-work.md).

## Why these limits

Keeping the addon to a storage implementation means it follows BlueMap's own interfaces and upgrades with BlueMap, and it avoids duplicating web hosting, which operators already solve in their own way.
