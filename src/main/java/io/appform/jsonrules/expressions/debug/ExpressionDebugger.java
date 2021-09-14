/**
 * Copyright (c) 2021 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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

package io.appform.jsonrules.expressions.debug;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionType;
import io.appform.jsonrules.ExpressionVisitor;
import io.appform.jsonrules.expressions.array.ContainsAllExpression;
import io.appform.jsonrules.expressions.array.ContainsAnyExpression;
import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.composite.AndExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.meta.ExistsExpression;
import io.appform.jsonrules.expressions.meta.NotExistsExpression;
import io.appform.jsonrules.expressions.numeric.BetweenExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.expressions.string.EmptyExpression;
import io.appform.jsonrules.expressions.string.EndsWithExpression;
import io.appform.jsonrules.expressions.string.MatchesExpression;
import io.appform.jsonrules.expressions.string.NotEmptyExpression;
import io.appform.jsonrules.expressions.string.StartsWithExpression;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@Builder
public class ExpressionDebugger implements ExpressionVisitor<FailureDetail> {
    private static final FailureDetail DEFAULT_SUCCESS_RESPONSE = FailureDetail.builder()
            .failed(false)
            .build();
    private final Expression expression;
    private final JsonNode node;

    public FailureDetail debug() {
        return expression.accept(this, node);
    }

    @Override
    public FailureDetail visit(AndExpression expression, JsonNode node) {
        final List<FailureDetail> details = evaluteChildren(expression.getChildren(), node);
        if (details.isEmpty()) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return generateDetails(expression.getType(), details);
    }

    @Override
    public FailureDetail visit(OrExpression expression, JsonNode node) {
        final List<FailureDetail> details = evaluteChildren(expression.getChildren(), node);
        if (details.size() < expression.getChildren()
                .size()) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return generateDetails(expression.getType(), details);
    }

    @Override
    public FailureDetail visit(NotExpression expression, JsonNode node) {
        final List<FailureDetail> details = evaluteChildren(expression.getChildren(), node);
        if (details.size() == expression.getChildren()
                .size()) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return generateDetails(expression.getType(), details);
    }

    @Override
    public FailureDetail visit(ExistsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Path [%s] doesn't exist", expression.getPath()));
    }

    @Override
    public FailureDetail visit(NotExistsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Path [%s] exists", expression.getPath()));
    }

    @Override
    public FailureDetail visit(GreaterThanExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is not greater than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(GreaterThanEqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is less than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(LessThanExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is not less than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(LessThanEqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is greater than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(EqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is not equals to [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(NotEqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is equal to [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(EmptyExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value at path [%s] is not empty", expression.getPath()));
    }

    @Override
    public FailureDetail visit(NotEmptyExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value at path [%s] is empty", expression.getPath()));
    }

    @Override
    public FailureDetail visit(StartsWithExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] doesn't start with [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(EndsWithExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] doesn't end with [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(MatchesExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] doesn't match with [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue()));
    }

    @Override
    public FailureDetail visit(InExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is not allowed", value, expression.getPath()));
    }

    @Override
    public FailureDetail visit(NotInExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is blocked", value, expression.getPath()));
    }

    @Override
    public FailureDetail visit(ContainsAnyExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("None of the values at path [%s] are allowed", expression.getPath()));
    }

    @Override
    public FailureDetail visit(ContainsAllExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Not all of the expected values at path [%s] are present", expression.getPath()));
    }

    @Override
    public FailureDetail visit(BetweenExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        return generateDetails(expression.getType(),
                expression.getPath(),
                value,
                expression.evaluate(node),
                String.format("Value of [%s] at path [%s] is not between [%s] & [%s]",
                        value,
                        expression.getPath(),
                        expression.getLowerBound(),
                        expression.getUpperBound()));
    }

    private List<FailureDetail> evaluteChildren(List<Expression> expressions, JsonNode node) {
        final List<FailureDetail> details = expressions.stream()
                .map(e -> e.accept(this, node))
                .filter(debugResult -> debugResult.isFailed())
                .collect(Collectors.toList());
        return details;
    }

    private FailureDetail generateDetails(final ExpressionType type, final List<FailureDetail> details) {
        return FailureDetail.builder()
                .expressionType(type)
                .failed(true)
                .reason(details.stream()
                        .map(FailureDetail::getReason)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .build();
    }

    private FailureDetail generateDetails(final ExpressionType type,
            final String expressionPath,
            final Object value,
            final boolean result,
            final String reason) {
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return FailureDetail.builder()
                .expressionType(type)
                .path(expressionPath)
                .value(value)
                .failed(true)
                .reason(Collections.singletonList(reason))
                .build();
    }

    private Object fetchValue(JsonNode jsonNode, String path) {
        if (jsonNode != null && path != null) {
            try {
                return JsonPath.read(jsonNode.toString(), path);
            } catch (PathNotFoundException e) {
                // ignore
            }
        }
        return null;
    }

}
