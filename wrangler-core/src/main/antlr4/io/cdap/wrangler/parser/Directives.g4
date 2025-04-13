/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

grammar Directives;

// Entry point of the parser
parse
  : statement+ EOF
  ;

// Statement can be a directive, an if statement, or a pragma directive
statement
  : directive ';'                    // Directives followed by semicolons
  | ifStatement                       // If statements
  | pragmaDirective                   // Pragma directives
  ;

// A pragma directive definition (e.g., #pragma)
pragmaDirective
  : PRAGMA ID (',' ID)* ';'
  ;

// A general directive (e.g., 'load-directives', 'parse-as-bytes', etc.)
directive
  : ID (expr (',' expr)*)?            // Directive name followed by optional expressions
  ;

// If statement handling
ifStatement
  : 'if' '(' expr ')' '{' statement+ '}'
  ;

// Expression handling (various possible values)
expr
  : ID                               // Variable or identifier
  | STRING                           // String literal
  | NUMBER                           // Number literal
  | BYTE_SIZE                        // Byte size literal
  | TIME_DURATION                    // Time duration literal
  | macro                            // Macro expansion
  | predicate                        // Predicate expression
  | expr binOp expr                  // Binary expressions (e.g., comparisons)
  | '(' expr ')'                     // Parenthesized expressions
  ;

// Predicate expression (conditional logic)
predicate
  : 'exp' ':' '{' expr '}'
  ;

// Macro expansion (e.g., ${var_name})
macro
  : '${' ID ('_' ID)? '}'
  ;

// Binary operators for expressions
binOp
  : '&&'
  | '||'
  | '=='
  | '!='
  | '<'
  | '<='
  | '>'
  | '>='
  | '=~'
  ;

// Token Definitions
ID: ':'? [a-zA-Z_][a-zA-Z_0-9]* ;

NUMBER: [0-9]+ ('.' [0-9]+)? ;

BYTE_SIZE: [0-9]+ ('.' [0-9]+)? BYTE_UNIT ;

TIME_DURATION: [0-9]+ ('.' [0-9]+)? TIME_UNIT ;

// Byte unit options (e.g., kB, MB, GB, b)
fragment BYTE_UNIT: [kK][bB] | [mM][bB] | [gG][bB] | [bB] ;

// Time unit options (e.g., ms, sec, min, etc.)
fragment TIME_UNIT: 'ms' | 's' | 'sec' | 'm' | 'min' | 'h' | 'hr' | 'd' | 'day' ;

// Pragmas start with '#pragma'
PRAGMA: '#pragma' ;

// Strings enclosed in single quotes
STRING: '\'' (~['\\] | '\\' .)* '\'' ;

// Single-line comments (ignored by parser)
COMMENT: '#' ~[\r\n]* -> skip ;

// Whitespace (ignored by parser)
WS: [ \t\r\n]+ -> skip ;

