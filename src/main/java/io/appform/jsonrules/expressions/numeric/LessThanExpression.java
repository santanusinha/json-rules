/*
 * Copyright (c) 2016 Santanu Sinha <santanu.sinha@gmail.com>
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

package io.appform.jsonrules.expressions.numeric;

import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Builder;

/**
 * Created by santanu on 15/9/16.
 */
public class LessThanExpression extends NumericJsonPathBasedExpression {
    public LessThanExpression() {
        super(ExpressionType.less_than);
    }

    @Builder
    public LessThanExpression(String path, Number value, boolean defaultResult, PreOperation<?> preoperation) {
        super(ExpressionType.less_than, path, value, defaultResult, preoperation);
    }

    public LessThanExpression(String path, Number value, PreOperation<?> preoperation) {
        this(path, value, false, preoperation);
    }

    protected boolean evaluate(ExpressionEvaluationContext context, int comparisonResult) {
        return comparisonResult < 0;
    }

}
