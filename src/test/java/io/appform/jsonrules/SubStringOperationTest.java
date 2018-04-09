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
import io.appform.jsonrules.expressions.preoperation.string.SubStringOperation;
import io.appform.jsonrules.expressions.string.EmptyExpression;
import io.appform.jsonrules.expressions.string.NotEmptyExpression;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;

public class SubStringOperationTest {

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
                .preoperation(SubStringOperation.builder().beginIndex(0).build())
                .value("$.abcd")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(1).build())
                .value("ello")
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(1).build())
                .value("$.abcd")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(1).endIndex(2).build())
                .values(Sets.newHashSet("a","e","i","o","u"))
                .build()
                .evaluate(context));
        Assert.assertFalse(InExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(1).build())
                .values(Sets.newHashSet("a","e","i","o","u"))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotInExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(1).build())
                .values(Sets.newHashSet("a","e","i","o","u"))
                .build()
                .evaluate(context));
        Assert.assertTrue(EmptyExpression.builder()
                .path("$.stringifiedValue")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(0).build())
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEmptyExpression.builder()
                .path("$.stringifiedValue")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(1).build())
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testNegativeCases() throws Exception {
        try {
            // On Array
            EqualsExpression.builder()
                .path("$.array_values")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(1).build())
                .value(20)
                .build()
                .evaluate(context);
                Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Sub string operation is not supported", true);
        }
        
        try {
            // On Null value
            EqualsExpression.builder()
                .path("$.kid")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(1).build())
                .value(20)
                .build()
                .evaluate(context);
                Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Sub string operation is not supported", true);
        }
        
        try {
            // On valid string, but beginIndex & endIndex not given
            EqualsExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().build())
                .value(20)
                .defaultResult(false)
                .build()
                .evaluate(context);
            Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Sub string operation is not supported", true);
        }
        
        try {
            // On valid string, but invalid endIndex given
            EqualsExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(-2).build())
                .value(20)
                .defaultResult(false)
                .build()
                .evaluate(context);
            Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Sub string operation is not supported", true);
        }
        
        try {
            // On valid string, but invalid beginIndex given
            EqualsExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().beginIndex(-2).build())
                .value(20)
                .defaultResult(false)
                .build()
                .evaluate(context);
            Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Sub string operation is not supported", true);
        }
        
        try {
            // On valid string, but beginIndex NOT given
            EqualsExpression.builder()
                .path("$.abcd")
                .preoperation(SubStringOperation.builder().endIndex(1).build())
                .value(20)
                .defaultResult(false)
                .build()
                .evaluate(context);
            Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue("Sub string operation is not supported", true);
        }
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
                                .child(EqualsExpression.builder()
                                        .path("$.value")
                                        .value("$.value")
                                        .preoperation(SubStringOperation.builder().beginIndex(0).build())
                                        .extractValueFromPath(true)
                                        .build())
                                .child(NotEqualsExpression.builder()
                                        .path("$.value")
                                        .value("$.value")
                                        .preoperation(SubStringOperation.builder().beginIndex(0).endIndex(1).build())
                                        .extractValueFromPath(true)
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"equals\",\"path\":\"$.value\",\"preoperation\":{\"operation\":\"sub_str\"},\"defaultResult\":false,\"value\":\"$.value\",\"extractValueFromPath\":true},{\"type\":\"not_equals\",\"path\":\"$.value\",\"preoperation\":{\"operation\":\"sub_str\"},\"defaultResult\":true,\"value\":\"$.value\",\"extractValueFromPath\":true}]}]}", ruleRep);
    }
    
}
