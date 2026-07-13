/**
 * A simple storage implementation for bluemap to save data into s3 storage solution.
 * Copyright (C) 2025 TheMeinerLP and contributors
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.themeinerlp.bluemap.s3.storage;

/**
 * Resolves a provider's own shorthand config (e.g. Cloudflare R2's account-id) into the plain
 * connection settings {@link S3FileSystemFactory} actually needs. Keeps provider-specific
 * conventions out of the generic S3 engine: add a new profile here when a provider gets its
 * own convenience config, register it in {@link ProviderProfiles}, and the factory itself
 * never needs to change.
 */
interface ProviderProfile {

    /** Stable name users can set explicitly via the provider config option, e.g. "r2". */
    String id();

    /**
     * Whether this profile's own shorthand config is present, so it should be picked
     * automatically when the user didn't set provider explicitly. Only consulted as a
     * fallback - see {@link ProviderProfiles#resolve(S3Configuration)}.
     */
    boolean appliesTo(S3Configuration cfg);

    /** Only called once this profile has been selected, either explicitly or via {@link #appliesTo}. */
    ResolvedEndpoint resolve(S3Configuration cfg);

    record ResolvedEndpoint(String endpointUrl, boolean forcePathStyle, String region) {}
}
