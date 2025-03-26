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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.ExpressionVisitor;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.utils.JsonUtils;
import lombok.*;

import java.util.Set;

/**
 * Compares collections for complete match
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContainsAllExpression extends CollectionJsonPathBasedExpression {

    public ContainsAllExpression() {
        super(ExpressionType.contains_all);
    }

    @Builder
    public ContainsAllExpression(String path,
                                 @Singular Set<Object> values,
                                 boolean extractValues,
                                 String valuesPath,
                                 boolean defaultResult,
                                 PreOperation<?> preoperation) {
        // No pre-operations supported on this expression.
        super(ExpressionType.contains_all, path, JsonUtils.convertToJsonNode(values), extractValues, valuesPath, defaultResult, null);
    }

    @Override
    protected boolean evaluate(JsonNode evaluatedNode, Set<Object> values) {
        if (!evaluatedNode.isArray()) {
            return false;
        }
        return JsonUtils.checkAllMatch((ArrayNode) evaluatedNode, values);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor, JsonNode jsonNode) {
        return visitor.visit(this, jsonNode);
    }
}
