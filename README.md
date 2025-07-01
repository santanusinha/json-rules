# Json Rules [![Build Status](https://travis-ci.org/santanusinha/json-rules.svg?branch=master)](https://travis-ci.org/santanusinha/json-rules)

JSON serializable rules to match Jackson JsonNodes with JSONPath expressions.

###### Rule
```json
   {
      "type": "equals",
      "value": "happy",
      "path": "$.mood"
   }
```

###### Payload
```json
   {
      "name": "John Doe",
      "mood": "happy"
   }
```

Payload would match the rules when evaluated


## Getting Started
### Installation

Maven repo
```
  <dependency>
    <groupId>io.appform.rules</groupId>
    <artifactId>json-rules-core</artifactId>
    <version>LATEST</version>
  </dependency>
```

### Usage
```java
    // Build expression with java objects
    Expression expression = LessThanExpression.builder()
                                .path("$.value")
                                .value(30)
                                .build();
    // Or read from serialized json sources
    Expression expression = (new ObjectMapper()).readValue(expressionJson, Expression.class)
    
    // Get json payload to be evaluated
    JsonNode jsonNode = objectMapper.readTree(productJson);
    
    boolean matches = expression.evaluate(jsonNode);
```
##### Operators


###### General

 * equals
 * not_equals
 * less_than 
 * greater_than
 * less_than_equals
 * greater_than_equals
 * between (half-closed with lower bound included)

```json
   {
      "type": "equals",
      "value": "happy",
      "path": "$.mood"
   }
```

###### Composite/Boolean operators
 * and
 * not
 * or
```json
   {
      "type": "and",
      "children": [
          {
             "type": "equals",
             "value": "happy",
             "path": "$.mood"
          },
          {
             "type": "less_than",
             "value": 1000,
             "path": "$.product.cost"
          }
      ]
   }
```
###### Collection Search

 * not_in
 * in
 * contains_any
 * contains_all

```json
   {
      "type": "in",
      "path": "$.mood",
      "values": [
        "happy",
        "sad"
      ]
   }
```

###### Strings
 * empty
 * not_empty
 * starts_with
 * ends_with
 * matches

The string operations of `starts_with`, `ends_with` and `matches` support case insensitive comparison also. Default comparison is case sensitive.

```json
    {
        "type": "matches",
        "path": "$.s1",
        "value": ".* WORLD",
        "ignoreCase" : true
    }
```

###### Path validations
 * exists
 * not_exists
 

##### Default results

For unstructured json evaluation you can specify a defaultResult value.
The default value would be the evaluation result if `path` doesn't exist in the evaluation payload.

```json
   {
      "type": "equals",
      "value": "happy",
      "path": "$.mood",
      "defaultResult": true
   }
```

##### Pre-Operations

Pre-operations are pre-evaluation mutations that can be applied to payload.
 
 * Datetime
     * Epoch - Mutation rules for unix timestamp
     * DateTime - Mutation rules for textual dates
 * Numeric
     * Divide
     * Multiply
     * Sum
     * Difference
     * modulo
 * Array
     * size
 * String
     * length
     * sub_str

```json
    {
        "type": "in",
        "path": "$.time",
        "preoperation": {
          "operation": "epoch",
          "operand": "week_of_month",
          "zoneOffSet": "+05:30"
        },
        "values": [
          2,
          4
        ]
    }
```
  
##### Path based comparisons

These allow comparison of dynamic values. Using `"extractValueFromPath" : true`, indicates the value to be used for comparison has to be extracted from `value` json path.

``` json
    {
        "type": "matches",
        "path": "$.s1",
        "value": "$.s2",
        "extractValueFromPath" : true
    }
```

### Debugging

Debugging support is provided to understand exact reasons of rule failures for any given context. This support is extended across all the available operators.

## Advanced Configurations

### Performance <> Safety Preference
There is a performanceSafetyPreference option that can be set to either SPEED or SAFETY depending upon your needs. <br>
If your application doesn't use an infinite set of json paths, it is recommended to set this option to SPEED. <br>
If on the other hand, your application uses an infinite or unbounded number of json paths (at least 1 million or more), then to prevent json-rules 
from using more than a finite amount of heap memory for caching the json path expressions, you may want to set this option to SAFETY. <br>
It can be set to SPEED as follows

```java
JsonRulesConfiguration.configure(PerformanceSafetyPreference.SPEED);
```

### Support for complex JSONPath expressions
Filter expressions and UDF invocations are supported for JSONPath expressions. This allows for more complex evaluations and transformations on the JSON data.
For more details on which UDFs are supported, please refer to the [UDFs documentation](https://github.com/json-path/JsonPath?tab=readme-ov-file#functions)
For examples, you can refer to this [test class](./src/test/java/io/appform/rules/jsonpath/JsonPathComplexExpressionsTest.java).

To enable this feature, you need to set the `enableComplexJsonPathExpressions` flag in the configuration as follows:<br>
```java
JsonRulesConfiguration.enableComplexJsonPathExpressions(true);
```



