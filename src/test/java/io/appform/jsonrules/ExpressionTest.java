package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.expressions.composite.AndExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.InExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.equality.NotInExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {

    @Test
    public void testExpressions() throws Exception { //Todo:: break

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
        ExpressionEvaluationContext context = ExpressionEvaluationContext.builder().node(node).build();

        Assert.assertTrue(GreaterThanExpression.builder()
                .path("/value")
                .value(5)
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("/value")
                .value(30)
                .build()
                .evaluate(context));
        Assert.assertTrue(
                AndExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));
        Assert.assertTrue(
                AndExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));

        Assert.assertTrue(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(20)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));

        Assert.assertTrue(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(21)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .build()
                        .evaluate(context));
        Assert.assertFalse(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(11)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .build()
                        .evaluate(context));

        Assert.assertTrue(
                NotExpression.builder()
                        .child(
                                OrExpression.builder()
                                        .child(LessThanExpression.builder()
                                                .path("/value")
                                                .value(11)
                                                .build())
                                        .child(GreaterThanExpression.builder()
                                                .path("/value")
                                                .value(30)
                                                .build())
                                        .build())
                        .build()
                        .evaluate(context));

        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .value(20)
            .build()
            .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/value")
                .value(10)
                .build()
                .evaluate(context));

        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/value")
                .value(10)
                .build()
                .evaluate(context));


        Assert.assertTrue(EqualsExpression.builder()
                .path("/string")
                .value("Hello")
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/string")
                .value("hello")
                .build()
                .evaluate(context));

        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/string")
                .value("Hello")
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/string")
                .value("hello")
                .build()
                .evaluate(context));

        Assert.assertTrue(InExpression.builder()
                .path("/string")
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertFalse(InExpression.builder()
                .path("/string")
                .value("hello")
                .value("world")
                .build()
                .evaluate(context));

        Assert.assertFalse(NotInExpression.builder()
                .path("/string")
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertTrue(NotInExpression.builder()
                .path("/string")
                .value("hello")
                .value("world")
                .build()
                .evaluate(context));

    }

}