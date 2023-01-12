(ns qbasic-parser.compiler
  (:require [clojure.java.io :as io]
            [clojure.zip :as z]
            [clojure.string :as str]))

(declare emit)

(defn- find-body
  "Returns expressions after START symbol from a tree sequence given as s."
  [s]
  (loop [loc (z/next (z/seq-zip s))]
    (let [node (z/node loc)]
      (if (= 'START node)
        (cons 'START (z/rights loc))
        (recur (z/right loc))))))

(defn- ->label [s]
  [(str "REM LABEL: " s)])

(defn- ->dim [[_ name value]]
  (if (int? value)
    [(str "DIM " name " AS INTEGER") (str name " = " value)]
    [(str "DIM " name " AS STRING") (str name " = \"" value "\"")]))

(defn- ->array-index [[_ index variable]]
  [(str variable "(" index ")")])

(defn- ->chr [[_ expr]]
  [(str "CHR(" expr ")")])

(defn- ->concat [x]
  (->> (rest x)
       (mapcat emit)
       (str/join " + ")
       vector))

(defn- ->assign [[_ name value]]
  [(str name " = " (str/join (emit value)))])

(defn- ->print [x]
  (let [args (->> (rest x)
                  (mapcat emit)
                  (str/join " + "))]
    [(str "PRINT " args)]))

(defn- label? [x]
  (and (symbol? x)
       (= (name x) (str/upper-case (name x)))))

(defn- variable? [x]
  (and (symbol? x)
       (not (label? x))))

(defn- ->if [[_ expr then]]
  [(str "IF " (str/join (emit expr)) " THEN " (str/join (emit then)))])

(defn- ->goto [[_ label]]
  [(str "GOTO " label)])

(defn- ->funcall [[fname & args]]
  (let [args-str (->> args (mapcat emit) (str/join ", "))]
    [(str (str/upper-case fname) "(" args-str ")")]))

(defn- ->increment [[_ variable]]
  [(str variable " = " variable " + 1")])

(defn- ->expression [[op x y]]
  [(format "%s %s %s" x op y)])

(defn emit [x]
  (cond (label? x)    (->label x)
        (variable? x) [(str x)]
        (string? x)   [(str "\"" x "\"")]
        :else
        (let [op (first x)]
          (cond (= 'defparameter op) (->dim x)
                (= 'incf op)         (->increment x)
                (= '< op)            (->expression x)
                (= '> op)            (->expression x)
                (= 'chr op)          (->chr x)
                (= 'go op)           (->goto x)
                (= 'if op)           (->if x)
                (= 'checkpass op)    (->funcall x)
                (= 'qprint op)       (->print x)
                (= 'concat op)       (->concat x)
                (= 'setf op)         (->assign x)
                (= 'at op)           (->array-index x)))))

(defn compile-exprs
  "TODO: Link LABEL and GOTO"
  [exprs]
  (->> exprs
       find-body
       (mapcat emit)
       (map-indexed #(str (+ 500 %1) " " %2))
       (str/join "\n")))

(defn compile-file [path]
  (-> path slurp read-string compile-exprs))
