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
public class EqualsExpression extends JsonPathBasedExpression {
    private Object value;
    private boolean extractValueFromPath;

    public EqualsExpression() {
        super(ExpressionType.equals);
    }

    @Builder
    public EqualsExpression(String path, Object value, boolean extractValueFromPath, boolean defaultResult,
            PreOperation<?> preoperation) {
        super(ExpressionType.equals, path, defaultResult, preoperation);
        this.value = value;
        this.extractValueFromPath = extractValueFromPath;
    }

    public EqualsExpression(String path, Object value, boolean extractValueFromPath, PreOperation<?> preoperation) {
        this(path, value, extractValueFromPath, false, preoperation);
    }

    public EqualsExpression(String path, Object value, PreOperation<?> preoperation) {
        this(path, value,false, preoperation);
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if (extractValueFromPath) {
            return ComparisonUtils.compareForEquality(context, evaluatedNode, value);
        }
        return !ComparisonUtils.isNodeMissingOrNull(evaluatedNode)
                && ComparisonUtils.compare(evaluatedNode, value) == 0;
    }

}
