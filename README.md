# Json Rules [![Build Status](https://travis-ci.org/santanusinha/json-rules.svg?branch=master)](https://travis-ci.org/santanusinha/json-rules)

JSON serializable rules to match Jackson JsonNodes using JSON Pointers.

###### Rule
```json
   {
      "type": "equals",
      "value": "happy",
      "path": "$.mood"
   }
```

###### Paylod
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
    <artifactId>json-rules</artifactId>
    <version>1.0.12</version>
  </dependency>
```

### Usage
```java
    // Build expression with java objects
    Expression expression = LessThanExpression.builder()
                                .path("/$.value")
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

##### Preoperations

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

### Debuggability

Debuggability support is provided to understand exact reasons of rule failures for any given context. This support is extended across all the available operators.



