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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Selects a {@link ProviderProfile} and resolves it against the given config.
 *
 * <p>If {@code provider} is set explicitly, that profile is used directly, an unmistakable,
 * explicit strategy choice, and an unknown name fails fast rather than silently falling back.
 * If left unset, profiles are tried in order and the first one whose {@link
 * ProviderProfile#appliesTo(S3Configuration)} matches wins, so existing zero-config setups
 * (e.g. just setting account-id) keep working without needing to also learn about
 * {@code provider}. {@link GenericS3Profile} always applies and is tried last.
 */
final class ProviderProfiles {

    private static final List<ProviderProfile> PROFILES = List.of(new R2Profile(), new GenericS3Profile());

    private static final Map<String, ProviderProfile> BY_ID = new LinkedHashMap<>();

    static {
        for (ProviderProfile profile : PROFILES) {
            BY_ID.put(profile.id(), profile);
        }
    }

    private ProviderProfiles() {}

    static ProviderProfile.ResolvedEndpoint resolve(S3Configuration cfg) {
        return select(cfg).resolve(cfg);
    }

    private static ProviderProfile select(S3Configuration cfg) {
        String explicit = cfg.getProvider();
        if (explicit != null && !explicit.isBlank()) {
            ProviderProfile profile = BY_ID.get(explicit.trim().toLowerCase());
            if (profile == null) {
                throw new IllegalArgumentException(
                        "Unknown provider '"
                                + explicit
                                + "', expected one of: "
                                + String.join(", ", BY_ID.keySet()));
            }
            return profile;
        }

        return PROFILES.stream()
                .filter(profile -> profile.appliesTo(cfg))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No provider profile matched (GenericS3Profile should always match)"));
    }
}
