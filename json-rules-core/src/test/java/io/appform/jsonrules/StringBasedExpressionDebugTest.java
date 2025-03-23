package io.appform.jsonrules;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appform.jsonrules.expressions.debug.FailureDetail;
import io.appform.jsonrules.expressions.string.EmptyExpression;
import io.appform.jsonrules.expressions.string.EndsWithExpression;
import io.appform.jsonrules.expressions.string.MatchesExpression;
import io.appform.jsonrules.expressions.string.NotEmptyExpression;
import io.appform.jsonrules.expressions.string.StartsWithExpression;

public class StringBasedExpressionDebugTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(
                "{ \"value\": 20, \"emptyString\" : \"\", \"s3\" : \"Hello.*\", \"s1\" : \"HelloAllHello\", \"s2\" : \"Hello\",\"string\" : \"Hello\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder()
                .node(node)
                .build();
    }

    @Test
    public void testEmptyExpression() throws Exception {
        final Expression positiveCase = EmptyExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isFailed());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final Expression negativeCase = EmptyExpression.builder()
                .path("$.string")
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isFailed());
        Assert.assertEquals("Value at path [$.string] is not empty", debugNegative.getReason().get(0));
    }

    @Test
    public void testNotEmptyExpression() throws Exception {

        final NotEmptyExpression positiveCase = NotEmptyExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isFailed());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final NotEmptyExpression negativeCase = NotEmptyExpression.builder()
                .path("$.emptyString")
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isFailed());
        Assert.assertEquals("Value at path [$.emptyString] is empty", debugNegative.getReason().get(0));

    }

    @Test
    public void testStartsWithExpression() throws Exception {
        final StartsWithExpression positiveCase = StartsWithExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isFailed());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final StartsWithExpression negativeCase = StartsWithExpression.builder()
                .path("$.string")
                .value("he")
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isFailed());
        Assert.assertEquals("Value of [Hello] at path [$.string] doesn't start with [he]", debugNegative.getReason().get(0));
    }

    @Test
    public void testEndsWithExpression() throws Exception {
        final EndsWithExpression positiveCase = EndsWithExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isFailed());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final EndsWithExpression negativeCase = EndsWithExpression.builder()
                .path("$.string")
                .value("LO")
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isFailed());
        Assert.assertEquals("Value of [Hello] at path [$.string] doesn't end with [LO]", debugNegative.getReason().get(0));
    }

    @Test
    public void testMatchesExpression() throws Exception {
        final MatchesExpression positiveCase = MatchesExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isFailed());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final MatchesExpression negativeCase = MatchesExpression.builder()
                .path("$.string")
                .value(".*LO")
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isFailed());
        Assert.assertEquals("Value of [Hello] at path [$.string] doesn't match with [.*LO]", debugNegative.getReason().get(0));
    }

}
