package io.appform.jsonrules.expressions;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All expressions that evaluate a json path uses this.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class JsonPathBasedExpression extends Expression {
    private String path;

    protected JsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected JsonPathBasedExpression(ExpressionType type, String path) {
        this(type);
        this.path = path;
    }

    @Override
    public final boolean evaluate(ExpressionEvaluationContext context) {
        //T value = context.getParsedContext().read(path, clazz);
        final JsonNode evaluatedNode = context.getNode().at(path);
        return evaluate(context, path, evaluatedNode);
    }

    abstract protected boolean evaluate(ExpressionEvaluationContext context, final String path, JsonNode evaluatedNode);
}
