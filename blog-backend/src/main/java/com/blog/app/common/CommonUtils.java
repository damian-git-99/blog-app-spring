package com.blog.app.common;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * It handles validation exceptions generated by validation errors in the data binding process
     * between input data and Java objects.
     *
     * @param br The BindingResult object that contains the validation results.
     * @return ResponseEntity with a body containing a map of validation errors if any.
     */
    static public ResponseEntity<?> handleValidationExceptions(BindingResult br) {
        Map<String, Object> errorsMap = new HashMap<>();
        br.getFieldErrors()
                .forEach(objectError -> errorsMap.put(objectError.getField(), objectError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errorsMap);
    }
}
