package io.appform.jsonrules.expressions.numeric;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All numeric binary expressions
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class NumericJsonPathBasedExpression extends JsonPathBasedExpression {
    private Number value;

    protected NumericJsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected NumericJsonPathBasedExpression(ExpressionType type, String path, Number value) {
        super(type, path);
        this.value = value;
    }

    @Override
    protected final boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        if(!evaluatedNode.isNumber()) {
            return false;
        }
        int comparisonResult = 0;
        if(evaluatedNode.isIntegralNumber()) {
            comparisonResult = Long.compare(evaluatedNode.asLong(), value.longValue());
        }
        else if(evaluatedNode.isFloatingPointNumber()) {
            comparisonResult = Double.compare(evaluatedNode.asDouble(), value.doubleValue());
        }
        return evaluate(context, comparisonResult);
    }

    abstract protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult);
}
