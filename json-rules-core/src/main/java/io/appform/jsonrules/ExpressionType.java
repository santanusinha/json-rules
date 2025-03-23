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

/**
 * Types of expression
 */
public enum ExpressionType {

    //Equality
    equals,
    not_equals,

    //Numeric
    greater_than,
    less_than,
    greater_than_equals,
    less_than_equals,
    between,

    //String
    empty,
    not_empty,
    starts_with,
    ends_with,
    matches,

    //Meta
    exists,
    not_exists,

    //Joiner
    and,
    or,
    not,

    //Collection
    in,
    not_in,
    contains_any,
    contains_all
}
