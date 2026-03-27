package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.expressions.debug.FailureDetail;
import io.appform.jsonrules.expressions.string.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class StringBasedExpressionDebugTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @BeforeEach
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
        Assertions.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assertions.assertFalse(debugPositive.isFailed());
        Assertions.assertTrue(Objects.isNull(debugPositive.getReason()));

        final Expression negativeCase = EmptyExpression.builder()
                .path("$.string")
                .defaultResult(false)
                .build();
        Assertions.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assertions.assertTrue(debugNegative.isFailed());
        Assertions.assertEquals("Value at path [$.string] is not empty", debugNegative.getReason().get(0));
    }

    @Test
    public void testNotEmptyExpression() throws Exception {

        final NotEmptyExpression positiveCase = NotEmptyExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assertions.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assertions.assertFalse(debugPositive.isFailed());
        Assertions.assertTrue(Objects.isNull(debugPositive.getReason()));

        final NotEmptyExpression negativeCase = NotEmptyExpression.builder()
                .path("$.emptyString")
                .defaultResult(false)
                .build();
        Assertions.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assertions.assertTrue(debugNegative.isFailed());
        Assertions.assertEquals("Value at path [$.emptyString] is empty", debugNegative.getReason().get(0));

    }

    @Test
    public void testStartsWithExpression() throws Exception {
        final StartsWithExpression positiveCase = StartsWithExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assertions.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assertions.assertFalse(debugPositive.isFailed());
        Assertions.assertTrue(Objects.isNull(debugPositive.getReason()));

        final StartsWithExpression negativeCase = StartsWithExpression.builder()
                .path("$.string")
                .value("he")
                .defaultResult(false)
                .build();
        Assertions.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assertions.assertTrue(debugNegative.isFailed());
        Assertions.assertEquals("Value of [Hello] at path [$.string] doesn't start with [he]", debugNegative.getReason().get(0));
    }

    @Test
    public void testEndsWithExpression() throws Exception {
        final EndsWithExpression positiveCase = EndsWithExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assertions.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assertions.assertFalse(debugPositive.isFailed());
        Assertions.assertTrue(Objects.isNull(debugPositive.getReason()));

        final EndsWithExpression negativeCase = EndsWithExpression.builder()
                .path("$.string")
                .value("LO")
                .defaultResult(false)
                .build();
        Assertions.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assertions.assertTrue(debugNegative.isFailed());
        Assertions.assertEquals("Value of [Hello] at path [$.string] doesn't end with [LO]", debugNegative.getReason().get(0));
    }

    @Test
    public void testMatchesExpression() throws Exception {
        final MatchesExpression positiveCase = MatchesExpression.builder()
                .path("$.somepath")
                .defaultResult(true)
                .build();
        Assertions.assertTrue(positiveCase.evaluate(context));
        final FailureDetail debugPositive = positiveCase.debug(context.getNode());
        Assertions.assertFalse(debugPositive.isFailed());
        Assertions.assertTrue(Objects.isNull(debugPositive.getReason()));

        final MatchesExpression negativeCase = MatchesExpression.builder()
                .path("$.string")
                .value(".*LO")
                .defaultResult(false)
                .build();
        Assertions.assertFalse(negativeCase.evaluate(context));
        final FailureDetail debugNegative = negativeCase.debug(context.getNode());
        Assertions.assertTrue(debugNegative.isFailed());
        Assertions.assertEquals("Value of [Hello] at path [$.string] doesn't match with [.*LO]", debugNegative.getReason().get(0));
    }

}
