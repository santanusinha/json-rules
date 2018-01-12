/**
 * Copyright (c) 2018 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Builder;

public class BetweenExpression extends JsonPathBasedExpression {
    private Number lowerBound;
    private Number upperBound;

    public BetweenExpression() {
        super(ExpressionType.between);
    }

    @Builder
    public BetweenExpression(String path, Number lowerbound, Number upperBound, boolean defaultResult,
            PreOperation<?> preoperation) {
        super(ExpressionType.between, path, defaultResult, preoperation);
        this.lowerBound = lowerbound;
        this.upperBound = upperBound;
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if (null == evaluatedNode || !evaluatedNode.isNumber()) {
            return false;
        }
        if (evaluatedNode.isIntegralNumber()) {
            return evaluatedNode.asInt() >= lowerBound.intValue() && evaluatedNode.asInt() < upperBound.intValue();
        } else if (evaluatedNode.isFloatingPointNumber()) {
            return evaluatedNode.doubleValue() >= lowerBound.doubleValue()
                    && evaluatedNode.asInt() < upperBound.doubleValue();
        }
        return false;
    }

}
