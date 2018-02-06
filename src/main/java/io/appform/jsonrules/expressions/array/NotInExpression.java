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

package io.appform.jsonrules.expressions.array;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.utils.ComparisonUtils;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

/**
 * Compares objects
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotInExpression extends CollectionJsonPathBasedExpression {
    public NotInExpression() {
        super(ExpressionType.not_in);
        setDefaultResult(true);
    }

    @Builder
    public NotInExpression(String path, @Singular Set<Object> values, boolean extractValues, String valuesPath,
            Boolean defaultResult, PreOperation<?> preoperation) {
        super(ExpressionType.not_in, path, values, extractValues, valuesPath,
                ComparisonUtils.getDefaultResult(defaultResult, true), preoperation);
    }

    @Override
    protected boolean evaluate(JsonNode evaluatedNode, Set<Object> values) {
        return (ComparisonUtils.isNodeMissingOrNull(evaluatedNode)
                || values.stream().allMatch(value -> ComparisonUtils.compare(evaluatedNode, value) != 0));
    }
}
