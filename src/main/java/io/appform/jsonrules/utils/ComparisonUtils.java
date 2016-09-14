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
