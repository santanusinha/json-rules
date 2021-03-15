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
import com.google.common.collect.Sets;

import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.debug.DenialDetail;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.meta.ExistsExpression;
import io.appform.jsonrules.expressions.meta.NotExistsExpression;
import io.appform.jsonrules.expressions.numeric.BetweenExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;

public class NumericalExpressionDebugTest {

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
    public void testEqualsExpression() throws Exception {
        final EqualsExpression positiveCase = EqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final EqualsExpression negativeCase = EqualsExpression.builder()
                .path("$.abcd")
                .value(20)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [null] at path [$.abcd] is not equals to [20]", debugNegative.getReason());
    }

    @Test
    public void testNotEqualsExpression() throws Exception {
        final NotEqualsExpression positiveCase = NotEqualsExpression.builder()
                .path("$.value")
                .value(10)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final NotEqualsExpression negativeCase = NotEqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [20] at path [$.value] is equal to [20]", debugNegative.getReason());
    }

    @Test
    public void testInExpression() throws Exception {
        final InExpression positiveCase = InExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("Hello", "World"))
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final InExpression negativeCase = InExpression.builder()
                .path("$.kid")
                .values(Sets.newHashSet("Hello", "World"))
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [null] at path [$.kid] is not among shorlisted values",
                debugNegative.getReason());

    }

    @Test
    public void testNotInExpression() throws Exception {
        final NotInExpression positiveCase = NotInExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("hello", "world"))
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final NotInExpression negativeCase = NotInExpression.builder()
                .path("$.string")
                .values(Sets.newHashSet("Hello", "World"))
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [Hello] at path [$.string] is among shortlisted values",
                debugNegative.getReason());
    }

    @Test
    public void testExistsExpression() throws Exception {
        final ExistsExpression positiveCase = ExistsExpression.builder()
                .path("$.string")
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final ExistsExpression negativeCase = ExistsExpression.builder()
                .path("$.somepath")
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Path [$.somepath] doesn't exist", debugNegative.getReason());
    }

    @Test
    public void testNotExistsExpression() throws Exception {
        final NotExistsExpression positiveCase = NotExistsExpression.builder()
                .path("$.somepath")
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final NotExistsExpression negativeCase = NotExistsExpression.builder()
                .path("$.string")
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Path [$.string] exists", debugNegative.getReason());
    }

    @Test
    public void testGreaterThanExpression() throws Exception {
        final GreaterThanExpression positiveCase = GreaterThanExpression.builder()
                .path("$.value")
                .value(5)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final GreaterThanExpression negativeCase = GreaterThanExpression.builder()
                .path("$.value")
                .value(50)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [20] at path [$.value] is not greater than [50]", debugNegative.getReason());
    }

    @Test
    public void testGreaterThanEqualsExpression() throws Exception {
        final GreaterThanEqualsExpression positiveCase = GreaterThanEqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final GreaterThanEqualsExpression negativeCase = GreaterThanEqualsExpression.builder()
                .path("$.value")
                .value(21)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [20] at path [$.value] is less than [21]", debugNegative.getReason());
    }

    @Test
    public void testLessThanExpression() throws Exception {
        final LessThanExpression positiveCase = LessThanExpression.builder()
                .path("$.value")
                .value(30)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final LessThanExpression negativeCase = LessThanExpression.builder()
                .path("$.value")
                .value(10)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [20] at path [$.value] is not less than [10]", debugNegative.getReason());
    }

    @Test
    public void testLessThanEqualsExpression() throws Exception {
        final LessThanEqualsExpression positiveCase = LessThanEqualsExpression.builder()
                .path("$.value")
                .value(20)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final LessThanEqualsExpression negativeCase = LessThanEqualsExpression.builder()
                .path("$.value")
                .value(19)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [20] at path [$.value] is greater than [19]", debugNegative.getReason());
    }

    @Test
    public void testBetweenExpression() throws Exception {
        final BetweenExpression positiveCase = BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(10)
                .upperBound(30)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final BetweenExpression negativeCase = BetweenExpression.builder()
                .path("$.v1")
                .lowerbound(10)
                .upperBound(19)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [20] at path [$.v1] is not between [10] & [19]", debugNegative.getReason());
    }

}
