package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
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
import io.appform.jsonrules.expressions.numeric.*;
import io.appform.jsonrules.expressions.string.*;

public interface ExpressionVisitor<T> {
    T visit(AndExpression expression, JsonNode node);

    T visit(OrExpression expression, JsonNode node);

    T visit(NotExpression expression, JsonNode node);

    T visit(ExistsExpression expression, JsonNode node);

    T visit(NotExistsExpression expression, JsonNode node);

    T visit(GreaterThanExpression expression, JsonNode node);

    T visit(GreaterThanEqualsExpression expression, JsonNode node);

    T visit(LessThanExpression expression, JsonNode node);

    T visit(LessThanEqualsExpression expression, JsonNode node);

    T visit(EqualsExpression expression, JsonNode node);

    T visit(NotEqualsExpression expression, JsonNode node);

    T visit(EmptyExpression expression, JsonNode node);

    T visit(NotEmptyExpression expression, JsonNode node);

    T visit(StartsWithExpression expression, JsonNode node);

    T visit(EndsWithExpression expression, JsonNode node);

    T visit(MatchesExpression expression, JsonNode node);

    T visit(InExpression expression, JsonNode node);

    T visit(NotInExpression expression, JsonNode node);

    T visit(ContainsAnyExpression expression, JsonNode node);

    T visit(ContainsAllExpression expression, JsonNode node);

    T visit(BetweenExpression expression, JsonNode node);
}
