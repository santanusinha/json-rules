/**
 * Copyright (c) 2021 Mohammed Irfanulla S <mohammed.irfanulla.s1@gmail.com>
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

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appform.jsonrules.expressions.composite.AndExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.debug.DenialDetail;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;

public class CompositeExpressionDebugTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(
                "{ \"v1\": 20, \"v2\": 30, \"v3\": 20.001, \"value\": 20, \"string\" : \"Hello\", \"string1\" : \"Hello1\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder()
                .node(node)
                .build();
    }

    @Test
    public void testAndExpression() {
        final AndExpression positiveCase = AndExpression.builder()
                .child(LessThanExpression.builder()
                        .path("$.value")
                        .value(30)
                        .build())
                .child(GreaterThanExpression.builder()
                        .path("$.value")
                        .value(10)
                        .build())
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final AndExpression negativeCase = AndExpression.builder()
                .child(LessThanExpression.builder()
                        .path("$.value")
                        .value(5)
                        .build())
                .child(GreaterThanExpression.builder()
                        .path("$.value")
                        .value(10)
                        .build())
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("{\nValue of [20] at path [$.value] is not less than [5]\n}", debugNegative.getReason());
    }

    @Test
    public void testOrExpression() {
        final OrExpression positiveCase = OrExpression.builder()
                .child(LessThanExpression.builder()
                        .path("$.value")
                        .value(20)
                        .build())
                .child(GreaterThanExpression.builder()
                        .path("$.value")
                        .value(10)
                        .build())
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final OrExpression negativeCase = OrExpression.builder()
                .child(LessThanExpression.builder()
                        .path("$.value")
                        .value(11)
                        .build())
                .child(GreaterThanExpression.builder()
                        .path("$.value")
                        .value(30)
                        .build())
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals(
                "{\nValue of [20] at path [$.value] is not less than [11]\n"
                        + "Value of [20] at path [$.value] is not greater than [30]\n" + "}",
                debugNegative.getReason());

    }

    @Test
    public void testNotExpression() throws Exception {
        final NotExpression positiveCase = NotExpression.builder()
                .child(OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(11)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(19)
                                .build())
                        .build())
                .build();
        Assert.assertFalse(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertTrue(debugPositive.isDenied());
        Assert.assertEquals("{\n\n}", debugPositive.getReason());

        final NotExpression negativeCase = NotExpression.builder()
                .child(OrExpression.builder()
                        .child(LessThanExpression.builder()
                                .path("$.value")
                                .value(11)
                                .build())
                        .child(GreaterThanExpression.builder()
                                .path("$.value")
                                .value(21)
                                .build())
                        .build())
                .build();
        Assert.assertTrue(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertFalse(debugNegative.isDenied());
        Assert.assertTrue(Objects.isNull(debugNegative.getReason()));
    }

}
