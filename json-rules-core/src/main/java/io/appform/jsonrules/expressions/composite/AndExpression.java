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

package io.appform.jsonrules.expressions.composite;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.ExpressionVisitor;
import lombok.*;

import java.util.List;
import java.util.function.Predicate;

/**
 * And operator
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AndExpression extends CompositeExpression {
    public AndExpression() {
        super(ExpressionType.and);
    }

    @Builder
    public AndExpression(@Singular List<Expression> children) {
        super(ExpressionType.and, children);
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, List<Expression> children) {
        return children.stream()
                .map(expression -> expression.evaluate(context))
                .allMatch(Predicate.isEqual(true));
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor, JsonNode jsonNode) {
        return visitor.visit(this, jsonNode);
    }
}
