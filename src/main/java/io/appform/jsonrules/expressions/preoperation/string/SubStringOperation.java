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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubStringOperation extends PreOperation<String> {

    private static final String EMPTY_STRING = "";
    @Builder.Default private int beginIndex = -1;
    @Builder.Default private int endIndex = -1;

    public SubStringOperation() {
        super(PreOperationType.sub_str);
    }
    
    public SubStringOperation(int beginIndex, int endIndex) {
        this();
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    @Override
    public String compute(ExpressionEvaluationContext context) {
        JsonNode node = context.getNode();
        if (node.isTextual() && beginIndex < node.asText(EMPTY_STRING).length()) {
            if (beginIndex >= 0 && endIndex >= 0 && endIndex >= beginIndex) {
                return node.asText().substring(beginIndex, endIndex);
            } else if (beginIndex >= 0 && endIndex == -1 ) {
                return node.asText().substring(beginIndex);
            }
        }
        throw new IllegalArgumentException("Sub-String operation is not supported");
    }
}
