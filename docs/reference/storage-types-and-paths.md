# Storage type and file locations

## Storage type key

| Item | Value |
|------|-------|
| Storage type key | `themeinerlp:s3` |
| Set with | `storage-type: "themeinerlp:s3"` in a storage configuration file |
| Other storage types | None. There is no separate `themeinerlp:r2` type; R2 uses `themeinerlp:s3`. |

## Where files live

The addon jar goes into the `packs` directory of BlueMap. Storage configuration files go into the `storages` directory that sits next to `packs`. Map configuration files are in the `maps` directory.

| Platform | Jar (`packs`) | Storage configurations (`storages`) |
|----------|---------------|--------------------------------------|
| Spigot, Paper | `./plugins/BlueMap/packs/` | `./plugins/BlueMap/storages/` |
| Sponge, Forge, Fabric | `./config/bluemap/packs/` | `./config/bluemap/storages/` |
| CLI | `./config/packs/` | `./config/storages/` |

## Linking a map to a storage

The file name of a storage configuration without `.conf` is the storage id. A map selects it with `storage: "<id>"` in its map configuration. Example: `storages/r2.conf` is used by a map with `storage: "r2"`.

## Applying changes

A storage configuration is read when BlueMap starts. Applying a change by a full server restart is the supported way; this addon does not test `/bluemap reload` for storage changes.

## Keys

All keys of the storage configuration are listed in the [Configuration reference](configuration.md).
