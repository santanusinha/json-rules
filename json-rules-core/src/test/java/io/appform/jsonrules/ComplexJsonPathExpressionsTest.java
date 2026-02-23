package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.config.JsonRulesConfiguration;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComplexJsonPathExpressionsTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"v1\": 20, \"v2\": [10, 20, 30], \"v3\": 20.001, \"v4\": [\"Open\", \"the\", \"pod\", \"bay\", \"doors\", \"HAL\"], \"string\" : \"Hello\", \"string1\" : \"Hello1\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder().node(node).build();
        JsonRulesConfiguration.enableSupportForComplexJsonPathExpressions(true);
    }

    @Test
    public void testUdfInvocations() throws Exception {
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("sum($.v2)")
                .value(70)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("sum($.v2)")
                .value(60)
                .build()
                .evaluate(context));
        Assert.assertFalse(LessThanEqualsExpression.builder()
                .path("sum($.v2)")
                .value(50)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("max($.v2)")
                .value(30)
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("min($.v2)")
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("$.v4.length()")
                .value(4)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4.length()")
                .value(6)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4.first()")
                .value("Open")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4.last()")
                .value("HAL")
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("$.v4.last()")
                .value("HAL1")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v2.avg()")
                .value(20.0)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4.concat(\"!\")")
                .value("OpenthepodbaydoorsHAL\"!\"")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4.append(\"!\")")
                .value("[\"Open\", \"the\", \"pod\", \"bay\", \"doors\", \"HAL\", \"!\"]")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.keys()")
                .value("[\"v1\", \"v2\", \"v3\", \"v4\", \"string\", \"string1\", \"kid\", \"boolean\"]")
                .build()
                .evaluate(context));
    }

    @Test
    public void testFilterExpressions() throws Exception {
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v2[?(@ > 15)]")
                .value("[20, 30]")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v2[?(@ > 30)]")
                .value("[]")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4[?(@ == 'Open')]")
                .value("[\"Open\"]")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.v4[?(@ == 'Closed')]")
                .value("[]")
                .build()
                .evaluate(context));
    }
}
