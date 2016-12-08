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
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
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
    private Number value;

    protected NumericJsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected NumericJsonPathBasedExpression(ExpressionType type, String path, Number value) {
        super(type, path);
        this.value = value;
    }

    @Override
    protected final boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if( null == evaluatedNode || !evaluatedNode.isNumber()) {
            return false;
        }
        int comparisonResult = 0;
        if(evaluatedNode.isIntegralNumber()) {
            comparisonResult = Long.compare(evaluatedNode.asLong(), value.longValue());
        }
        else if(evaluatedNode.isFloatingPointNumber()) {
            comparisonResult = Double.compare(evaluatedNode.asDouble(), value.doubleValue());
        }
        return evaluate(context, comparisonResult);
    }

    abstract protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult);
}
