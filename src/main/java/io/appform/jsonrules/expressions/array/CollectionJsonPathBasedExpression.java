/**
 * Copyright (c) 2017 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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

package io.appform.jsonrules.expressions.array;

import static io.appform.jsonrules.utils.ComparisonUtils.mapper;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

/**
 * All collection operable expressions
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class CollectionJsonPathBasedExpression extends JsonPathBasedExpression {
    private Set<Object> values;
    private boolean extractValues;
    private String valuesPath;

    protected CollectionJsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected CollectionJsonPathBasedExpression(ExpressionType type, String path, @Singular Set<Object> values,
            boolean extractValues, String valuesPath, boolean defaultResult, PreOperation<?> preoperation) {
        super(type, path, defaultResult, preoperation);
        this.values = values;
        this.extractValues = extractValues;
        this.valuesPath = valuesPath;
    }

    @Override
    protected final boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if (extractValues) {
            JsonNode jsonNode = MissingNode.getInstance();
            try {
                jsonNode = mapper
                        .valueToTree(JsonPath.read(context.getNode().toString(), String.valueOf(valuesPath)));
            } catch (PathNotFoundException e) {
                // consume silently; indicates path denoted by @valuesPath doesn't exist.
            }
            if (jsonNode == null || !jsonNode.isArray()) {
                return false;
            }
            // fetch values from @values path as a set.
            final HashSet<Object> extractedPathVaues = new HashSet<Object>(JsonPath.read(jsonNode.toString(), "$"));
            return evaluate(evaluatedNode, extractedPathVaues);
        }

        if (null == values || values.isEmpty()) {
            return false;
        }
        return evaluate(evaluatedNode, values);
    }

    abstract protected boolean evaluate(JsonNode evaluatedNode, Set<Object> values);

}
