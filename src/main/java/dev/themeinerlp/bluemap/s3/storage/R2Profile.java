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
 * Cloudflare R2: derives the endpoint from account-id so users don't have to look up/type the
 * full https://&lt;account-id&gt;.r2.cloudflarestorage.com URL themselves. Applies only when
 * account-id is set and no explicit endpoint-url was given, so setting endpoint-url always
 * still wins (e.g. for R2's jurisdictional/EU-restricted endpoints).
 *
 * <p>R2 only supports path-style access, so this always forces it on regardless of
 * force-path-style.
 */
final class R2Profile implements ProviderProfile {

    static final String ID = "r2";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean appliesTo(S3Configuration cfg) {
        boolean noExplicitEndpoint = cfg.getEndpointUrl() == null || cfg.getEndpointUrl().isBlank();
        return noExplicitEndpoint && cfg.getAccountId() != null && !cfg.getAccountId().isBlank();
    }

    @Override
    public ResolvedEndpoint resolve(S3Configuration cfg) {
        String endpoint = "https://" + cfg.getAccountId() + ".r2.cloudflarestorage.com";
        return new ResolvedEndpoint(endpoint, true, cfg.getRegion());
    }
}
