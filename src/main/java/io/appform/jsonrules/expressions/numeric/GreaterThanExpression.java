package io.appform.jsonrules.expressions.numeric;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import lombok.Builder;

/**
 * Created by santanu on 15/9/16.
 */
public class GreaterThanExpression extends NumericJsonPathBasedExpression {
    public GreaterThanExpression() {
        super(ExpressionType.greater_than);
    }

    @Builder
    public GreaterThanExpression(String path, Number value) {
        super(ExpressionType.greater_than, path, value);
    }

    protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult) {
        return comparisonResult > 0;
    }

}
