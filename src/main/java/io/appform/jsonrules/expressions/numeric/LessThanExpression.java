package io.appform.jsonrules.expressions.numeric;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import lombok.Builder;

/**
 * Created by santanu on 15/9/16.
 */
public class LessThanExpression extends NumericJsonPathBasedExpression {
    public LessThanExpression() {
        super(ExpressionType.less_than);
    }

    @Builder
    public LessThanExpression(String path, Number value) {
        super(ExpressionType.less_than, path, value);
    }

    protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult) {
        return comparisonResult < 0;
    }

}
