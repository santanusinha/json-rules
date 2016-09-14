package io.appform.jsonrules.expressions.equality;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.utils.ComparisonUtils;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Compares objects
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotEqualsExpression extends JsonPathBasedExpression {
    private Object value;

    public NotEqualsExpression() {
        super(ExpressionType.not_equals);
    }

    @Builder
    public NotEqualsExpression(String path, Object value) {
        super(ExpressionType.not_equals, path);
        this.value = value;
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        return ComparisonUtils.compare(evaluatedNode, value) != 0;
    }
}
