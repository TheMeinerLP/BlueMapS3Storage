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

import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * Cloudflare R2. Applies when account-id is set and endpoint-url is blank or still the built-in
 * default (the endpoint is then derived as https://&lt;account-id&gt;.r2.cloudflarestorage.com),
 * or when endpoint-url itself points at an R2 host (e.g. the EU jurisdiction endpoint), in which
 * case that endpoint is used as-is and account-id is not needed.
 *
 * <p>R2 only supports path-style access and signs with region "auto", so both are forced
 * regardless of force-path-style/region (a differing region is reported as a warning).
 */
final class R2Profile implements ProviderProfile {

    static final String ID = "r2";
    static final String HOST_SUFFIX = ".r2.cloudflarestorage.com";
    static final String REGION = "auto";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean appliesTo(S3Configuration cfg) {
        if (isR2Endpoint(cfg.getEndpointUrl())) {
            return true;
        }
        return hasAccountId(cfg) && endpointUnset(cfg.getEndpointUrl());
    }

    @Override
    public ResolvedEndpoint resolve(S3Configuration cfg) {
        String endpoint;
        if (isR2Endpoint(cfg.getEndpointUrl())) {
            endpoint = cfg.getEndpointUrl().trim();
        } else if (hasAccountId(cfg)) {
            endpoint = "https://" + cfg.getAccountId().trim() + HOST_SUFFIX;
        } else {
            throw new IllegalArgumentException("Provider 'r2' needs either 'account-id' or an 'endpoint-url' "
                    + "ending in " + HOST_SUFFIX);
        }
        requireCredential("access-key-id", cfg.getAccessKeyId(), S3StorageConfiguration.DEFAULT_ACCESS_KEY_ID);
        requireCredential(
                "secret-access-key", cfg.getSecretAccessKey(), S3StorageConfiguration.DEFAULT_SECRET_ACCESS_KEY);

        List<String> warnings = List.of();
        String region = cfg.getRegion();
        if (!isAutoAlias(region)) {
            warnings = List.of("Cloudflare R2 only accepts region 'auto' (or the aliases 'us-east-1'/empty); "
                    + "ignoring configured region '" + region + "' and using 'auto'");
        }
        return new ResolvedEndpoint(ID, endpoint, true, REGION, warnings, true);
    }

    /** Replaces the account-id label of an R2 host with {@code ***} so logs don't leak it. */
    static String maskEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            return "";
        }
        String host = hostOf(endpoint);
        if (host == null || !host.toLowerCase(Locale.ROOT).endsWith(HOST_SUFFIX)) {
            return endpoint;
        }
        int dot = host.indexOf('.');
        if (dot <= 0) {
            return endpoint;
        }
        int at = endpoint.indexOf(host);
        return endpoint.substring(0, at) + "***" + endpoint.substring(at + dot);
    }

    static boolean isR2Endpoint(String endpoint) {
        String host = hostOf(endpoint);
        return host != null && host.toLowerCase(Locale.ROOT).endsWith(HOST_SUFFIX);
    }

    private static String hostOf(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            return null;
        }
        try {
            return URI.create(endpoint.trim()).getHost();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean hasAccountId(S3Configuration cfg) {
        return cfg.getAccountId() != null && !cfg.getAccountId().isBlank();
    }

    private static boolean endpointUnset(String endpoint) {
        return endpoint == null
                || endpoint.isBlank()
                || endpoint.trim().equals(S3StorageConfiguration.DEFAULT_ENDPOINT_URL);
    }

    private static boolean isAutoAlias(String region) {
        if (region == null || region.isBlank()) {
            return true;
        }
        String r = region.trim();
        return r.equalsIgnoreCase(S3StorageConfiguration.DEFAULT_REGION)
                || r.equalsIgnoreCase(REGION)
                || r.equalsIgnoreCase("us-east-1");
    }

    private static void requireCredential(String key, String value, String placeholder) {
        if (value == null || value.isBlank() || value.equals(placeholder)) {
            throw new IllegalArgumentException("Cloudflare R2 needs a real '" + key
                    + "' (create an R2 API token); it is missing or still the built-in placeholder");
        }
    }
}
