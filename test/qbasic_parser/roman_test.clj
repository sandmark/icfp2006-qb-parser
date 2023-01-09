(ns qbasic-parser.roman-test
  (:require
   [clojure.test :as t]
   [instaparse.core :as insta]
   [qbasic-parser.roman :as sut]))

(t/deftest roman-test
  (let [p (insta/parser sut/ebnf-roman)]
    (t/testing "Roman"
      (let [roman   "MMMCMXCIX"
            numeric 3999]
        (t/testing "to Numeric"
          (t/is (= [:decimal numeric] (sut/tree->decimal (p roman))))
          (t/is (= [:other [:tree]]
                   (sut/tree->decimal [:other [:tree]]))))

        (t/testing "from Numberic"
          (t/is (= roman (sut/decimal->roman numeric))))

        (t/testing "Parse"
          (let [expect [:roman
                        [:thousands [:M "M"] [:M "M"] [:M "M"]]
                        [:hundreds  [:CM "CM"]]
                        [:tens [:XC "XC"]]
                        [:ones [:IX "IX"]]]]
            (t/is (= expect (p roman)))))))))
