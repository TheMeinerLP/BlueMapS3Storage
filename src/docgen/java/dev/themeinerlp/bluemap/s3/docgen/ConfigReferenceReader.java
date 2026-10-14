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
package dev.themeinerlp.bluemap.s3.docgen;

import de.bluecolored.bluemap.common.debug.DebugDump;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.util.NamingSchemes;

/**
 * Reads configuration keys the way Configurate's object mapper sees them: every non-static,
 * non-transient field of the class and its superclasses, named by {@code @Setting} or, without
 * it, by the lower-case dashed naming scheme. Defaults come from a fresh instance.
 */
public final class ConfigReferenceReader {

    private ConfigReferenceReader() {}

    public static List<ConfigOption> read(Class<?> type) {
        Object instance = newInstance(type);
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            hierarchy.add(c);
        }
        Collections.reverse(hierarchy);

        List<ConfigOption> options = new ArrayList<>();
        for (Class<?> c : hierarchy) {
            for (Field field : c.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
                    continue;
                }
                options.add(toOption(field, instance));
            }
        }
        return options;
    }

    private static ConfigOption toOption(Field field, Object instance) {
        Setting setting = field.getAnnotation(Setting.class);
        String key = setting != null && !setting.value().isEmpty()
                ? setting.value()
                : NamingSchemes.LOWER_CASE_DASHED.coerce(field.getName());
        Comment comment = field.getAnnotation(Comment.class);
        DebugDump debugDump = field.getAnnotation(DebugDump.class);
        try {
            field.setAccessible(true);
            return new ConfigOption(
                    key,
                    field.getType().getSimpleName(),
                    field.get(instance),
                    comment == null ? "" : comment.value(),
                    debugDump != null && debugDump.exclude());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read field " + field, e);
        }
    }

    private static Object newInstance(Class<?> type) {
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot instantiate " + type, e);
        }
    }
}
