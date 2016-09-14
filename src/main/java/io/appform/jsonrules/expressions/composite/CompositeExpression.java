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

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Expression that accepts another expression
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class CompositeExpression extends Expression {
    private List<Expression> children;

    protected CompositeExpression(ExpressionType type) {
        super(type);
    }

    protected CompositeExpression(ExpressionType type, List<Expression> children) {
        this(type);
        this.children = children;
    }

    @Override
    public final boolean evaluate(ExpressionEvaluationContext context) {
        return null != children
                && evaluate(context, children);
    }

    protected abstract boolean evaluate(ExpressionEvaluationContext context, List<Expression> children);
}
