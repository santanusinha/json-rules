package io.appform.jsonrules.expressions.numeric;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import lombok.Builder;

/**
 * Created by santanu on 15/9/16.
 */
public class LessThanEqualsExpression extends NumericJsonPathBasedExpression {
    public LessThanEqualsExpression() {
        super(ExpressionType.less_than_equals);
    }

    @Builder
    public LessThanEqualsExpression(String path, Number value) {
        super(ExpressionType.less_than_equals, path, value);
    }

    protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult) {
        return comparisonResult <= 0;
    }

}
