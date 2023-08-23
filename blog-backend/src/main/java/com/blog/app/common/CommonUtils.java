package com.blog.app.common;

public class CommonUtils {

    private CommonUtils() {}

    /**
     * Merges two values, prioritizing the new value if it's not null.
     *
     * @param oldValue The original value.
     * @param newValue The new value.
     * @return The merged value, preferring newValue if it's not null, otherwise oldValue.
     */
    static public String mergeNullableFields(String oldValue, String newValue) {
        return newValue == null ? oldValue : newValue;
    }

    /**
     * Merges two values, prioritizing the new value if it's not null.
     *
     * @param oldValue The original value.
     * @param newValue The new value.
     * @return The merged value, preferring newValue if it's not null, otherwise oldValue.
     */
    static public int mergeNullableFields(int oldValue, int newValue) {
        return newValue == 0 ? oldValue : newValue;
    }
}
