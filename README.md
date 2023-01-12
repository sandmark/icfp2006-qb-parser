# QBasic parser

Qvick Basic parser for ICFP2006 using [instaparse](https://github.com/Engelberg/instaparse), supports:

- Parse code and interconverts Roman numerals and decimal numbers
- Transpile Common Lisp's `tagbody` expressions to Qvick Basic

## Installation

Clone this repository (you'll need Leiningen to launch).

## Usage

Inside repo dir, type:

    lein run <-c|-r|-d> <file>

or you can build uberjar with `lein uberjar` and run `java -jar ...` if necessary.

## Options

The results of each option will be displayed on stdout.

- `-c` or `--compile`
  Compile Common Lisp's `tagbody` to Qvick Basic (with decimal numbers).
  See `dev/resources/qbasic.lisp` for more info.
  NOTE: This does NOT support linking `GOTO` and `LABEL`... yet.
- `-d` or `--decimalize`
  Parse an original Qvick Basic code and convert Roman numerals to Decimal numbers.
- `-r` or `--romanize`
  Parse a decimalized Qvick Basic code and convert numbers to Roman numerals.

## Examples

- `dev/resources/qbasic_roman.bas`
  An original QB code, for option `-d`.
- `dev/resources/qbasic_decimal.bas`
  A decimalized QB code, and tweaked to work well on UM-IX.
- `dev/resources/qbasic.lisp`
  A mock-up of Qvick Basic that works as-is with Common Lisp implementations (tested on SBCL).
  This can be compiled into Qvick Basic by passing the `-c` option.

## License

Copyright © 2023 sandmark

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
