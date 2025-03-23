package io.appform.jsonrules.expressions.preoperation.date;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.OptionKeys;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.expressions.preoperation.PreOperationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by gabber12 on 24/07/17.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiffFromCurrentEpochOperation extends PreOperation<Number> {
    protected DiffFromCurrentEpochOperation() {
        super(PreOperationType.current_epoch_diff);
    }


    private Number compute(JsonNode evaluatedNode, long currentEpoch) {
        if (evaluatedNode.isLong()) {
            return currentEpoch - evaluatedNode.asLong();
        }
        throw new IllegalArgumentException("Evaluated node does not represent a valid epoch");
    }

    @Override
    public Number compute(ExpressionEvaluationContext context) {
        try {
            long currentEpoch = (long)(context.getOptions().getOrDefault(OptionKeys.SYSTEM_TIME, System.currentTimeMillis()));
            return compute(context.getNode(), currentEpoch);
        } catch (Exception e) {
            throw new IllegalArgumentException("Operands does not represent a valid epoch", e);
        }
    }
}
