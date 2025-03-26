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

package io.appform.jsonrules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import io.appform.jsonrules.expressions.array.ContainsAllExpression;
import io.appform.jsonrules.expressions.array.ContainsAnyExpression;
import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.composite.AndExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.debug.ExpressionDebugger;
import io.appform.jsonrules.expressions.debug.FailureDetail;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.meta.ExistsExpression;
import io.appform.jsonrules.expressions.meta.NotExistsExpression;
import io.appform.jsonrules.expressions.numeric.*;
import io.appform.jsonrules.expressions.string.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

/**
 * A base expression
 */
@Data
@EqualsAndHashCode
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "equals", value = EqualsExpression.class),
        @JsonSubTypes.Type(name = "not_equals", value = NotEqualsExpression.class),

        @JsonSubTypes.Type(name = "greater_than", value = GreaterThanExpression.class),
        @JsonSubTypes.Type(name = "greater_than_equals", value = GreaterThanEqualsExpression.class),
        @JsonSubTypes.Type(name = "less_than", value = LessThanExpression.class),
        @JsonSubTypes.Type(name = "less_than_equals", value = LessThanEqualsExpression.class),
        @JsonSubTypes.Type(name = "between", value = BetweenExpression.class),

        @JsonSubTypes.Type(name = "and", value = AndExpression.class),
        @JsonSubTypes.Type(name = "or", value = OrExpression.class),
        @JsonSubTypes.Type(name = "not", value = NotExpression.class),

        @JsonSubTypes.Type(name = "exists", value = ExistsExpression.class),
        @JsonSubTypes.Type(name = "not_exists", value = NotExistsExpression.class),

        @JsonSubTypes.Type(name = "empty", value = EmptyExpression.class),
        @JsonSubTypes.Type(name = "not_empty", value = NotEmptyExpression.class),
        @JsonSubTypes.Type(name = "starts_with", value = StartsWithExpression.class),
        @JsonSubTypes.Type(name = "ends_with", value = EndsWithExpression.class),
        @JsonSubTypes.Type(name = "matches", value = MatchesExpression.class),

        @JsonSubTypes.Type(name = "in", value = InExpression.class),
        @JsonSubTypes.Type(name = "not_in", value = NotInExpression.class),
        @JsonSubTypes.Type(name = "contains_any", value = ContainsAnyExpression.class),
        @JsonSubTypes.Type(name = "contains_all", value = ContainsAllExpression.class),
})
public abstract class Expression {
    private final ExpressionType type;

    protected Expression(ExpressionType type) {
        this.type = type;
    }

    public boolean evaluate(JsonNode node) {
        return evaluate(node, Collections.emptyMap());
    }

    public boolean evaluate(JsonNode node, Map<OptionKeys, Object> options) {
        if (null == node) {
            // Fail safe check, to replace null with missing node.
            node = MissingNode.getInstance();
        }
        return evaluate(ExpressionEvaluationContext.builder()
                .node(node)
                .options(options)
                .build());
    }

    public FailureDetail debug(JsonNode node) {
        return ExpressionDebugger.builder()
                .expression(this)
                .node(node)
                .build()
                .debug();
    }

    public abstract boolean evaluate(ExpressionEvaluationContext context);

    public abstract <T> T accept(ExpressionVisitor<T> visitor, JsonNode node);
}
