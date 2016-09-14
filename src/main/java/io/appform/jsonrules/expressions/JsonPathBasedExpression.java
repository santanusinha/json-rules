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
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All expressions that evaluate a json path uses this.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class JsonPathBasedExpression extends Expression {
    private String path;

    protected JsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected JsonPathBasedExpression(ExpressionType type, String path) {
        this(type);
        this.path = path;
    }

    @Override
    public final boolean evaluate(ExpressionEvaluationContext context) {
        //T value = context.getParsedContext().read(path, clazz);
        final JsonNode evaluatedNode = context.getNode().at(path);
        return evaluate(context, path, evaluatedNode);
    }

    abstract protected boolean evaluate(ExpressionEvaluationContext context, final String path, JsonNode evaluatedNode);
}
