package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.expressions.preoperation.date.DiffFromCurrentEpochOperation;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;

/**
 * Created by shubham.ppe on 25/07/17.
 */
public class DiffFromCurrentEpochOperationTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        long epoch = System.currentTimeMillis();
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\", \"kid\": null, \"epochTime\" : "+epoch+",\"setEpoch\": 1500000000000}");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }

    @Test
    public void testWithEquals() {
        Expression expression = EqualsExpression.builder().path("/setEpoch")
                .preoperation(DiffFromCurrentEpochOperation.builder()
                                .build()).value(0).build();
        Assert.assertTrue(expression.evaluate(context.getNode(), Collections.singletonMap(OptionKeys.SYSTEM_TIME,new Long(1500000000000L))));
    }

    @Test
    public void testWithIntegerSystemTime()  throws Exception{
        try {
            Expression expression = EqualsExpression.builder().path("/setEpoch")
                    .preoperation(DiffFromCurrentEpochOperation.builder()
                            .build()).value(0).build();
            expression.evaluate(context.getNode(), Collections.singletonMap(OptionKeys.SYSTEM_TIME,1500000000));
            Assert.fail("No Exception thrown");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Invalid Time", true);
        }
    }

    @Test
    public void testWithEqualsWithoutOptions() {
        Expression expression = GreaterThanExpression.builder().path("/setEpoch")
                .preoperation(DiffFromCurrentEpochOperation.builder()
                        .build()).value(0).build();
        Assert.assertTrue(expression.evaluate(context.getNode()));
    }

    @Test
    public void testRule() throws Exception {
        final String ruleRepr = TestUtils.read("/diffFromEpochOperation.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": "+System.currentTimeMillis()+" }");
        Assert.assertTrue(rule.matches(node));
    }

    @Test
    public void testRepresentation() throws Exception {
        Rule rule = new Rule(NotExpression.builder()
                .child(
                        OrExpression.builder()
                                .child(LessThanExpression.builder()
                                        .path("/value")
                                        .value(11)
                                        .preoperation(DiffFromCurrentEpochOperation.builder().build())
                                        .build())
                                .child(GreaterThanExpression.builder()
                                        .path("/value")
                                        .value(30)
                                        .preoperation(DiffFromCurrentEpochOperation.builder().build())
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"less_than\",\"path\":\"/value\",\"preoperation\":{\"operation\":\"current_epoch_diff\"},\"defaultResult\":false,\"value\":11},{\"type\":\"greater_than\",\"path\":\"/value\",\"preoperation\":{\"operation\":\"current_epoch_diff\"},\"defaultResult\":false,\"value\":30}]}]}", ruleRep);
    }
}
