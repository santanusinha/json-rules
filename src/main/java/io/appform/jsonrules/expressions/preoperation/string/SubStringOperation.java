/**
 * Copyright (c) 2018 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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

package io.appform.jsonrules.expressions.preoperation.string;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import io.appform.jsonrules.expressions.preoperation.PreOperationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubStringOperation extends PreOperation<String> {

    private static final String EMPTY_STRING = "";
    @Builder.Default
    private int beginIndex = -1;
    @Builder.Default
    private int endIndex = -1;
    private boolean suppressExceptions;

    public SubStringOperation() {
        super(PreOperationType.sub_str);
        // Work-around for https://github.com/rzwitserloot/lombok/issues/1347
        this.beginIndex = -1;
        this.endIndex = -1;
    }

    @Builder
    public SubStringOperation(int beginIndex, int endIndex, boolean suppressExceptions) {
        this();
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.suppressExceptions = suppressExceptions;
    }

    @Override
    public String compute(ExpressionEvaluationContext context) {
        try {
            final JsonNode node = context.getNode();
            if (node.isTextual()) {
                final String nodeText = node.asText(EMPTY_STRING);
                if (beginIndex >= 0 && beginIndex < nodeText.length()) {
                    if (endIndex == -1) {
                        return nodeText.substring(beginIndex);
                    } else if (endIndex >= 0 && endIndex >= beginIndex && endIndex <= nodeText.length()) {
                        return nodeText.substring(beginIndex, endIndex);
                    }
                }
            }
            if (suppressExceptions) {
                return EMPTY_STRING;
            }
            throw new IllegalArgumentException("Sub-String operation is not supported");
        } catch (Exception e) {
            if (suppressExceptions) {
                return EMPTY_STRING;
            }
            throw e;
        }
    }
}
