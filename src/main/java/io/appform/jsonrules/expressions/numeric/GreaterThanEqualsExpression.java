package io.appform.jsonrules.expressions.numeric;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import lombok.Builder;

/**
 * Created by santanu on 15/9/16.
 */
public class GreaterThanEqualsExpression extends NumericJsonPathBasedExpression {
    public GreaterThanEqualsExpression() {
        super(ExpressionType.greater_than_equals);
    }

    @Builder
    public GreaterThanEqualsExpression(String path, Number value) {
        super(ExpressionType.greater_than_equals, path, value);
    }

    protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult) {
        return comparisonResult >= 0;
    }

}
