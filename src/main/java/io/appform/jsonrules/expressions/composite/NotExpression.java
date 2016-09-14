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
public class NotExpression extends CompositeExpression {
    public NotExpression() {
        super(ExpressionType.not);
    }

    @Builder
    public NotExpression(@Singular List<Expression> children) {
        super(ExpressionType.not, children);
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, List<Expression> children) {
        return children.stream()
                .map(expression -> expression.evaluate(context))
                .noneMatch(Predicate.isEqual(true));
    }
}
