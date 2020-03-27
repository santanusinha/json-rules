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
import io.appform.jsonrules.ExpressionVisitor;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Builder;

/**
 * Created by santanu on 15/9/16.
 */
public class GreaterThanEqualsExpression extends NumericJsonPathBasedExpression {
    public GreaterThanEqualsExpression() {
        super(ExpressionType.greater_than_equals);
    }

    @Builder
    public GreaterThanEqualsExpression(String path, Object value, boolean extractValueFromPath, boolean defaultResult,
            PreOperation<?> preoperation) {
        super(ExpressionType.greater_than_equals, path, value, extractValueFromPath, defaultResult, preoperation);
    }

    public GreaterThanEqualsExpression(String path, Object value, boolean extractValueFromPath,
            PreOperation<?> preoperation) {
        this(path, value, extractValueFromPath, false, preoperation);
    
    }

    protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult) {
        return comparisonResult >= 0;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor, JsonNode jsonNode) {
        return visitor.visit(this, jsonNode);
    }
}
