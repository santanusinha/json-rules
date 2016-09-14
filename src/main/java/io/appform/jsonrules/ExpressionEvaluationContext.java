package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Context passed to expression evaluator
 */
@Data
@AllArgsConstructor
@Builder
public class ExpressionEvaluationContext {
    private byte json[];
    private JsonNode node;
}
