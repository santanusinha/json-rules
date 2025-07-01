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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.jayway.jsonpath.PathNotFoundException;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.utils.JsonPathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

import java.util.Objects;

import static io.appform.jsonrules.utils.ComparisonUtils.mapper;

/**
 * All expressions that evaluate a json path uses this.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class JsonPathBasedExpression extends Expression {
    private String path;
    private PreOperation<?> preoperation;
    private boolean defaultResult;

    protected JsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected JsonPathBasedExpression(ExpressionType type, String path, boolean defaultResult,
            PreOperation<?> preoperation) {
        this(type);
        this.path = path;
        this.preoperation = preoperation;
        this.defaultResult = defaultResult;
    }

    @Override
    public final boolean evaluate(ExpressionEvaluationContext context) {
        JsonNode nodeAtPath = null;
        try {
            Object value = JsonPathUtils.read(context.getNode(), path);
            if (value instanceof JsonNode) {
                nodeAtPath = (JsonNode) value;
            } else {
                // convert value to a json node
                // this might happen because of the usage of UDFs supported by json path library used
                // See more here: https://github.com/json-path/JsonPath?tab=readme-ov-file#functions
                nodeAtPath = mapper.valueToTree(value);
            }
            // If nodeAtPath is null, then we use MissingNode instead
            nodeAtPath = nodeAtPath == null ? MissingNode.getInstance() : nodeAtPath;
        } catch (PathNotFoundException exception) {
            // Using default result when the 'path' doesn't exist
            return defaultResult;
        }

        JsonNode evaluatedNode = applyPreoperation(context, nodeAtPath);
        return evaluate(context, path, evaluatedNode);
    }

    private JsonNode applyPreoperation(ExpressionEvaluationContext globalContext, JsonNode nodeAtPath) {
        if (null == preoperation) {
            return nodeAtPath;
        }

        val newContext = ExpressionEvaluationContext.builder()
                .node(nodeAtPath)
                .options(globalContext.getOptions())
                .build();

        val computedValue = preoperation.compute(newContext);
        return mapper.valueToTree(computedValue);
    }

    protected abstract boolean evaluate(ExpressionEvaluationContext context, final String path, JsonNode evaluatedNode);
}
