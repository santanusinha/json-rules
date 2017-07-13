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
import io.appform.jsonrules.expressions.equality.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExistensialExpressionTest {

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
        String NON_EXISTENT_PATH="/value_not_exisiting";
        Assert.assertTrue(ExistentialEqualsExpression.builder()
                .path(NON_EXISTENT_PATH)
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path(NON_EXISTENT_PATH)
                .value(20)
                .build()
                .evaluate(context));
    }

    @Test
    public void testInExpression() throws Exception {
        String NON_EXISTENT_PATH="/value_not_exisiting";
        Assert.assertFalse(InExpression.builder()
                .path(NON_EXISTENT_PATH)
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));

        Assert.assertTrue(ExistentialInExpression.builder()
                .path(NON_EXISTENT_PATH)
                .value("Hello")
                .value("World")
                .build()
                .evaluate(context));
    }

}