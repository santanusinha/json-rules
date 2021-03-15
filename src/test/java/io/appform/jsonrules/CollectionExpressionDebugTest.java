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

import io.appform.jsonrules.expressions.array.ContainsAllExpression;
import io.appform.jsonrules.expressions.array.ContainsAnyExpression;
import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.debug.DenialDetail;

public class CollectionExpressionDebugTest {

    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(
                "{ \"felines\": [\"leopard\",\"lion\",\"tiger\",\"jaguar\"],\"integers\": [10,20,30,40],\"decimals\": [10.01,20.22,30.33,40.55], \"emptyString\" : \"\", \"s3\" : \"Hello.*\", \"s1\" : \"HelloAllHello\", \"s2\" : \"Hello\",\"string\" : \"Hello\", \"kid\": null, \"boolean\" : true }");
        context = ExpressionEvaluationContext.builder()
                .node(node)
                .build();
    }

    @Test
    public void testContainsAnyExpression() throws Exception {
        final ContainsAnyExpression positiveCase = ContainsAnyExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("leopard", "lion", "panther"))
                .defaultResult(false)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final ContainsAnyExpression negativeCase = ContainsAnyExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("panther"))
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("None of the values at path [$.felines] are among shortlisted values",
                debugNegative.getReason());
    }

    @Test
    public void testContainsAllExpression() throws Exception {
        final ContainsAllExpression positiveCase = ContainsAllExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("tiger", "leopard", "lion", "jaguar"))
                .defaultResult(false)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final ContainsAllExpression negativeCase = ContainsAllExpression.builder()
                .path("$.felines")
                .values(Sets.newHashSet("tiger", "leopard", "lion", "panther"))
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Not all of the values at path [$.felines] are among shortlisted values",
                debugNegative.getReason());
    }

    @Test
    public void testInExpression() throws Exception {
        final InExpression positiveCase = InExpression.builder()
                .path("$.felines[0]")
                .valuesPath("$.felines")
                .extractValues(true)
                .defaultResult(false)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final InExpression negativeCase = InExpression.builder()
                .path("$.felines[0]")
                .valuesPath("$.integers")
                .extractValues(true)
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [leopard] at path [$.felines[0]] is not among shorlisted values",
                debugNegative.getReason());
    }

    @Test
    public void testNotInExpression() throws Exception {
        final NotInExpression positiveCase = NotInExpression.builder()
                .path("$.felines[0]")
                .valuesPath("$.integers")
                .extractValues(true)
                .defaultResult(false)
                .build();
        Assert.assertTrue(positiveCase.evaluate(context));
        final DenialDetail debugPositive = positiveCase.debug(context.getNode());
        Assert.assertFalse(debugPositive.isDenied());
        Assert.assertTrue(Objects.isNull(debugPositive.getReason()));

        final NotInExpression negativeCase = NotInExpression.builder()
                .path("$.felines[0]")
                .valuesPath("$.felines")
                .extractValues(true)
                .defaultResult(false)
                .build();
        Assert.assertFalse(negativeCase.evaluate(context));
        final DenialDetail debugNegative = negativeCase.debug(context.getNode());
        Assert.assertTrue(debugNegative.isDenied());
        Assert.assertEquals("Value of [leopard] at path [$.felines[0]] is among shortlisted values",
                debugNegative.getReason());
    }

}
