(ns qbasic-parser.roman
  (:require [mount.core :as m]))

(m/defstate ebnf-roman
  :start "
roman = thousands hundreds tens ones
I = 'I'
V = 'V'
X = 'X'
L = 'L'
C = 'C'
D = 'D'
M = 'M'

IV = 'IV'
IX = 'IX'
XL = 'XL'
XC = 'XC'
CD = 'CD'
CM = 'CM'

ones = V? I? I? I? | IV | IX
tens = L? X? X? X? | XL | XC
hundreds = D? C? C? C? | CD | CM
thousands = M? M? M?")

(def symbols
  {:I  1, :V  5, :X  10, :L  50, :C  100, :D  500, :M 1000
   :IV 4, :IX 9, :XL 40, :XC 90, :CD 400, :CM 900})

(def symbols-sorted
  [[1000 "M"] [900 "CM"] [500 "D"] [400 "CD"] [100 "C"] [90 "XC"]
   [50 "L"] [40 "XL"] [10 "X"] [9 "IX"] [5 "V"] [4 "IV"] [1 "I"]])

(defn- largest-numeral [n]
  (->> symbols-sorted
       (filter (comp (partial >= n) first))
       first))

(defn decimal->roman [n]
  (when-let [[i letter] (largest-numeral n)]
    (str letter (decimal->roman (- n i)))))

(defn tree->decimal [tree]
  (letfn [(f [[label & elements]]
            (if-let [n (label symbols)]
              [n]
              (mapcat f elements)))]
    (if (= (first tree) :roman)
      [:decimal (reduce + (f tree))]
      tree)))
