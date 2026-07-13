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
 * Fallback profile: passes the configured endpoint-url/force-path-style/region through
 * unchanged. Always applies, and {@link ProviderProfiles} tries it last so any provider-specific
 * profile gets a chance to take over first.
 */
final class GenericS3Profile implements ProviderProfile {

    static final String ID = "generic";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean appliesTo(S3Configuration cfg) {
        return true;
    }

    @Override
    public ResolvedEndpoint resolve(S3Configuration cfg) {
        return new ResolvedEndpoint(cfg.getEndpointUrl(), cfg.forcePathStyle(), cfg.getRegion());
    }
}
