(ns qbasic-parser.ebnf
  (:require [mount.core :as m]
            [clojure.string :as str]
            [qbasic-parser.roman :as roman]))

(m/defstate exp
  :start (let [rule "exp = value
                         | (value space op space value)
                         | exp space op space value
                     op = ('+' | '/' | '-' | '*' | '<' | '>')
                     variable = var-char+ | var-char+ index
                     index = slicer (number | variable) slicer
                     slicer = '(' | ')'
                     <var-char> = #'[a-z]'
                     <space> = whitespace+
                     <whitespace> = ' '
                     value = (variable | literal | funcall)
                     literal = (string | number)
                     string  = quote character* quote
                     <character> = #'[/.a-zA-Z0-9\\s!:()\\\\]'
                     <quote> = '\"'
                     funcall = funname args
                     funname = #'[A-Z]'+
                     args = arg-begin arg? (delimiter arg)* arg-end
                     arg-begin = '('
                     arg-end   = ')'
                     delimiter = ',' space?
                     number = digit | roman
                     <digit> = #'[0-9]'+
                     arg = exp"]
           (str/join "\n" [rule roman/ebnf-roman])))

(m/defstate assign
  :start (let [rule "assign = variable space '=' space exp"]
           (str/join "\n" [rule exp])))

(m/defstate statement
  :start (let [rule "statement = rem | if | goto | print | end | dim
                     end = 'END'
                     rem = 'REM' | 'REM' space+ comment+
                     <comment> = #'.'
                     if = 'IF' space exp space 'THEN' space goto
                     goto = 'GOTO' space number
                     print = 'PRINT' space exp
                     dim = 'DIM' space variable space 'AS' space type
                     type = 'INTEGER' | 'STRING'"]
           (str/join "\n" [rule assign])))

(m/defstate line
  :start (let [rule "S = (number space line eol)*
                     line = exp | statement | assign
                     eol = '\n'"]
           (str/join "\n" [rule statement])))
