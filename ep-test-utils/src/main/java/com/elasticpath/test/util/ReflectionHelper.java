package com.elasticpath.test.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for java.lang.reflect.
 * @author dlewis
 */
@SuppressWarnings("PMD.UseSingleton")
public class ReflectionHelper {

    private static final Map<Class<?>, List<Field>> LIST_OF_FIELDS_BY_CLASS_MAP = new ConcurrentHashMap<Class<?>, List<Field>>();

    /**
     * Get a list of all declared fields on a class, and it's superclass, and it's superclass, and so on. 
     * @param klass Class to get fields from;
     * @return a list of fields
     */
    public static List<Field> getFields(final Class<?> klass) {
        List<Field> fields = LIST_OF_FIELDS_BY_CLASS_MAP.get(klass);
        if (fields == null) {
            fields = new LinkedList<Field>();
            for (Field field : klass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            Class<?> superClass = klass.getSuperclass();
            if (superClass != null) {
                fields.addAll(getFields(superClass));
            }
            LIST_OF_FIELDS_BY_CLASS_MAP.put(klass, fields);
        }
        return fields;
    }

}
