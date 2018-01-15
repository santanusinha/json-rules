/**
 * Copyright (c) 2018 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.expressions.preoperation.string.LengthOperation;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;

public class LengthOperationTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"array_values\":[1,2,3,4,5],\"stringifiedValue\": \"9886098860\",\"value\": 20, \"abcd\" : \"Hello\", \"string\" : \"Hello\", \"kid\": null}");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }

    @Test
    public void testPositiveCases() throws Exception {
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.abcd")
                .preoperation(LengthOperation.builder().build())
                .value(5)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.stringifiedValue")
                .preoperation(LengthOperation.builder().build())
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("$.abcd")
                .preoperation(LengthOperation.builder().build())
                .value(4)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.abcd")
                .preoperation(LengthOperation.builder().build())
                .values(Sets.newHashSet(5))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotInExpression.builder()
                .path("$.abcd")
                .preoperation(LengthOperation.builder().build())
                .values(Sets.newHashSet(6,7))
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanExpression.builder()
                .path("$.abcd")
                .preoperation(LengthOperation.builder().build())
                .value(4)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("$.abcd")
                .preoperation(LengthOperation.builder().build())
                .value(6)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testNegativeCases() throws Exception {
        try {
            EqualsExpression.builder()
            .path("$.array_values")
            .preoperation(LengthOperation.builder().build())
            .value(20)
            .build()
            .evaluate(context);
            Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Size operation is not supported", true);
        }
        
        try {
            EqualsExpression.builder()
            .path("$.kid")
            .preoperation(LengthOperation.builder().build())
            .value(20)
            .build()
            .evaluate(context);
            Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Size operation is not supported", true);
        }

        Assert.assertFalse(EqualsExpression.builder()
            .path("$.xyzx")
            .preoperation(LengthOperation.builder().build())
            .value(20)
            .defaultResult(false)
            .build()
            .evaluate(context));
    }

    @Test
    public void testRule() throws Exception {
        final String ruleRepr = TestUtils.read("/lengthOperation.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": \"Hello World\", \"string\" : \"Hello\" }");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testRepresentation() throws Exception {
        Rule rule = new Rule(NotExpression.builder()
                .child(
                        OrExpression.builder()
                                .child(LessThanExpression.builder()
                                        .path("$.value")
                                        .value(11)
                                        .preoperation(LengthOperation.builder().build())
                                        .build())
                                .child(GreaterThanExpression.builder()
                                        .path("$.value")
                                        .value(30)
                                        .preoperation(LengthOperation.builder().build())
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"less_than\",\"path\":\"$.value\",\"preoperation\":{\"operation\":\"length\"},\"defaultResult\":false,\"value\":11,\"extractValueFromPath\":false},{\"type\":\"greater_than\",\"path\":\"$.value\",\"preoperation\":{\"operation\":\"length\"},\"defaultResult\":false,\"value\":30,\"extractValueFromPath\":false}]}]}", ruleRep);
    }
    
}
