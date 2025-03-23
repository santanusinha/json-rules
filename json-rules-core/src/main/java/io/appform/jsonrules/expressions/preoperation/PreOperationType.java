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

package io.appform.jsonrules.expressions.preoperation;

/**
 * Types of operations
 */
public enum PreOperationType {
	
	// Numeric
	add,
	subtract,
	multiply,
	divide,
	modulo,

	//collection
	size,

	//string
	length,
	sub_str,

	// Calendar
	epoch,
	date_time,
	current_epoch_diff
}
