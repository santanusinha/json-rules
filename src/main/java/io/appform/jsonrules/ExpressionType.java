package io.appform.jsonrules;

/**
 * Types of expression
 */
public enum ExpressionType {

    //Equality
    equals,
    not_equals,
    in,
    not_in,

    //Numeric
    greater_than,
    less_than,
    greater_than_equals,
    less_than_equals,

    //Joiner
    and,
    or,
    not
}
