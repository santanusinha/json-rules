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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import io.appform.jsonrules.expressions.array.ContainsAllExpression;
import io.appform.jsonrules.expressions.array.ContainsAnyExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;

public class CollectionExpressionTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"felines\": [\"leopard\",\"lion\",\"tiger\",\"jaguar\"],\"integers\": [10,20,30,40],\"decimals\": [10.01,20.22,30.33,40.55], \"emptyString\" : \"\", \"s3\" : \"Hello.*\", \"s1\" : \"HelloAllHello\", \"s2\" : \"Hello\",\"string\" : \"Hello\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }
    
    @Test
    public void testContainsAnyExpressionPositive() throws Exception {
        Assert.assertTrue(ContainsAnyExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("leopard","lion","panther"))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(ContainsAnyExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet(20,10,40,30))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(ContainsAnyExpression.builder()
                .path("$.decimals")
                .values(Sets.newHashSet(20.22,10.01))
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testContainsAnyExpressionNegative() throws Exception {
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("panther"))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet(50))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet("20","10","30","40"))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.decimals")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.emptyString")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.kid")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAnyExpression.builder()
                .path("$.boolean")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testContainsAllExpressionPositive() throws Exception {
        Assert.assertTrue(ContainsAllExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("tiger","leopard","lion","jaguar"))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(ContainsAllExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet(20,10,40,30))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(ContainsAllExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet(20,10,40,30,50))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(ContainsAllExpression.builder()
                .path("$.decimals")
                .values(Sets.newHashSet(20.22,10.01,40.55,30.33))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertTrue(ContainsAllExpression.builder()
                .path("$.decimals")
                .values(Sets.newHashSet(20.22,10.01,40.55,30.33,10000.100050))
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testContainsAllExpressionNegative() throws Exception {
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("tiger","leopard","lion","panther"))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet(20,10,30,50))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.integers")
                .values(Sets.newHashSet("20","10","30","40"))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.decimals")
                .values(Sets.newHashSet(20,10.0101,40.55,30))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.emptyString")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.kid")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assert.assertFalse(ContainsAllExpression.builder()
                .path("$.boolean")
                .values(Sets.newHashSet(10.001))
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testContainsAnyExpressionRule() throws Exception {
        final String ruleRepr = TestUtils.read("/containsAnyExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"felines\": [\"leopard\",\"lion\",\"tiger\",\"jaguar\"],\"integers\": [10,20,30,40],\"decimals\": [10.01,20.22,30.33,40.55]}");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testContainsAllExpressionRule() throws Exception {
        final String ruleRepr = TestUtils.read("/containsAllExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"felines\": [\"leopard\",\"lion\",\"tiger\",\"jaguar\"],\"integers\": [10,20,30,40],\"decimals\": [10.01,20.22,30.33,40.55]}");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testRepresentation() throws Exception {
        Rule rule = new Rule(NotExpression.builder()
                .child(
                        OrExpression.builder()
                                .child(ContainsAnyExpression.builder()
                                        .path("$.felines")
                                        .values(Sets.newHashSet("leopard","lion","panther"))
                                        .defaultResult(false)
                                        .build())
                                .child(ContainsAllExpression.builder()
                                        .path("$.integers")
                                        .values(Sets.newHashSet(20,10,40,30))
                                        .defaultResult(false)
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"contains_any\",\"path\":\"$.felines\",\"defaultResult\":false,\"values\":[\"leopard\",\"panther\",\"lion\"]},{\"type\":\"contains_all\",\"path\":\"$.integers\",\"defaultResult\":false,\"values\":[40,10,20,30]}]}]}", ruleRep);
    }
    

}
