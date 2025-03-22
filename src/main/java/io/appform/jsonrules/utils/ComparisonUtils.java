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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.filter.ValueNodes;
import io.appform.jsonrules.ExpressionEvaluationContext;

/**
 * Created by santanu on 15/9/16.
 */
public class ComparisonUtils {
    public static final Configuration SUPPRESS_EXCEPTION_CONFIG = Configuration.defaultConfiguration()
            .addOptions(Option.SUPPRESS_EXCEPTIONS);
    public static final ObjectMapper mapper = new ObjectMapper();

    public static int compare(JsonNode evaluatedNode, Object value) {
        int comparisonResult = 0;
        if (evaluatedNode.isNumber()) {
            if (value instanceof NumericNode) {
                return compare(evaluatedNode, ((NumericNode) value).numberValue());
            }
            return compare(evaluatedNode, (Number) value);
        } else if (evaluatedNode.isBoolean()) {
            return compare(evaluatedNode, Boolean.parseBoolean(value.toString()));
        } else if (evaluatedNode.isTextual()) {
            if (value instanceof TextNode) {
                return compare(evaluatedNode, ((TextNode) value).asText());
            }
            return compare(evaluatedNode, String.valueOf(value));
        } else if (evaluatedNode.isObject()) {
            throw new IllegalArgumentException("Object comparisons not supported");
        }
        return comparisonResult;
    }

    public static int compare(JsonNode evaluatedNode, Number value) {
        int comparisonResult = 0;
        if (evaluatedNode.isNumber()) {
            if (evaluatedNode.isIntegralNumber()) {
                comparisonResult = Long.compare(evaluatedNode.asLong(), value.longValue());
            } else if (evaluatedNode.isFloatingPointNumber()) {
                comparisonResult = Double.compare(evaluatedNode.asDouble(), value.doubleValue());
            }
        }
        return comparisonResult;
    }

    public static int compare(JsonNode evaluatedNode, Boolean value) {
        int comparisonResult = 0;
        if (evaluatedNode.isBoolean()) {
            final boolean bValue = Boolean.parseBoolean(value.toString());
            comparisonResult = Boolean.compare(evaluatedNode.asBoolean(), bValue);
        }
        return comparisonResult;
    }

    public static int compare(JsonNode evaluatedNode, String value) {
        int comparisonResult = -1;
        if (evaluatedNode.isTextual()) {
            comparisonResult = evaluatedNode.asText().compareTo(value);
        }
        return comparisonResult;
    }

    public static boolean compareForEquality(ExpressionEvaluationContext context, JsonNode evaluatedNode,
            Object value) {
        final boolean nodeMissingOrNullCheck = isNodeMissingOrNull(evaluatedNode);
        final JsonNode jsonNode = mapper.valueToTree(JsonPath.using(SUPPRESS_EXCEPTION_CONFIG)
                .parse(context.getNode().toString()).read(String.valueOf(value)));

        if (isNodeMissingOrNull(jsonNode)) {
            return nodeMissingOrNullCheck;
        } else if (jsonNode.isNumber()) {
            if (jsonNode.isIntegralNumber()) {
                return !nodeMissingOrNullCheck && compare(evaluatedNode, jsonNode.asLong()) == 0;
            } else {
                return !nodeMissingOrNullCheck && compare(evaluatedNode, jsonNode.asDouble()) == 0;
            }
        } else if (jsonNode.isBoolean()) {
            return !nodeMissingOrNullCheck
                    && ComparisonUtils.compare(evaluatedNode, jsonNode.asBoolean()) == 0;
        } else if (jsonNode.isTextual()) {
            return !nodeMissingOrNullCheck && compare(evaluatedNode, jsonNode.asText()) == 0;
        } else {
            return !nodeMissingOrNullCheck && compare(evaluatedNode, jsonNode) == 0;
        }
    }

    public static boolean compareForNotEquals(ExpressionEvaluationContext context, JsonNode evaluatedNode,
            Object value) {
        final boolean nodeMissingOrNullCheck = isNodeMissingOrNull(evaluatedNode);
        final JsonNode jsonNode = mapper.valueToTree(JsonPath.using(SUPPRESS_EXCEPTION_CONFIG)
                .parse(context.getNode().toString()).read(String.valueOf(value)));

        if (isNodeMissingOrNull(jsonNode)) {
            return !nodeMissingOrNullCheck;
        } else if (jsonNode.isNumber()) {
            if (jsonNode.isIntegralNumber()) {
                return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode.asLong()) != 0;
            } else {
                return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode.asDouble()) != 0;
            }
        } else if (jsonNode.isBoolean()) {
            return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode.asBoolean()) != 0;
        } else if (jsonNode.isTextual()) {
            return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode.asText()) != 0;
        } else {
            return nodeMissingOrNullCheck || compare(evaluatedNode, jsonNode) != 0;
        }
    }

    public static boolean isNodeMissingOrNull(JsonNode node) {
        return null == node || node.isMissingNode() || node.isNull();
    }

    public static boolean getDefaultResult(Boolean defaultResult, boolean resultIfNull) {
        if (null == defaultResult)
            return resultIfNull;
        return defaultResult;
    }

    private ComparisonUtils() {}
}
