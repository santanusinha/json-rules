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

package io.appform.jsonrules.expressions.preoperation.numeric;

import io.appform.jsonrules.expressions.preoperation.PreOperationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultiplyOperation extends NumericPreOperation {
	
	public MultiplyOperation() {
		super(PreOperationType.multiply);
	}
	
	@Builder
	public MultiplyOperation(Number operand) {
		super(PreOperationType.multiply, operand);
	}

	@Override
	public long compute(long leftOperand, long rightOperand) {
		return (leftOperand * rightOperand);
	}

	@Override
	public double compute(double leftOperand, double rightOperand) {
		return (leftOperand * rightOperand);
	}

}
