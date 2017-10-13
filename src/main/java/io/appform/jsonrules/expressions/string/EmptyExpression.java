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

package io.appform.jsonrules.expressions.string;

import com.google.common.base.Strings;

import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.expressions.preoperation.PreOperation;
import lombok.Builder;

/**
 * Check is string is empty or null
 */
public class EmptyExpression extends StringJsonPathBasedExpression {
    public EmptyExpression() {
        super(ExpressionType.empty);
    }

    @Builder
    public EmptyExpression(String path, Boolean defaultResult, PreOperation<?> preoperation) {
        super(ExpressionType.empty, path, null, false, false, defaultResult, preoperation);
    }

    @Override
    protected boolean evaluate(String data, String value, boolean ignoreCase) {
        return Strings.isNullOrEmpty(data);
    }
}
