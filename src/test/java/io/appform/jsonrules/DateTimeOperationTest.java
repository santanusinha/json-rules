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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import io.appform.jsonrules.expressions.array.InExpression;
import io.appform.jsonrules.expressions.array.NotInExpression;
import io.appform.jsonrules.expressions.composite.NotExpression;
import io.appform.jsonrules.expressions.composite.OrExpression;
import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.expressions.preoperation.date.DateTimeOperation;
import io.appform.jsonrules.utils.Rule;
import io.appform.jsonrules.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class DateTimeOperationTest {


    private ExpressionEvaluationContext context;
    private ObjectMapper mapper;
    private Instant dateTime;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        dateTime = Instant.now();
        long epoch = dateTime.getEpochSecond();
        String dateTimeStr = new StringBuilder().append("\"").append(dateTime.toString()).append("\"").toString();
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\", \"kid\": null, \"epochTime\" : "+epoch+", \"dateTime\" : "+dateTimeStr+" }");
        context = ExpressionEvaluationContext.builder().node(node).build();
    }

    @Test
	public void testWithEqualsExpression() {
		// DateTimeOperation test cases
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertFalse(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        try {
        	EqualsExpression.builder()
	            .path("$.epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
	}

    @Test
	public void testWithNotEqualsExpression() {
		// DateTimeOperation test cases
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        try {
        	NotEqualsExpression.builder()
	            .path("$.epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
	}

    @Test
    public void testWithInExpression() throws Exception {

		// DateTimeOperation test cases
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR)))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertFalse(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR)))
                .build()
                .evaluate(context));
        Assert.assertFalse(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY)))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR)))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY)))
                .build()
                .evaluate(context));
        
        try {
        	InExpression.builder()
	            .path("$.epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR)))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
    }
    
    @Test
    public void testWithNotInExpression() throws Exception {

		// DateTimeOperation test cases
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR)))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertTrue(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR)))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY)))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR)))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY)))
                .build()
                .evaluate(context));
        
        try {
        	NotInExpression.builder()
	            .path("$.epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .values(Sets.newHashSet(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR)))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
    }
    
    @Test
    public void testRule() throws Exception {
        final String ruleRepr = TestUtils.read("/dateTimeOperation.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"dateTime\": \"2017-05-31T11:09:37Z\", \"string\" : \"Hello\" }");
        Assert.assertTrue(rule.matches(node));
    }
    
    @Test
    public void testRepresentation() throws Exception {
        Rule rule = new Rule(NotExpression.builder()
                .child(
                        OrExpression.builder()
                                .child(LessThanExpression.builder()
                                        .path("$.unixTime")
                                        .value(11)
                                        .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                                        .build())
                                .child(GreaterThanExpression.builder()
                                        .path("$.unixTime")
                                        .value(30)
                                        .preoperation(DateTimeOperation.builder().operand("week_of_month").build())
                                        .build())
                                .build())
                .build());

        final String ruleRep = rule.representation(mapper);

        System.out.println(ruleRep);
        Assert.assertEquals("{\"type\":\"not\",\"children\":[{\"type\":\"or\",\"children\":[{\"type\":\"less_than\",\"path\":\"$.unixTime\",\"preoperation\":{\"operation\":\"date_time\",\"operand\":\"hour_of_day\"},\"defaultResult\":false,\"value\":11,\"extractValueFromPath\":false},{\"type\":\"greater_than\",\"path\":\"$.unixTime\",\"preoperation\":{\"operation\":\"date_time\",\"operand\":\"week_of_month\"},\"defaultResult\":false,\"value\":30,\"extractValueFromPath\":false}]}]}", ruleRep);
    }
    @Test
    public void testGreaterThanExpressionOnDateColumn() throws Exception {
        String dateTime = "2022-03-17 13:01:36.857";
        Expression expression = GreaterThanExpression.builder()
                .path("$.dateTime")
                .preoperation(DateTimeOperation.builder()
                        .operand("year")
                        .pattern("yyyy-MM-dd HH:mm:ss.SSS")
                        .zoneOffSet("+05:30")
                        .build())
                .value(2021).build();
        long epoch = Instant.now().getEpochSecond();
        String dateTimeStr = new StringBuilder().append("\"").append(dateTime).append("\"").toString();
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\", \"kid\": null, \"epochTime\" : "+epoch+", \"dateTime\" : "+dateTimeStr+" }");
        context.setNode(node);
        Assert.assertTrue(expression.evaluate(context.getNode()));
    }
}
