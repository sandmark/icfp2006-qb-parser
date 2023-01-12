(ns qbasic-parser.compiler-test
  (:require [qbasic-parser.compiler :as sut]
            [clojure.test :as t]))

(t/deftest compiler-test
  (t/testing "Get expressions after START"
    (let [s      '(IGNORE (+ 1 1) START (exp 1 2) (exp 3 4))
          expect '(START (exp 1 2) (exp 3 4))]
      (t/is (= expect (sut/find-body s)))))

  (t/testing "Emit"
    (t/testing "Label"
      (t/is (= ["REM LABEL: SYMBOL"] (sut/emit 'SYMBOL))))
    (t/testing "DIM"
      (t/testing "INTEGER"
        (let [expr   '(defparameter zero 48)
              expect ["DIM zero AS INTEGER" "zero = 48"]]
          (t/is (= expect (sut/emit expr)))))
      (t/testing "STRING"
        (let [expr   '(defparameter pass "")
              expect ["DIM pass AS STRING" "pass = \"\""]]
          (t/is (= expect (sut/emit expr))))))
    (t/testing "Array Index"
      (let [expr   '(at i words)
            expect ["words(i)"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "CHR"
      (let [expr   '(chr one)
            expect ["CHR(one)"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "Concat"
      (let [expr   '(concat (at i words) (chr two))
            expect ["words(i) + CHR(two)"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "Assignment"
      (let [expr   '(setf pass (concat (at i words) (chr two)))
            expect ["pass = words(i) + CHR(two)"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "PRINT"
      (let [expr   '(qprint username "@" pass (chr 10))
            expect ["PRINT username + \"@\" + pass + CHR(10)"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "CHECKPASS"
      (let [expr   '(checkpass username pass)
            expect ["CHECKPASS(username, pass)"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "GOTO"
      (let [expr   '(go SUCCESS)
            expect ["GOTO SUCCESS"]]
        (t/is (= expect (sut/emit expr)))))
    (t/testing "IF"
      (t/testing "FUNCALL"
        (let [expr   '(if (checkpass username pass) (go SUCCESS))
              expect ["IF CHECKPASS(username, pass) THEN GOTO SUCCESS"]]
          (t/is (= expect (sut/emit expr)))))
      (t/testing ">"
        (let [expr   '(if (> one nine) (go INC-TWO))
              expect ["IF one > nine THEN GOTO INC-TWO"]]
          (t/is (= expect (sut/emit expr)))))
      (t/testing "<"
        (let [expr   '(< 1 2)
              expect ["1 < 2"]]
          (t/is (= expect (sut/emit expr))))))
    (t/testing "INCF"
      (let [expr   '(incf one)
            expect ["one = one + 1"]]
        (t/is (= expect (sut/emit expr)))))))
