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

package io.appform.jsonrules.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionEvaluationContext;

/**
 * A basic rule
 */
public class Rule {
    private final Expression expression;

    public Rule(Expression expression) {
        this.expression = expression;
    }

    public static Rule create(final String json, final ObjectMapper mapper) throws Exception {
        return new Rule(mapper.readValue(json, Expression.class));
    }

    public boolean matches(JsonNode node) {
        return expression.evaluate(
                ExpressionEvaluationContext.builder()
                        .node(node)
                        .build());
    }

    public String representation(ObjectMapper mapper) throws Exception {
        return mapper.writeValueAsString(expression);
    }
}
