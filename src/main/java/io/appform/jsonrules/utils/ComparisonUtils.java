package io.appform.jsonrules.utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by santanu on 15/9/16.
 */
public interface ComparisonUtils {
    static int compare(JsonNode evaluatedNode, Object value) {
        int comparisonResult = 0;
        if(evaluatedNode.isNumber()) {
            if(Number.class.isAssignableFrom(value.getClass())) {
                Number nValue = (Number)value;
                if(evaluatedNode.isIntegralNumber()) {
                    comparisonResult = Long.compare(evaluatedNode.asLong(), nValue.longValue());
                }
                else if(evaluatedNode.isFloatingPointNumber()) {
                    comparisonResult = Double.compare(evaluatedNode.asDouble(), nValue.doubleValue());
                }
            }
            else {
                throw new IllegalArgumentException("Type mismatch between operator and operand");
            }
        }
        else if(evaluatedNode.isTextual()) {
            if (String.class.isAssignableFrom(value.getClass())) {
                comparisonResult = evaluatedNode.asText().compareTo(String.valueOf(value));
            }
            else {
                throw new IllegalArgumentException("Type mismatch between operator and operand");
            }
        }
        else if(evaluatedNode.isObject()) {
            throw new IllegalArgumentException("Object comparisons not supported");
        }
        return comparisonResult;
    }
}
