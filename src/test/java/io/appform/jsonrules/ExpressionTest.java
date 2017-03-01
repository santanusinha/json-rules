/*
 * Copyright (c) 2016 Santanu Sinha <santanu.sinha@gmail.com>
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
import io.appform.jsonrules.expressions.composite.AndExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.InExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.equality.NotInExpression;
import io.appform.jsonrules.expressions.meta.ExistsExpression;
import io.appform.jsonrules.expressions.meta.NotExistsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ExpressionTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }

    @Test
    public void testEqualsExpression() throws Exception {
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/abcd")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/kid")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/value")
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/string")
                .value("Hello")
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/string")
                .value("hello")
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/boolean")
                .value(true)
                .build()
                .evaluate(context));

        Assert.assertFalse(EqualsExpression.builder()
                .path("/boolean")
                .value(false)
                .build()
                .evaluate(context));

    }

    @Test
    public void testNotEqualsExpression() throws Exception {
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/value")
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/kid")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/efgh")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/string")
                .value("Hello")
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/string")
                .value("hello")
                .build()
                .evaluate(context));
    }

    @Test
    public void testInExpression() throws Exception {
        Assert.assertTrue(InExpression.builder()
                .path("/string")
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertFalse(InExpression.builder()
                .path("/kid")
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertFalse(InExpression.builder()
                .path("/abcd")
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertFalse(InExpression.builder()
                .path("/string")
                .value("hello")
                .value("world")
                .build()
                .evaluate(context));
    }

    @Test
    public void testNotInExpression() throws Exception {
        Assert.assertFalse(NotInExpression.builder()
                .path("/string")
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertTrue(NotInExpression.builder()
                .path("/string")
                .value("hello")
                .value("world")
                .build()
                .evaluate(context));

        Assert.assertTrue(NotInExpression.builder()
                .path("/string")
                .values(new ArrayList<String>() {
                    {
                        add("abcd");
                        add("efgh");
                    }
                })
                .build()
                .evaluate(context));

        Assert.assertTrue(NotInExpression.builder()
                .path("/kid")
                .values(new ArrayList<String>() {
                    {
                        add("stupid");
                        add("dumb");
                    }
                })
                .build()
                .evaluate(context));

        Assert.assertTrue(NotInExpression.builder()
                .path("/xyz")
                .values(new ArrayList<String>() {
                    {
                        add("stupid");
                        add("dumb");
                    }
                })
                .build()
                .evaluate(context));
    }

    @Test
    public void testExistsExpression() throws Exception {
        Assert.assertFalse(ExistsExpression.builder()
                .path("/somepath")
                .build()
                .evaluate(context));
        Assert.assertTrue(ExistsExpression.builder()
                .path("/string")
                .build()
                .evaluate(context));
        Assert.assertFalse(ExistsExpression.builder()
                .path("/kid")
                .build()
                .evaluate(context));
        Assert.assertFalse(ExistsExpression.builder()
                .path("/efgh")
                .build()
                .evaluate(context));
    }

    @Test
    public void testNotExistsExpression() throws Exception {
        Assert.assertTrue(NotExistsExpression.builder()
                .path("/somepath")
                .build()
                .evaluate(context));
        Assert.assertFalse(NotExistsExpression.builder()
                .path("/string")
                .build()
                .evaluate(context));
        Assert.assertTrue(NotExistsExpression.builder()
                .path("/kid")
                .build()
                .evaluate(context));
        Assert.assertTrue(NotExistsExpression.builder()
                .path("/efgh")
                .build()
                .evaluate(context));
    }

    @Test
    public void testNumericExpression() {
        Assert.assertTrue(GreaterThanExpression.builder()
                .path("/value")
                .value(5)
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("/value")
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("/value")
                .value(30)
                .build()
                .evaluate(context));
    }

    @Test
    public void testAndExpression() {
        Assert.assertTrue(
                AndExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));
        Assert.assertTrue(
                AndExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));
    }

    @Test
    public void testOrExpression() {
        Assert.assertTrue(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(20)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));

        Assert.assertTrue(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(21)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .build()
                        .evaluate(context));
        Assert.assertFalse(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("/value")
                                .value(11)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("/value")
                                .value(30)
                                .build())
                        .build()
                        .evaluate(context));
    }

    @Test
    public void testNotExpression() throws Exception {
        Assert.assertTrue(
                NotExpression.builder()
                        .child(
                                OrExpression.builder()
                                        .child(LessThanExpression.builder()
                                                .path("/value")
                                                .value(11)
                                                .build())
                                        .child(GreaterThanExpression.builder()
                                                .path("/value")
                                                .value(30)
                                                .build())
                                        .build())
                        .build()
                        .evaluate(context));

    }
}