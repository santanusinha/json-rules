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

package io.appform.jsonrules.expressions.string;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.ExpressionVisitor;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Check is string at json path ends with given value
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EndsWithExpression extends StringJsonPathBasedExpression {

    public EndsWithExpression() {
        super(ExpressionType.ends_with);
    }

    @Builder
    public EndsWithExpression(String path, String value, boolean ignoreCase, boolean extractValueFromPath,
            Boolean defaultResult, PreOperation<?> preoperation) {
        super(ExpressionType.ends_with, path, value, ignoreCase, extractValueFromPath, defaultResult, preoperation);
    }

    @Override
    protected boolean evaluate(String leftValue, String rightValue, boolean ignoreCase) {
        if (!Strings.isNullOrEmpty(leftValue) && !Strings.isNullOrEmpty(rightValue)) {
            if (ignoreCase) {
                return leftValue.toLowerCase().endsWith(rightValue.toLowerCase());
            }
            return leftValue.endsWith(rightValue);
        }
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor, JsonNode jsonNode) {
        return visitor.visit(this, jsonNode);
    }
}
