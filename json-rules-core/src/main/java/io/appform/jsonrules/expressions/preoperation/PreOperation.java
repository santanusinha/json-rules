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

package io.appform.jsonrules.expressions.preoperation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.expressions.preoperation.array.SizeOperation;
import io.appform.jsonrules.expressions.preoperation.date.DateTimeOperation;
import io.appform.jsonrules.expressions.preoperation.date.DiffFromCurrentEpochOperation;
import io.appform.jsonrules.expressions.preoperation.date.EpochOperation;
import io.appform.jsonrules.expressions.preoperation.numeric.*;
import io.appform.jsonrules.expressions.preoperation.string.LengthOperation;
import io.appform.jsonrules.expressions.preoperation.string.SubStringOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A base operation
 */
@Data
@EqualsAndHashCode
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "operation")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "add", value = AddOperation.class),
        @JsonSubTypes.Type(name = "subtract", value = SubtractOperation.class),
        @JsonSubTypes.Type(name = "multiply", value = MultiplyOperation.class),
        @JsonSubTypes.Type(name = "divide", value = DivideOperation.class),
        @JsonSubTypes.Type(name = "modulo", value = ModuloOperation.class),

        @JsonSubTypes.Type(name = "size", value = SizeOperation.class),

        @JsonSubTypes.Type(name = "length", value = LengthOperation.class),
        @JsonSubTypes.Type(name = "sub_str", value = SubStringOperation.class),

        @JsonSubTypes.Type(name = "epoch", value = EpochOperation.class),
        @JsonSubTypes.Type(name = "date_time", value = DateTimeOperation.class),
		@JsonSubTypes.Type(name = "current_epoch_diff", value = DiffFromCurrentEpochOperation.class),
})
public abstract class PreOperation<T> {
	private final PreOperationType operation;
	
	public abstract T compute(ExpressionEvaluationContext context);
}
