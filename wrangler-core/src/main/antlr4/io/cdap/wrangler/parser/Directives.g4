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
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

grammar Directives;

options {
  language = Java;
}

@lexer::header {
  // Licensing header preserved
}

// === PARSER RULES ===

recipe
  : statements EOF
  ;

statements
  : (Comment | macro | directive SColon | pragma SColon | ifStatement)*
  ;

directive
  : command (

      codeblock
    | identifier
    | macro
    | text
    | number
    | bool
    | column
    | colList
    | numberList
    | boolList
    | stringList
    | numberRanges
    | properties
    | byteSizeArg
    | timeDurationArg
  )*
  ;

ifStatement
  : 'if' expression '{' statements ( '}' 'else' 'if' expression '{' statements )* ( '}' 'else' '{' statements )? '}'
  ;

expression
  : '(' (~')')* ')'
  ;

macro
  : Dollar OBrace (~'}')* CBrace
  ;

pragma
  : '#pragma' (pragmaLoadDirective | pragmaVersion)
  ;

pragmaLoadDirective
  : 'load-directives' identifierList
  ;

pragmaVersion
  : 'version' Number
  ;

codeblock
  : 'exp' Space* ':' condition
  ;

condition
  : OBrace (~CBrace | condition)* CBrace
  ;

identifier
  : Identifier
  ;

properties
  : 'prop' ':' OBrace propertyList CBrace
  ;

propertyList
  : property (',' property)*
  ;

property
  : Identifier '=' (text | number | bool)
  ;

numberRanges
  : numberRange (',' numberRange)*
  ;

numberRange
  : Number ':' Number '=' value
  ;

value
  : String | Number | Column | Bool | BYTE_SIZE | TIME_DURATION
  ;

byteSizeArg
  : BYTE_SIZE
  ;

timeDurationArg
  : TIME_DURATION
  ;

column
  : Column
  ;

text
  : String
  ;

number
  : Number
  ;

bool
  : Bool
  ;

colList
  : Column (',' Column)+
  ;

numberList
  : Number (',' Number)+
  ;

boolList
  : Bool (',' Bool)+
  ;

stringList
  : String (',' String)+
  ;

identifierList
  : Identifier (',' Identifier)*
  ;

command
  : Identifier
  ;

// === LEXER RULES ===

OBrace   : '{';
CBrace   : '}';
SColon   : ';';
Dollar   : '$';
Colon    : ':';
Comma    : ',';

Bool
  : 'true'
  | 'false'
  ;

BYTE_SIZE
  : Int ('.' Digit*)? BYTE_UNIT
  ;

TIME_DURATION
  : Int ('.' Digit*)? TIME_UNIT
  ;

fragment BYTE_UNIT
  : 'B' | 'KB' | 'MB' | 'GB' | 'TB' | 'kb' | 'mb' | 'gb' | 'tb'
  ;

fragment TIME_UNIT
  : 'ms' | 's' | 'sec' | 'seconds' | 'm' | 'min' | 'minutes'
  ;

Number
  : Int ('.' Digit*)?
  ;

Identifier
  : [a-zA-Z_\-] [a-zA-Z_0-9\-]*
  ;

Macro
  : [a-zA-Z_] [a-zA-Z_0-9]*
  ;

Column
  : ':' [a-zA-Z_\-] [:a-zA-Z_0-9\-]*
  ;

String
  : '\'' (EscapeSequence | ~'\'')* '\''
  | '"'  (EscapeSequence | ~'"')* '"'
  ;

fragment EscapeSequence
  : '\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')
  | UnicodeEscape
  | OctalEscape
  ;

fragment OctalEscape
  : '\\' [0-3] [0-7] [0-7]
  | '\\' [0-7] [0-7]
  | '\\' [0-7]
  ;

fragment UnicodeEscape
  : '\\' 'u' HexDigit HexDigit HexDigit HexDigit
  ;

fragment HexDigit
  : [0-9a-fA-F]
  ;

Comment
  : ('//' ~[\r\n]* | '/*' .*? '*/' | '--' ~[\r\n]*) -> skip
  ;

Space
  : [ \t\r\n\u000C]+ -> skip
  ;

fragment Int
  : '-'? [1-9] Digit* [L]?
  | '0'
  ;

fragment Digit
  : [0-9]
  ;
