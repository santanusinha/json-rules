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

package io.appform.jsonrules.expressions.string;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Builder;

/**
 * Check is string is not empty or null
 */
public class NotEmptyExpression extends JsonPathBasedExpression {
    public NotEmptyExpression() {
        super(ExpressionType.not_empty);
    }

    @Builder
    public NotEmptyExpression(String path, boolean defaultResult, PreOperation<?> preoperation) {
        super(ExpressionType.not_empty, path, defaultResult, preoperation);
    }

    public NotEmptyExpression(String path, PreOperation<?> preoperation) {
        this(path, false, preoperation);
    }
    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if(!evaluatedNode.isTextual()) {
            return false;
        }
        final String data = evaluatedNode.asText();
        return null != data && !data.isEmpty();
    }
}
