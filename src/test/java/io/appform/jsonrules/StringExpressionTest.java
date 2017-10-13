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

import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.string.EmptyExpression;
import io.appform.jsonrules.expressions.string.EndsWithExpression;
import io.appform.jsonrules.expressions.string.MatchesExpression;
import io.appform.jsonrules.expressions.string.NotEmptyExpression;
import io.appform.jsonrules.expressions.string.StartsWithExpression;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;

public class StringExpressionTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"value\": 20, \"emptyString\" : \"\", \"s3\" : \"Hello.*\", \"s1\" : \"HelloAllHello\", \"s2\" : \"Hello\",\"string\" : \"Hello\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }
 
    @Test
    public void testEmptyExpression() throws Exception {
        Assert.assertTrue(EmptyExpression.builder()
        		.path("/somepath")
        		.defaultResult(true)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(EmptyExpression.builder()
        		.path("/string")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(EmptyExpression.builder()
        		.path("/emptyString")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(EmptyExpression.builder()
        		.path("/kid") // value is null; hence not a textual node
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(EmptyExpression.builder()
        		.path("/efgh")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
    }

    @Test
    public void testNotEmptyExpression() throws Exception {
    	Assert.assertTrue(NotEmptyExpression.builder()
    			.path("/somepath")
    			.defaultResult(true)
    			.build()
    			.evaluate(context));
    	Assert.assertTrue(NotEmptyExpression.builder()
    			.path("/string")
    			.build()
    			.evaluate(context));
    	Assert.assertFalse(NotEmptyExpression.builder()
        		.path("/emptyString")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
    	Assert.assertFalse(NotEmptyExpression.builder()
    			.path("/kid") // value is null; hence not a textual node
    			.build()
    			.evaluate(context));
    	Assert.assertFalse(NotEmptyExpression.builder()
    			.path("/efgh")
    			.build()
    			.evaluate(context));
    }

    @Test
    public void testStartsWithExpression() throws Exception {
        Assert.assertTrue(StartsWithExpression.builder()
        		.path("/somepath")
        		.defaultResult(true)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(StartsWithExpression.builder()
        		.path("/string")
        		.value("He")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(StartsWithExpression.builder()
        		.path("/string")
        		.value("he")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(StartsWithExpression.builder()
        		.path("/string")
        		.value("he")
        		.ignoreCase(true)
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(StartsWithExpression.builder()
        		.path("/kid") // value is null; hence not a textual node
        		.value("xyz")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(StartsWithExpression.builder()
        		.path("/efgh")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(StartsWithExpression.builder()
                .path("/s1")
                .value("/s2")
                .extractValueFromPath(true)
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testEndsWithExpression() throws Exception {
        Assert.assertTrue(EndsWithExpression.builder()
        		.path("/somepath")
        		.defaultResult(true)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(EndsWithExpression.builder()
        		.path("/string")
        		.value("lo")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(EndsWithExpression.builder()
        		.path("/string")
        		.value("LO")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(EndsWithExpression.builder()
        		.path("/string")
        		.value("LO")
        		.ignoreCase(true)
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(EndsWithExpression.builder()
        		.path("/kid") // value is null; hence not a textual node
        		.value("xyz")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(EndsWithExpression.builder()
        		.path("/efgh")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(EndsWithExpression.builder()
                .path("/s1")
                .value("/s2")
                .extractValueFromPath(true)
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testMatchesExpression() throws Exception {
        Assert.assertTrue(MatchesExpression.builder()
        		.path("/somepath")
        		.defaultResult(true)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(MatchesExpression.builder()
        		.path("/string")
        		.value(".*lo")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(MatchesExpression.builder()
        		.path("/string")
        		.value("H.?llo")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(MatchesExpression.builder()
        		.path("/string")
        		.value(".*LO")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(MatchesExpression.builder()
        		.path("/string")
        		.value(".*LO")
        		.ignoreCase(true)
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(MatchesExpression.builder()
        		.path("/kid") // value is null; hence not a textual node
        		.value("xyz")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertFalse(MatchesExpression.builder()
        		.path("/efgh")
        		.defaultResult(false)
        		.build()
        		.evaluate(context));
        Assert.assertTrue(MatchesExpression.builder()
                .path("/s1")
                .value("/s3")
                .extractValueFromPath(true)
                .defaultResult(false)
                .build()
                .evaluate(context));
    }
    
    @Test
    public void testEmptyRule() throws Exception {
        final String ruleRepr = TestUtils.read("/emptyExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"string\" : \"\" }");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testNotEmptyRule() throws Exception {
        final String ruleRepr = TestUtils.read("/notEmptyExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"string\" : \"Hello World\" }");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testStartsWithRule() throws Exception {
        final String ruleRepr = TestUtils.read("/startsWithExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"string\" : \"Hello World\" }");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testEndsWithRule() throws Exception {
        final String ruleRepr = TestUtils.read("/endsWithExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"string\" : \"Hello World\" }");
        Assert.assertTrue(rule.matches(node));
    }

    @Test
    public void testMatchesRule() throws Exception {
        final String ruleRepr = TestUtils.read("/matchesExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"string\" : \"Hello World\" }");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testExtractPathRule() throws Exception {
        final String ruleRepr = TestUtils.read("/extractPathExpression.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 8, \"s1\" : \"Hello World\", \"s2\" : \"Hello.*\" }");
        Assert.assertTrue(rule.matches(node));
    }

    @Test
    public void testRepresentation() throws Exception {
        Rule rule = new Rule(NotExpression.builder()
                .child(
                        OrExpression.builder()
                                .child(MatchesExpression.builder()
                                        .path("/string")
                                        .value(".*WORLD")
                                        .ignoreCase(true)
                                        .defaultResult(false)
                                        .build())
                                .child(StartsWithExpression.builder()
                                        .path("/string")
                                        .value("Hello")
                                        .defaultResult(false)
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"mathces\",\"path\":\"/string\",\"defaultResult\":false,\"value\":\".*WORLD\",\"ignoreCase\":true,\"extractValueFromPath\":false},{\"type\":\"starts_with\",\"path\":\"/string\",\"defaultResult\":false,\"value\":\"Hello\",\"ignoreCase\":false,\"extractValueFromPath\":false}]}]}", ruleRep);
    }
    

}
