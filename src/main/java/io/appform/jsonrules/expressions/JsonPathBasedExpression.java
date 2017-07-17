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

package io.appform.jsonrules.expressions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

/**
 * All expressions that evaluate a json path uses this.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class JsonPathBasedExpression extends Expression {
    private String path;
    private PreOperation<?> preoperation;
    private static final ObjectMapper mapper = new ObjectMapper();
    private boolean defaultResult;

    protected JsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected JsonPathBasedExpression(ExpressionType type, String path, boolean defaultResult, PreOperation<?> preoperation) {
        this(type);
        this.path = path;
        this.preoperation = preoperation;
        this.defaultResult = defaultResult;
    }

    @Override
    public final boolean evaluate(ExpressionEvaluationContext context) {
        JsonNode evaluatedNode = context.getNode().at(path);

        if (preoperation != null) {
        	val computedValue = preoperation.compute(evaluatedNode);
        	evaluatedNode = mapper.valueToTree(computedValue);
        }

        if (evaluatedNode.isMissingNode()) {
            return defaultResult;
        }

        return evaluate(context, path, evaluatedNode);
    }

    abstract protected boolean evaluate(ExpressionEvaluationContext context, final String path, JsonNode evaluatedNode);
}
