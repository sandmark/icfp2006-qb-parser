(ns qbasic-parser.core-test
  (:require [clojure.test :as t]
            [qbasic-parser.core :as sut]
            [qbasic-parser.test-helper :as h]
            [clojure.java.io :as io]))

(t/deftest source-test
  (let [p sut/parser]
    (t/testing "Roman Source"
      (t/is (h/parsed? (p (slurp (io/resource "qbasic_roman.bas")))))
      (t/is (h/parsed? (p (slurp (io/resource "qbasic_decimal.bas"))))))))

(t/deftest line-test
  (t/testing "Decimalize"
    (let [source "I PRINT word(IV) + \"XIV\"\nII GOTO I\n"
          expect "1 PRINT word(4) + \"XIV\"\n2 GOTO 1\n"]
      (t/is (= expect (sut/decimalize-str source))))
    (let [source "XC       REM  get username from command line\n"
          expect "90       REM  get username from command line\n"]
      (t/testing source
        (t/is (= expect (sut/decimalize-str source)))))
    (let [source "CCCXCV   PRINT \"attempting hack with \" + pwdcount + \" passwords \" + CHR(X)\n"
          expect "395   PRINT \"attempting hack with \" + pwdcount + \" passwords \" + CHR(10)\n"]
      (t/testing source
        (t/is (= expect (sut/decimalize-str source)))))))
