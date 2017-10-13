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

package io.appform.jsonrules.expressions.string;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.JsonPathBasedExpression;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All string operable expressions
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class StringJsonPathBasedExpression extends JsonPathBasedExpression {
    private String value;
	private boolean ignoreCase;
	private boolean extractValueFromPath;

    protected StringJsonPathBasedExpression(ExpressionType type) {
        super(type);
    }

    protected StringJsonPathBasedExpression(ExpressionType type, String path, String value, boolean ignoreCase, 
    		boolean extractValueFromPath, boolean defaultResult, PreOperation<?> preoperation) {
        super(type, path, defaultResult, preoperation);
        this.value = value;
        this.ignoreCase = ignoreCase;
        this.extractValueFromPath = extractValueFromPath;
    }

    @Override
    protected final boolean evaluate(ExpressionEvaluationContext context, String path, JsonNode evaluatedNode) {
    	 if(!evaluatedNode.isTextual()) {
             return false;
         }
    	 if (extractValueFromPath) {
    		 final String extractedValue = context.getNode().at(value).asText();
    		 return evaluate(evaluatedNode.asText(), extractedValue, ignoreCase);
    	 }
         return evaluate(evaluatedNode.asText(), value, ignoreCase);
    }

    abstract protected boolean evaluate(String leftValue, String rightValue, boolean ignoreCase);
    
}
