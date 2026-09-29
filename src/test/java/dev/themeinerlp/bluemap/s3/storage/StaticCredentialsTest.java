/**
 * A simple storage implementation for bluemap to save data into s3 storage solution.
 * Copyright (C) 2026 TheMeinerLP and contributors
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class StaticCredentialsTest {

    @AfterEach
    void clearProperties() {
        System.clearProperty(StaticCredentials.ACCESS_KEY_ID_PROPERTY);
        System.clearProperty(StaticCredentials.SECRET_ACCESS_KEY_PROPERTY);
    }

    @Test
    void blankKeysMeanNoStaticCredentials() {
        assertFalse(StaticCredentials.isConfigured(null, null));
        assertFalse(StaticCredentials.isConfigured("", ""));
        assertFalse(StaticCredentials.isConfigured("  ", "\t"));
    }

    @Test
    void bothKeysMeanStaticCredentials() {
        assertTrue(StaticCredentials.isConfigured("AKIAEXAMPLE", "secret"));
    }

    @Test
    void onlyOneKeyIsRejected() {
        IllegalArgumentException onlyId =
                assertThrows(IllegalArgumentException.class, () -> StaticCredentials.isConfigured("AKIAEXAMPLE", ""));
        assertTrue(onlyId.getMessage().contains("access-key-id"));
        assertTrue(onlyId.getMessage().contains("secret-access-key"));
        assertThrows(IllegalArgumentException.class, () -> StaticCredentials.isConfigured(null, "secret"));
    }

    @Test
    void applySetsSystemPropertiesWhenKeysAreConfigured() {
        StaticCredentials.applyAwsSystemProperties("AKIAEXAMPLE", "secret");

        assertEquals("AKIAEXAMPLE", System.getProperty(StaticCredentials.ACCESS_KEY_ID_PROPERTY));
        assertEquals("secret", System.getProperty(StaticCredentials.SECRET_ACCESS_KEY_PROPERTY));
    }

    @Test
    void applyLeavesSystemPropertiesUnsetWhenKeysAreBlank() {
        StaticCredentials.applyAwsSystemProperties("", "");

        assertNull(System.getProperty(StaticCredentials.ACCESS_KEY_ID_PROPERTY));
        assertNull(System.getProperty(StaticCredentials.SECRET_ACCESS_KEY_PROPERTY));
    }

    @Test
    void applyClearsStalePropertiesWhenKeysAreRemoved() {
        StaticCredentials.applyAwsSystemProperties("AKIAEXAMPLE", "secret");

        StaticCredentials.applyAwsSystemProperties(null, null);

        assertNull(System.getProperty(StaticCredentials.ACCESS_KEY_ID_PROPERTY));
        assertNull(System.getProperty(StaticCredentials.SECRET_ACCESS_KEY_PROPERTY));
    }

    @Test
    void userInfoIsOnlyBuiltFromConfiguredKeys() {
        assertEquals("AKIAEXAMPLE:secret", StaticCredentials.userInfo("AKIAEXAMPLE", "secret"));
        assertNull(StaticCredentials.userInfo("", ""));
    }
}
