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

package io.appform.jsonrules.expressions.numeric;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import static io.appform.jsonrules.utils.ComparisonUtils.mapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All numeric binary expressions
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class NumericJsonPathBasedExpression extends JsonPathBasedExpression {
    private Object value;
    private boolean extractValueFromPath;

    protected NumericJsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected NumericJsonPathBasedExpression(ExpressionType type, String path, Object value,
            boolean extractValueFromPath, boolean defaultResult, PreOperation<?> preoperation) {
        super(type, path, defaultResult, preoperation);
        this.value = value;
        this.extractValueFromPath = extractValueFromPath;
    }

    @Override
    protected final boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if (null == evaluatedNode || !evaluatedNode.isNumber()) {
            return false;
        }

        Number numericalValue;
        if (extractValueFromPath) {
            JsonNode jsonNode = MissingNode.getInstance();
            try {
                jsonNode = mapper
                        .valueToTree(JsonPath.read(context.getNode().toString(), String.valueOf(value)));
                if (jsonNode == null) {
                    return false;
                }
            } catch (PathNotFoundException e) {
                // consume silently; indicates path denoted by @value doesn't exist.
            }
            if (jsonNode.isIntegralNumber()) {
                numericalValue = jsonNode.asLong();
            } else if (jsonNode.isFloatingPointNumber()) {
                numericalValue = jsonNode.asDouble();
            } else {
                // If node @value path is missing or not a number, exception
                // would be thrown.
                throw new IllegalArgumentException("Operand is not a number");
            }
        } else {
            numericalValue = (Number) value;
        }

        int comparisonResult = 0;
        if (evaluatedNode.isIntegralNumber()) {
            comparisonResult = Long.compare(evaluatedNode.asLong(), numericalValue.longValue());
        } else if (evaluatedNode.isFloatingPointNumber()) {
            comparisonResult = Double.compare(evaluatedNode.asDouble(), numericalValue.doubleValue());
        }
        return evaluate(context, comparisonResult);
    }

    abstract protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult);
}
