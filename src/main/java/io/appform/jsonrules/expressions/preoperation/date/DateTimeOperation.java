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
import io.appform.jsonrules.expressions.preoperation.PreOperationType;
import io.appform.jsonrules.utils.PreOperationUtils;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DateTimeOperation extends CalendarOperation {

	protected DateTimeOperation() {
		super(PreOperationType.date_time);
	}
	public DateTimeOperation(String operand, String zoneOffSet) {
		super(PreOperationType.date_time, operand, zoneOffSet);
	}

	@Builder
	public DateTimeOperation(String operand, String zoneOffSet, String pattern) {
		super(PreOperationType.date_time, operand, zoneOffSet, pattern);
	}

	@Override
	public Number compute(JsonNode evaluatedNode, String operand, String zoneOffset) {
		try {
			final OffsetDateTime dateTime = PreOperationUtils.getDateTime(evaluatedNode.asText(), zoneOffset);
			return PreOperationUtils.getFromDateTime(dateTime, operand);
		} catch (Exception e) {
			throw new IllegalArgumentException("Operand doesnot represent a valid date");
		}
	}

	@Override
	public Number compute(JsonNode evaluatedNode, String operand, String zoneOffset, String pattern) {
		try {
			final OffsetDateTime dateTime = PreOperationUtils.getDateTimeFromLocalTimeFormat(evaluatedNode.asText(), zoneOffset, pattern);
			return PreOperationUtils.getFromDateTime(dateTime, operand);
		} catch (Exception e) {
			throw new IllegalArgumentException("Operand doesnot represent a valid date");
		}
	}

}
