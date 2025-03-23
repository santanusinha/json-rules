package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test to check rule functionality
 */
public class RuleTest {
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRule() throws Exception {
        final String ruleRepr = TestUtils.read("/simple.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
        Assert.assertTrue(rule.matches(node));
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            if(!rule.matches(node)) {
                System.err.println("Mismatch");
            }
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - currentTime));
    }

    @Test
    public void testDefaultResultRule() throws Exception {
        final String ruleRepr = TestUtils.read("/simple_rule_with_default.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode nodeWithMissingOperandPath = mapper.readTree("{ \"value\": 20, \"name\" : \"Hello\" }");
        Assert.assertTrue(rule.matches(nodeWithMissingOperandPath));

        final String ruleRepr2 = TestUtils.read("/simple.rule");
        Rule rule2 = Rule.create(ruleRepr2, mapper);
        Assert.assertFalse(rule2.matches(nodeWithMissingOperandPath));

        final String ruleRepr3 = TestUtils.read("/complex.rule");
        Rule rule3 = Rule.create(ruleRepr3, mapper);
        Assert.assertTrue(rule3.matches(nodeWithMissingOperandPath));
    }

    @Test
    public void testDefaultResultForNegativeRule() throws Exception {
        final String ruleRepr = TestUtils.read("/notExists.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode nodeWithMissingOperandPath = mapper.readTree("{ \"value\": 20, \"name\" : \"Hello\" }");
        Assert.assertTrue(rule.matches(nodeWithMissingOperandPath));

    }

    @Test
    @Ignore
    public void testPerf() throws Exception {
        final String ruleRepr = TestUtils.read("/complex.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
        for(int j = 0; j < 10; j++) {
            long currentTime = System.currentTimeMillis();
            for (long i = 0; i < 10_000_000; i++) {
                if(!rule.matches(node)) {
                    System.err.println("Mismatch");
                }
            }
            System.out.println("Time taken: " + (System.currentTimeMillis() - currentTime));
        }
    }

    @Test
    public void testRepresentation() throws Exception {
        Rule rule = new Rule(NotExpression.builder()
                .child(
                        OrExpression.builder()
                                .child(LessThanExpression.builder()
                                        .path("$.value")
                                        .value(11)
                                        .build())
                                .child(GreaterThanExpression.builder()
                                        .path("$.value")
                                        .value(30)
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"less_than\",\"path\":\"$.value\",\"defaultResult\":false,\"value\":11,\"extractValueFromPath\":false},{\"type\":\"greater_than\",\"path\":\"$.value\",\"defaultResult\":false,\"value\":30,\"extractValueFromPath\":false}]}]}", ruleRep);
    }
}
