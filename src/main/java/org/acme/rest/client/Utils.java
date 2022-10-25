package org.acme.rest.client;

import io.vertx.core.json.JsonArray;

import java.util.*;
import java.util.function.Supplier;

public class Utils {
    public static <T> boolean equals(Set<T> s1, Set<T> s2) {
        if (isBlank(s1) && isBlank(s2)) {
            return true;
        }
        if ((isBlank(s1) && notBlank(s2)) || (isBlank(s2) && notBlank(s1))) {
            return false;
        }
        return new HashSet<>(s1).equals(new HashSet<>(s2));
    }

    public static <T> boolean notEquals(Set<T> s1, Set<T> s2) {
        return !equals(s1, s2);
    }

    public static <T> boolean equals(List<T> list1, List<T> list2) {
        return equals(new HashSet<>(list1), new HashSet<>(list2));
    }

    public static <T> boolean notEquals(List<T> list1, List<T> list2) {
        return !equals(list1, list2);
    }

    public static boolean isTrue(Boolean value) {
        return notNull(value) && value;
    }

    public static boolean isNull(Object value) {
        return value == null;
    }

    public static boolean isBlank(String value) {
        return isNull(value) || value.trim().length() == 0;
    }

    public static boolean isBlank(Iterable<?> list) {
        return isNull(list) || !list.iterator().hasNext();
    }

    public static boolean isBlank(Collection<?> collection) {
        return isNull(collection) || collection.size() == 0;
    }

    public static boolean isBlank(Map map) {
        return isNull(map) || map.size() == 0;
    }

    public static boolean isBlank(Object[] list) {
        return isNull(list) || list.length == 0;
    }

    public static boolean isBlank(JsonArray permissions) {
        return isNull(permissions) || permissions.size() == 0;
    }

    public static boolean notNull(Object... values) {
        boolean notNull = true;
        for (Object value : values) if (value == null) notNull = false;
        return notNull;
    }

    public static boolean hasNulls(Object... values) {
        if (values == null) {
            return true;
        }
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean nonNull(Object... values) {
        return !hasNulls(values);
    }

    public static boolean notBlank(String value) {
        return notNull(value) && value.trim().length() > 0;
    }

    public static String ifBlankDefault(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    public static String ifBlankEmpty(String value) {
        return isBlank(value) ? "" : value;
    }

    public static boolean notBlank(Collection<?> collection) {
        return notNull(collection) && collection.size() > 0;
    }

    public static boolean notBlank(Map collection) {
        return notNull(collection) && collection.size() > 0;
    }

    public static boolean notBlank(JsonArray permissions) {
        return notNull(permissions) && permissions.size() > 0;
    }

    public static boolean notBlank(Object[] array) {
        return notNull(array) && array.length > 0;
    }

    // region mongo utils

    public static <T> T value(Supplier<T> supplier, T defaultVal) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultVal;
        }
    }


    // endregion
}
