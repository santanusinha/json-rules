package io.appform.jsonrules.expressions.equality;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.utils.ComparisonUtils;
import lombok.*;

import java.util.List;

/**
 * Compares objects
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotInExpression extends JsonPathBasedExpression {
    private List<Object> values;

    public NotInExpression() {
        super(ExpressionType.not_in);
    }

    @Builder
    public NotInExpression(String path, @Singular List<Object> values) {
        super(ExpressionType.not_in, path);
        this.values = values;
    }

    @Override
    protected boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
        return null != values
                && values.stream().allMatch(value -> ComparisonUtils.compare(evaluatedNode, value) != 0);
    }
}
