# Changelog

## [1.6.1](https://github.com/TheMeinerLP/BlueMapS3Storage/compare/v1.6.0...v1.6.1) (2026-10-14)


### Bug Fixes

* **storage:** use the standard s3 client for cloudflare r2 ([#96](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/96)) ([1158e00](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/1158e00da170d769fa33b91916c1758abfcad7a2))

## [1.6.0](https://github.com/TheMeinerLP/BlueMapS3Storage/compare/v1.5.1...v1.6.0) (2026-10-14)


### Features

* **aws:** support the default AWS credential chain ([#93](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/93)) ([c04bbc7](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/c04bbc7161d56e4546ea35add1776a8ee1ba05bd))


### Bug Fixes

* **deps:** update dependency org.junit:junit-bom to v6.1.3 ([#87](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/87)) ([53f7cf4](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/53f7cf457508e7fa2d19b001a28af2fb82f8a298))
* **storage:** make cloudflare r2 work with account-id only ([#94](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/94)) ([a967f1e](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/a967f1e4ee4e8528c6f2026daf7db6097162d68b))

## [1.5.1](https://github.com/TheMeinerLP/BlueMapS3Storage/compare/v1.5.0...v1.5.1) (2026-07-19)


### Bug Fixes

* don't set global credential system properties for third-party S3 targets ([#83](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/83)) ([a83df04](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/a83df046022eb5945d25b8f90f8f8a2f32ae2fae))

## [1.5.0](https://github.com/TheMeinerLP/BlueMapS3Storage/compare/v1.4.8...v1.5.0) (2026-07-13)


### Features

* add Cloudflare R2 support via account-id and optional list caching ([#77](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/77)) ([b946aeb](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/b946aeb9a0ede4c0ebb634ffcb4503e8dd94d052))

## [1.4.8](https://github.com/TheMeinerLP/BlueMapS3Storage/compare/v1.4.7...v1.4.8) (2026-07-13)


### Bug Fixes

* **deps:** update dependency org.junit:junit-bom to v6.1.2 ([#68](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/68)) ([41b1ee0](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/41b1ee05ef8fe5ebd8e7074aa7d00be0b77920c3))
* make checksum validation mode configurable, default when_required ([#73](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/73)) ([1ec52c5](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/1ec52c5a0b8759ba305e8f008f9ad5728f22571b))
* use inline x-release-please-version annotation ([#76](https://github.com/TheMeinerLP/BlueMapS3Storage/issues/76)) ([b98258e](https://github.com/TheMeinerLP/BlueMapS3Storage/commit/b98258e52cc443e1c47777525e581f7bdee3e661))
