(ns qbasic-parser.ebnf-test
  (:require  [clojure.test :as t]
             [qbasic-parser.ebnf :as sut]
             [qbasic-parser.test-helper :as h]
             [instaparse.core :as insta]))

(t/deftest exp-test
  (let [p (insta/parser sut/exp)]
    (t/testing "Expression"
      (t/testing "Integer + Integer"
        (t/is (h/parsed? (p "I + I + I"))))
      (t/testing "String + variable"
        (t/is (h/parsed? (p "\"user: \" + username"))))
      (t/testing "Integer + variable"
        (t/is (h/parsed? (p "I + var"))))
      (t/testing "variable + Integer"
        (t/is (h/parsed? (p "var + I")))))
    (t/testing "Literal"
      (t/testing "String"
        (t/is (h/parsed? (p "\"(\\b.bb):! \""))))
      (t/testing "Roman"
        (t/is (h/parsed? (p "XIV")))))))

(t/deftest assignment-test
  (let [p (insta/parser sut/assign)]
    (t/testing "Assignment"
      (t/testing "Literal"
        (t/is (h/parsed? (p "i = I")))
        (t/is (h/parsed? (p "s = \"String\"")))
        (t/is (h/parsed? (p "i = i + I"))))
      (t/testing "Array"
        (t/is (h/parsed? (p "words(i) = \"xyzzy\"")))))))

(t/deftest variable-test
  (let [p (insta/parser sut/exp)]
    (t/testing "Variable"
      (t/testing "Normal"
        (t/is (h/parsed? (p "variable"))))
      (t/testing "Array"
        (t/is (h/parsed? (p "words(I)")))
        (t/testing "with variable index"
          (t/is (h/parsed? (p "words(i)"))))))))

(t/deftest funcall-test
  (let [p (insta/parser sut/exp)]
    (t/testing "Function Call"
      (t/testing "No Arguments"
        (t/is (h/parsed? (p "ARGS()"))))
      (t/testing "One Argument"
        (t/is (h/parsed? (p "CHR(X)"))))
      (t/testing "Two Arguments"
        (t/is (h/parsed? (p "CHECKPASS(username, words(i))")))))))

(t/deftest statement-test
  (let [p (insta/parser sut/statement)]
    (t/testing "Statement"
      (t/testing "REM"
        (t/is (h/parsed? (p "REM")))
        (t/is (h/parsed? (p "REM comment"))))
      (t/testing "IF"
        (t/is (h/parsed? (p "IF ARGS() > I THEN GOTO LXXXV"))))
      (t/testing "GOTO"
        (t/is (h/parsed? (p "GOTO L"))))
      (t/testing "PRINT"
        (t/is (h/parsed? (p "PRINT \"usage: ./hack.exe username\"")))
        (t/is (h/parsed? (p "PRINT ARG(II) + \"is present.\""))))
      (t/testing "END"
        (t/is (h/parsed? (p "END"))))
      (t/testing "DIM"
        (t/is (h/parsed? (p "DIM i AS INTEGER")))
        (t/is (h/parsed? (p "DIM username AS STRING")))
        (t/is (h/parsed? (p "DIM words(pwdcount) AS STRING")))))))
