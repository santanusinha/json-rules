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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appform.jsonrules.expressions.equality.EqualsExpression;
import io.appform.jsonrules.expressions.equality.InExpression;
import io.appform.jsonrules.expressions.equality.NotEqualsExpression;
import io.appform.jsonrules.expressions.equality.NotInExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.GreaterThanExpression;
import io.appform.jsonrules.expressions.numeric.LessThanEqualsExpression;
import io.appform.jsonrules.expressions.numeric.LessThanExpression;
import io.appform.jsonrules.expressions.preoperation.date.DateTimeOperation;
import io.appform.jsonrules.expressions.preoperation.date.EpochOperation;
import io.appform.jsonrules.expressions.preoperation.numeric.AddOperation;
import io.appform.jsonrules.expressions.preoperation.numeric.DivideOperation;
import io.appform.jsonrules.expressions.preoperation.numeric.ModuloOperation;
import io.appform.jsonrules.expressions.preoperation.numeric.MultiplyOperation;
import io.appform.jsonrules.expressions.preoperation.numeric.SubtractOperation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class PreOperationTest {

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
    public void testEqualsExpressionWithNumericPreOperations() throws Exception {
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(-2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(-2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(2).build())
                .value(40)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(-2).build())
                .value(-40)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(0).build())
                .value(0)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(2).build())
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(-2).build())
                .value(-10)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(EqualsExpression.builder()
        			.path("/value")
        			.preoperation(DivideOperation.builder().operand(0).build())
        			.value(0)
        			.build()
        			.evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(EqualsExpression.builder()
                    .path("/value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	EqualsExpression.builder()
            .path("/abcd")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	EqualsExpression.builder()
            .path("/kid")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }

    @Test
	public void testEqualsExpressionWithCalendarPreOperation() {
		// DateTimeOperation test cases
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertFalse(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        try {
        	EqualsExpression.builder()
	            .path("/epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
        

		// EpochOperation
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertFalse(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(EqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        Assert.assertFalse(EqualsExpression.builder()
        		.path("/dateTime")
        		.preoperation(EpochOperation.builder().operand("minute_of_hour").build())
        		.value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
        		.build()
        		.evaluate(context));
	}

    @Test
    public void testNotEqualsExpressionWithNumericPreOperations() throws Exception {
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(-2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(-2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(2).build())
                .value(40)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(-2).build())
                .value(-40)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(0).build())
                .value(0)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(2).build())
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(-2).build())
                .value(-10)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(NotEqualsExpression.builder()
        			.path("/value")
        			.preoperation(DivideOperation.builder().operand(0).build())
        			.value(0)
        			.build()
        			.evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertFalse(NotEqualsExpression.builder()
                    .path("/value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	NotEqualsExpression.builder()
            .path("/abcd")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	NotEqualsExpression.builder()
            .path("/kid")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }

    @Test
	public void testNotEqualsExpressionWithCalendarPreOperation() {
		// DateTimeOperation test cases
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        try {
        	NotEqualsExpression.builder()
	            .path("/epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
        

		// EpochOperation
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotEqualsExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        Assert.assertTrue(NotEqualsExpression.builder()
        		.path("/dateTime")
        		.preoperation(EpochOperation.builder().operand("minute_of_hour").build())
        		.value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
        		.build()
        		.evaluate(context));
	}

    @Test
    public void testInExpressionWithNumericPreoperation() throws Exception {
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(-2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(-2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(2).build())
                .value(40)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(-2).build())
                .value(-40)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(0).build())
                .value(0)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(2).build())
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(-2).build())
                .value(-10)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(InExpression.builder()
        			.path("/value")
        			.preoperation(DivideOperation.builder().operand(0).build())
        			.value(0)
        			.build()
        			.evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(InExpression.builder()
                    .path("/value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	InExpression.builder()
            .path("/abcd")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	InExpression.builder()
            .path("/kid")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }
    
    @Test
    public void testInExpressionWithCalendarPreOperation() throws Exception {

		// DateTimeOperation test cases
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertFalse(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        try {
        	InExpression.builder()
	            .path("/epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
        

		// EpochOperation
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertFalse(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(InExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        Assert.assertFalse(InExpression.builder()
        		.path("/dateTime")
        		.preoperation(EpochOperation.builder().operand("minute_of_hour").build())
        		.value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
        		.build()
        		.evaluate(context));
    }
    
    @Test
    public void testNotInExpressionWithNumberPreOperation() throws Exception {

        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(-2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(2).build())
                .value(18)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(-2).build())
                .value(22)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(2).build())
                .value(40)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(-2).build())
                .value(-40)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(0).build())
                .value(0)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(2).build())
                .value(10)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(-2).build())
                .value(-10)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(NotInExpression.builder()
        			.path("/value")
        			.preoperation(DivideOperation.builder().operand(0).build())
        			.value(0)
        			.build()
        			.evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertFalse(NotInExpression.builder()
                    .path("/value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	NotInExpression.builder()
            .path("/abcd")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	NotInExpression.builder()
            .path("/kid")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }
    
    @Test
    public void testNotInExpressionWithCalendarPreOperation() throws Exception {

		// DateTimeOperation test cases
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertTrue(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/dateTime")
                .preoperation(DateTimeOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        try {
        	NotInExpression.builder()
	            .path("/epochTime")
	            .preoperation(DateTimeOperation.builder().operand("minute_of_hour").build())
	            .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
	            .build()
	            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Doesn't represent a valid date time string", true);
        }
        

		// EpochOperation
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_week").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_WEEK))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("day_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.DAY_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_month").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("week_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("month_of_year").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.of("+05:30")).get(ChronoField.MONTH_OF_YEAR))
                .build()
                .evaluate(context));
        
        // Would only match with the specified ZoneOffSet
        Assert.assertTrue(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertTrue(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").zoneOffSet("+05:30").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        // Default ZoneOffSet considered is UTC
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("minute_of_hour").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
                .build()
                .evaluate(context));
        Assert.assertFalse(NotInExpression.builder()
                .path("/epochTime")
                .preoperation(EpochOperation.builder().operand("hour_of_day").build())
                .value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY))
                .build()
                .evaluate(context));
        
        Assert.assertTrue(NotInExpression.builder()
        		.path("/dateTime")
        		.preoperation(EpochOperation.builder().operand("minute_of_hour").build())
        		.value(dateTime.atOffset(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR))
        		.build()
        		.evaluate(context));
    }
    
    @Test
    public void testNumbericExpressionWithNumericPreOperation() throws Exception {

        Assert.assertTrue(GreaterThanExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(2).build())
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("/value")
                .preoperation(AddOperation.builder().operand(-2).build())
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(2).build())
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanExpression.builder()
                .path("/value")
                .preoperation(SubtractOperation.builder().operand(-2).build())
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(2).build())
                .value(40)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(-2).build())
                .value(-40)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("/value")
                .preoperation(MultiplyOperation.builder().operand(0).build())
                .value(0)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(2).build())
                .value(20)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanExpression.builder()
                .path("/value")
                .preoperation(DivideOperation.builder().operand(-2).build())
                .value(20)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(GreaterThanExpression.builder()
        			.path("/value")
        			.preoperation(DivideOperation.builder().operand(0).build())
        			.value(0)
        			.build()
        			.evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        Assert.assertTrue(GreaterThanEqualsExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(3).build())
                .value(2)
                .build()
                .evaluate(context));
        Assert.assertTrue(LessThanEqualsExpression.builder()
                .path("/value")
                .preoperation(ModuloOperation.builder().operand(2).build())
                .value(0)
                .build()
                .evaluate(context));
        try {
        	Assert.assertTrue(GreaterThanExpression.builder()
                    .path("/value")
                    .preoperation(ModuloOperation.builder().operand(0).build())
                    .value(0)
                    .build()
                    .evaluate(context));
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Division by zero is not allowed", true);
        }
        
        try {
        	GreaterThanExpression.builder()
            .path("/abcd")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
        
        try {
        	GreaterThanExpression.builder()
            .path("/kid")
            .preoperation(AddOperation.builder().operand(2).build())
            .value(20)
            .build()
            .evaluate(context);
        	Assert.fail("Should have thrown an exception");
        } catch(IllegalArgumentException e) {
        	Assert.assertTrue("Object numeric operations are not supported", true);
        }
    }
    
}