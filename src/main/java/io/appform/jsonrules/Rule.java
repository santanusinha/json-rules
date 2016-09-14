package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A basic rule
 */
public class Rule {
    private final Expression expression;

    public Rule(Expression expression) {
        this.expression = expression;
    }

    public static Rule create(final String json, final ObjectMapper mapper) throws Exception {
        return new Rule(mapper.readValue(json, Expression.class));
    }

    public boolean matches(JsonNode node) {
        return expression.evaluate(
                ExpressionEvaluationContext.builder()
                        .node(node)
                        .build());
    }

    public String representation(ObjectMapper mapper) throws Exception {
        return mapper.writeValueAsString(expression);
    }
}
