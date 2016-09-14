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
