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

package io.appform.jsonrules.expressions.equality;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.utils.ComparisonUtils;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Compares objects
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotEqualsExpression extends JsonPathBasedExpression {
    private Object value;
    private boolean extractValueFromPath;

    public NotEqualsExpression() {
        super(ExpressionType.not_equals);
        setDefaultResult(true);
    }

    @Builder
    public NotEqualsExpression(String path, Object value, boolean extractValueFromPath, Boolean defaultResult,
            PreOperation<?> preoperation) {
        super(ExpressionType.not_equals, path, ComparisonUtils.getDefaultResult(defaultResult, true), preoperation);
        this.value = value;
        this.extractValueFromPath = extractValueFromPath;
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if (extractValueFromPath) {
            return ComparisonUtils.compareForNotEquals(context, evaluatedNode, value);
        }
        return value == null || ComparisonUtils.isNodeMissingOrNull(evaluatedNode)
                || ComparisonUtils.compare(evaluatedNode, value) != 0;
    }

}
