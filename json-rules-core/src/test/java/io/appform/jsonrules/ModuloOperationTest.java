/**
 * Copyright (c) 2017 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.expressions.preoperation.numeric.ModuloOperation;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

public class ModuloOperationTest {


    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;
    private Instant dateTime;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        dateTime = Instant.now();
        long epoch = dateTime.getEpochSecond();
        String dateTimeStr = new StringBuilder().append("\"").append(dateTime.toString()).append("\"").toString();
        JsonNode node = mapper.readTree("{ \"stringifiedValue\": \"9886098860\",\"value\": 20,\"abcd\" : \"Hello\",\"string\" : \"Hello\", \"kid\": null, \"epochTime\" : "+epoch+", \"dateTime\" : "+dateTimeStr+" }");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }

    @Test
    public void testWithEqualsExpression() throws Exception {
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.stringifiedValue")
                .preoperation(ModuloOperation.builder().operand(100).build())
                .value(60)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.stringifiedValue")
                .preoperation(ModuloOperation.builder().operand(100000).build())
                .value(98860)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(EqualsExpression.builder()
                    .path("$.value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	EqualsExpression.builder()
            .path("$.abcd")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	EqualsExpression.builder()
            .path("$.kid")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }

    @Test
    public void testWithNotEqualsExpression() throws Exception {
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertFalse(NotEqualsExpression.builder()
                    .path("$.value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	NotEqualsExpression.builder()
            .path("$.abcd")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	NotEqualsExpression.builder()
            .path("$.kid")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }

    @Test
    public void testWithInExpression() throws Exception {
        Assert.assertTrue(InExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .values(Sets.newHashSet(2))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .values(Sets.newHashSet(0))
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(InExpression.builder()
                    .path("$.value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .values(Sets.newHashSet(0))
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	InExpression.builder()
            .path("$.abcd")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .values(Sets.newHashSet(20))
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	InExpression.builder()
            .path("$.kid")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .values(Sets.newHashSet(20))
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }
    
    @Test
    public void testWithNotInExpression() throws Exception {
        Assert.assertFalse(NotInExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .values(Sets.newHashSet(2))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .values(Sets.newHashSet(0))
                .build()
                .evaluate(context));
        try {
        	Assert.assertFalse(NotInExpression.builder()
                    .path("$.value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .values(Sets.newHashSet(0))
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	NotInExpression.builder()
            .path("$.abcd")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .values(Sets.newHashSet(20))
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	NotInExpression.builder()
            .path("$.kid")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .values(Sets.newHashSet(20))
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }
    
    @Test
    public void testWithNumbericExpression() throws Exception {
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("$.value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(GreaterThanExpression.builder()
                    .path("$.value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	GreaterThanExpression.builder()
            .path("$.abcd")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	GreaterThanExpression.builder()
            .path("$.kid")
            .preoperation(ModuloOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }
    
    @Test
    public void testRule() throws Exception {
        final String ruleRepr = TestUtils.read("/moduloOperation.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"string\" : \"Hello\" }");
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
                                        .preoperation(ModuloOperation.builder().operand(5).build())
                                        .build())
                                .child(GreaterThanExpression.builder()
                                        .path("$.value")
                                        .value(30)
                                        .preoperation(ModuloOperation.builder().operand(-5).build())
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"less_than\",\"path\":\"$.value\",\"preoperation\":{\"operation\":\"modulo\",\"operand\":5},\"defaultResult\":false,\"value\":11,\"extractValueFromPath\":false},{\"type\":\"greater_than\",\"path\":\"$.value\",\"preoperation\":{\"operation\":\"modulo\",\"operand\":-5},\"defaultResult\":false,\"value\":30,\"extractValueFromPath\":false}]}]}", ruleRep);
    }
    

}
