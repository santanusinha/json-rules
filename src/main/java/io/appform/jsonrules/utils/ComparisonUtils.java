/*
 * Copyright (c) 2016 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.appform.jsonrules.utils;

import com.fasterxml.jackson.databind.JsonNode;

import io.appform.jsonrules.ExpressionEvaluationContext;

/**
 * Created by santanu on 15/9/16.
 */
public interface ComparisonUtils {
    static int compare(JsonNode evaluatedNode, Object value) {
        int comparisonResult = 0;
        if (evaluatedNode.isNumber()) {
            return compare(evaluatedNode, (Number) value);
        } else if (evaluatedNode.isBoolean()) {
            return compare(evaluatedNode, Boolean.parseBoolean(value.toString()));
        } else if (evaluatedNode.isTextual()) {
            return compare(evaluatedNode, String.valueOf(value));
        } else if (evaluatedNode.isObject()) {
            throw new IllegalArgumentException("Object comparisons not supported");
        }
        return comparisonResult;
    }

    static int compare(JsonNode evaluatedNode, Number value) {
        int comparisonResult = 0;
        if (evaluatedNode.isNumber()) {
            if (Number.class.isAssignableFrom(value.getClass())) {
                if (evaluatedNode.isIntegralNumber()) {
                    comparisonResult = Long.compare(evaluatedNode.asLong(), value.longValue());
                } else if (evaluatedNode.isFloatingPointNumber()) {
                    comparisonResult = Double.compare(evaluatedNode.asDouble(), value.doubleValue());
                }
            } else {
                throw new IllegalArgumentException("Type mismatch between operator and operand");
            }
        }
        return comparisonResult;
    }

    static int compare(JsonNode evaluatedNode, Boolean value) {
        int comparisonResult = 0;
        if (evaluatedNode.isBoolean()) {
            if (Boolean.class.isAssignableFrom(value.getClass())) {
                Boolean bValue = Boolean.parseBoolean(value.toString());
                comparisonResult = Boolean.compare(evaluatedNode.asBoolean(), bValue);
            } else {
                throw new IllegalArgumentException("Type mismatch between operator and operand");
            }
        }
        return comparisonResult;
    }

    static int compare(JsonNode evaluatedNode, String value) {
        int comparisonResult = 0;
        if (evaluatedNode.isTextual()) {
            if (String.class.isAssignableFrom(value.getClass())) {
                comparisonResult = evaluatedNode.asText().compareTo(String.valueOf(value));
            } else {
                throw new IllegalArgumentException("Type mismatch between operator and operand");
            }
        }
        return comparisonResult;
    }

    public static boolean compareForEquality(ExpressionEvaluationContext context, JsonNode evaluatedNode,
            Object value) {
        final boolean nodeMissingOrNullCheck = isNodeMissingOrNull(evaluatedNode);
        JsonNode jsonNode = context.getNode().at(String.valueOf(value));
        if (jsonNode.isNumber()) {
            if (jsonNode.isIntegralNumber()) {
                return !nodeMissingOrNullCheck && compare(evaluatedNode, jsonNode.asLong()) == 0;
            } else {
                return !nodeMissingOrNullCheck && compare(evaluatedNode, jsonNode.asDouble()) == 0;
            }
        } else if (jsonNode.isBoolean()) {
            return !nodeMissingOrNullCheck
                    && ComparisonUtils.compare(evaluatedNode, Boolean.parseBoolean(value.toString())) == 0;
        } else if (jsonNode.isTextual()) {
            return !nodeMissingOrNullCheck && compare(evaluatedNode, String.valueOf(value)) == 0;
        } else {
            return !nodeMissingOrNullCheck && compare(evaluatedNode, value) == 0;
        }
    }

    public static boolean compareForNotEquals(ExpressionEvaluationContext context, JsonNode evaluatedNode,
            Object value) {
        final boolean nodeMissingOrNullCheck = isNodeMissingOrNull(evaluatedNode);
        JsonNode jsonNode = context.getNode().at(String.valueOf(value));
        if (jsonNode.isNumber()) {
            if (jsonNode.isIntegralNumber()) {
                return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode.asLong()) != 0;
            } else {
                return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode.asDouble()) != 0;
            }
        } else if (jsonNode.isBoolean()) {
            return nodeMissingOrNullCheck || compare(evaluatedNode, Boolean.parseBoolean(value.toString())) != 0;
        } else if (jsonNode.isTextual()) {
            return nodeMissingOrNullCheck || compare(evaluatedNode, String.valueOf(value)) != 0;
        } else {
            return value == null || isNodeMissingOrNull(evaluatedNode) || compare(evaluatedNode, value) != 0;
        }
    }

    public static boolean isNodeMissingOrNull(JsonNode node) {
        return node.isMissingNode() || node.isNull();
    }

    public static boolean getDefaultResult(Boolean defaultResult, boolean resultIfNull) {
        if (null == defaultResult)
            return resultIfNull;
        return defaultResult.booleanValue();
    }
}
