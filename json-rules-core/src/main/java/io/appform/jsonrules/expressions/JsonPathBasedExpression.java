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
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.cache.Cache;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.config.JacksonConfiguration;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    static {
        Configuration.setDefaults(JacksonConfiguration.getInstance());
        CacheProvider.setCache(new Cache() {
            private final Map<String, JsonPath> map = new ConcurrentHashMap<>();

            @Override
            public JsonPath get(final String s) {
                return map.get(s);
            }

            @Override
            public void put(final String s,
                            final JsonPath jsonPath) {
                map.computeIfAbsent(s, k -> jsonPath);
            }
        });
    }

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
            JsonNode nodeValue = JsonPath.read(context.getNode(), path);
            if (nodeValue != null) {
                nodeAtPath = nodeValue;
            } else {
                // Node exists; but value is null. Proceed as missing node.
                nodeAtPath = MissingNode.getInstance();
            }
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
