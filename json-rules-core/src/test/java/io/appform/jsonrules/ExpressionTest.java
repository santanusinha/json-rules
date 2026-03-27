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
import com.google.common.collect.Sets;
import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.composite.AndExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.meta.ExistsExpression;
import io.appform.jsonrules.expressions.meta.NotExistsExpression;
import io.appform.jsonrules.expressions.numeric.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExpressionTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{ \"v1\": 20, \"v2\": 30, \"v3\": 20.001, \"value\": 20, \"string\" : \"Hello\", \"string1\" : \"Hello1\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }

    @Test
    public void testEqualsExpression() throws Exception {
        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.abcd")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.kid")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.value")
                .value(10)
                .build()
                .evaluate(context));
        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.string")
                .value("Hello")
                .build()
                .evaluate(context));
        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.string")
                .value("$.string")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.boolean")
                .value("$.boolean")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.kid") // nulls on both side are equal
                .value("$.kid")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.string")
                .value("hello")
                .build()
                .evaluate(context));
        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.boolean")
                .value(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.boolean")
                .value(false)
                .build()
                .evaluate(context));

        Assertions.assertTrue(EqualsExpression.builder()
                .path("$.v1")
                .value("$.value")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.v1")
                .value("$.string")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(EqualsExpression.builder()
                .path("$.v1")
                .value("$.v4") // path doesn't exist
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
    }

    @Test
    public void testNotEqualsExpression() throws Exception {
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.value")
                .value(10)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.kid")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.NON_EXISITING_KEY")
                .value(20)
                .defaultResult(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.NON_EXISITING_KEY")
                .value(20)
                .defaultResult(false)
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.string")
                .value("Hello")
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.string")
                .value("$.string")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.string")
                .value("$.string1")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.kid") // nulls on both sides are equal
                .value("$.kid")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.boolean")
                .value("$.boolean")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.string")
                .value("hello")
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotEqualsExpression.builder()
                .path("$.v1")
                .value("$.value")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.v1")
                .value("$.string")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotEqualsExpression.builder()
                .path("$.v1")
                .value("$.v4") // path doesn't exist
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
    }

    @Test
    public void testInExpression() throws Exception {
        Assertions.assertTrue(InExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("Hello","World"))
                .build()
                .evaluate(context));

        Assertions.assertFalse(InExpression.builder()
                .path("$.kid")
                .values(Sets.newHashSet("Hello","World"))
                .build()
                .evaluate(context));

        Assertions.assertFalse(InExpression.builder()
                .path("$.abcd")
                .values(Sets.newHashSet("Hello","World"))
                .build()
                .evaluate(context));

        Assertions.assertFalse(InExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("hello","world"))
                .build()
                .evaluate(context));
    }

    @Test
    public void testNotInExpression() throws Exception {
        Assertions.assertFalse(NotInExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("Hello","World"))
                .build()
                .evaluate(context));

        Assertions.assertTrue(NotInExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("hello","world"))
                .build()
                .evaluate(context));

        Assertions.assertTrue(NotInExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("abcd","efgh"))
                .build()
                .evaluate(context));

        Assertions.assertTrue(NotInExpression.builder()
                .path("$.kid")
                .values(Sets.newHashSet("stupid","dumb"))
                .build()
                .evaluate(context));

        Assertions.assertTrue(NotInExpression.builder()
                .path("$.NON_EXISITING_KEY")
                .values(Sets.newHashSet("stupid","dumb"))
                .build()
                .evaluate(context));

        Assertions.assertFalse(NotInExpression.builder()
                .path("$.NON_EXISITING_KEY")
                .defaultResult(false)
                .values(Sets.newHashSet("stupid","dumb"))
                .build()
                .evaluate(context));
    }

    @Test
    public void testExistsExpression() throws Exception {
        Assertions.assertFalse(ExistsExpression.builder()
                .path("$.somepath")
                .build()
                .evaluate(context));
        Assertions.assertTrue(ExistsExpression.builder()
                .path("$.string")
                .build()
                .evaluate(context));
        Assertions.assertFalse(ExistsExpression.builder()
                .path("$.kid")
                .build()
                .evaluate(context));
        Assertions.assertFalse(ExistsExpression.builder()
                .path("$.efgh")
                .build()
                .evaluate(context));
    }

    @Test
    public void testNotExistsExpression() throws Exception {
        Assertions.assertTrue(NotExistsExpression.builder()
                .path("$.somepath")
                .build()
                .evaluate(context));
        Assertions.assertFalse(NotExistsExpression.builder()
                .path("$.string")
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotExistsExpression.builder()
                .path("$.kid")
                .build()
                .evaluate(context));
        Assertions.assertTrue(NotExistsExpression.builder()
                .path("$.efgh")
                .build()
                .evaluate(context));
    }

    @Test
    public void testNumericExpression() {
        Assertions.assertTrue(GreaterThanExpression.builder()
                .path("$.value")
                .value(5)
                .build()
                .evaluate(context));
        Assertions.assertTrue(GreaterThanEqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertTrue(LessThanEqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build()
                .evaluate(context));
        Assertions.assertTrue(LessThanExpression.builder()
                .path("$.value")
                .value(30)
                .build()
                .evaluate(context));
        Assertions.assertTrue(GreaterThanExpression.builder()
                .path("$.v2")
                .value("$.v1")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(GreaterThanEqualsExpression.builder()
                .path("$.v2")
                .value("$.v1")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(LessThanEqualsExpression.builder()
                .path("$.v1")
                .value("$.v2")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(LessThanExpression.builder()
                .path("$.v1")
                .value("$.v2")
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
        Assertions.assertTrue(BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(10)
                .upperBound(30)
                .build()
                .evaluate(context));
        Assertions.assertTrue(BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(19) // lower bound excluded test; default behaviour
                .upperBound(30)
                .build()
                .evaluate(context));
        Assertions.assertFalse(BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(19)
                .upperBound(20) // upper bound excluded test; default behaviour
                .build()
                .evaluate(context));
        Assertions.assertTrue(BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(20) // override lower bound excluded test; default behaviour
                .includeLowerBound(true)
                .upperBound(30)
                .build()
                .evaluate(context));
        Assertions.assertTrue(BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(19)
                .upperBound(20) // upper bound excluded test; default behaviour
                .includeUpperBound(true)
                .build()
                .evaluate(context));

        Assertions.assertTrue(BetweenExpression.builder()
                .path("$.v3")
                .lowerbound(20.0001)
                .upperBound(20.01)
                .build()
                .evaluate(context));

        Assertions.assertFalse(LessThanExpression.builder()
                .path("$.value")
                .value("$.value1") // path doesn't exist
                .extractValueFromPath(true)
                .build()
                .evaluate(context));
    }

    @Test
    public void testAndExpression() {
        Assertions.assertTrue(
                AndExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(30)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));
        Assertions.assertTrue(
                AndExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(30)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));
    }

    @Test
    public void testOrExpression() {
        Assertions.assertTrue(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(20)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(10)
                                .build())
                        .build()
                        .evaluate(context));

        Assertions.assertTrue(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(21)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(30)
                                .build())
                        .build()
                        .evaluate(context));
        Assertions.assertFalse(
                OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(11)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(30)
                                .build())
                        .build()
                        .evaluate(context));
    }

    @Test
    public void testNotExpression() throws Exception {
        Assertions.assertTrue(
                NotExpression.builder()
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
                        .build()
                        .evaluate(context));

    }
}