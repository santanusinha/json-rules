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
public class ExpressionDebugger implements ExpressionVisitor<DenialDetail> {
    private static final DenialDetail DEFAULT_SUCCESS_RESPONSE = DenialDetail.builder()
            .denied(false)
            .build();
    private final Expression expression;
    private final JsonNode node;

    public DenialDetail debug() {
        return expression.accept(this, node);
    }

    @Override
    public DenialDetail visit(AndExpression expression, JsonNode node) {
        final List<DenialDetail> details = expression.getChildren()
                .stream()
                .map(e -> e.accept(this, node))
                .filter(debugResult -> debugResult.isDenied())
                .collect(Collectors.toList());
        if (details.isEmpty()) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .denied(true)
                .reason(details.stream()
                        .map(DenialDetail::getReason)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public DenialDetail visit(OrExpression expression, JsonNode node) {
        final List<DenialDetail> details = expression.getChildren()
                .stream()
                .map(e -> e.accept(this, node))
                .filter(debugResult -> debugResult.isDenied())
                .collect(Collectors.toList());
        if (details.size() < expression.getChildren()
                .size()) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .denied(true)
                .reason(details.stream()
                        .map(DenialDetail::getReason)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public DenialDetail visit(NotExpression expression, JsonNode node) {
        final List<DenialDetail> details = expression.getChildren()
                .stream()
                .map(e -> e.accept(this, node))
                .filter(debugResult -> debugResult.isDenied())
                .collect(Collectors.toList());
        if (details.size() == expression.getChildren()
                .size()) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .denied(true)
                .reason(details.stream()
                        .map(DenialDetail::getReason)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public DenialDetail visit(ExistsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Path [%s] doesn't exist", expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(NotExistsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Path [%s] exists", expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(GreaterThanExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is not greater than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(GreaterThanEqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is less than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(LessThanExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is not less than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(LessThanEqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is greater than [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(EqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is not equals to [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(NotEqualsExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is equal to [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(EmptyExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections
                        .singletonList(String.format("Value at path [%s] is not empty", expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(NotEmptyExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value at path [%s] is empty", expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(StartsWithExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] doesn't start with [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(EndsWithExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] doesn't end with [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(MatchesExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] doesn't match with [%s]",
                        value,
                        expression.getPath(),
                        expression.getValue())))
                .build();
    }

    @Override
    public DenialDetail visit(InExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(
                        String.format("Value of [%s] at path [%s] is not allowed", value, expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(NotInExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(
                        String.format("Value of [%s] at path [%s] is blocked", value, expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(ContainsAnyExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(
                        String.format("None of the values at path [%s] are allowed", expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(ContainsAllExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(
                        String.format("Not all of the expected values at path [%s] are present", expression.getPath())))
                .build();
    }

    @Override
    public DenialDetail visit(BetweenExpression expression, JsonNode node) {
        final val value = fetchValue(node, expression.getPath());
        final boolean result = expression.evaluate(node);
        if (result) {
            return DEFAULT_SUCCESS_RESPONSE;
        }
        return DenialDetail.builder()
                .expressionType(expression.getType())
                .path(expression.getPath())
                .value(value)
                .denied(true)
                .reason(Collections.singletonList(String.format("Value of [%s] at path [%s] is not between [%s] & [%s]",
                        value,
                        expression.getPath(),
                        expression.getLowerBound(),
                        expression.getUpperBound())))
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
