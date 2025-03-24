/**
 * Copyright (c) 2017 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.appform.jsonrules.expressions.preoperation.date;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.expressions.preoperation.PreOperationType;
import io.appform.jsonrules.utils.PreOperationUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All Date related pre-operations
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class CalendarOperation extends PreOperation<Number> {
	private String operand;
	private String zoneOffSet;
	private String pattern;

	protected CalendarOperation(PreOperationType type) {
		super(type);
	}
	
	protected CalendarOperation(PreOperationType type, String operand, String zoneOffSet) {
		this(type);
		this.operand = operand;
		this.zoneOffSet = zoneOffSet;
	}
	protected CalendarOperation(PreOperationType type, String operand, String zoneOffSet, String pattern) {
		this(type, operand, zoneOffSet);
		this.pattern = pattern;
	}

	public Number compute(JsonNode evaluatedNode) {
		if (operand != null && (evaluatedNode.isNumber() || evaluatedNode.isTextual())) {
			return compute(evaluatedNode, operand, zoneOffSet, pattern);
		}
		else {
			throw new IllegalArgumentException("Operands do not represent valid values");
		}
	}

	@Override
	public Number compute(ExpressionEvaluationContext context) {
		JsonNode node = context.getNode();
		return compute(node);
	}
	protected abstract Number compute(JsonNode evaluatedNode, String operand, String zoneOffSet, String pattern);

}
