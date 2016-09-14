package io.appform.jsonrules.expressions.composite;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionType;
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
    public AndExpression(ExpressionType type, @Singular List<Expression> children) {
        super(type, children);
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, List<Expression> children) {
        return children.stream()
                .map(expression -> expression.evaluate(context))
                .allMatch(Predicate.isEqual(true));
    }
}
