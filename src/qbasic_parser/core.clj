(ns qbasic-parser.core
  (:gen-class)
  (:require
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.tools.cli :as cli]
   [clojure.walk :as w]
   [instaparse.core :as insta]
   [mount.core :as m]
   [qbasic-parser.ebnf :as ebnf]
   [qbasic-parser.roman :as roman]
   [qbasic-parser.compiler :as compiler]))

(m/defstate parser
  :start (insta/parser ebnf/line))

(defn roman->decimal [tree]
  (if (and (coll? tree)
           (= :roman (first tree)))
    (roman/tree->decimal tree)
    tree))

(defn decimal->roman [tree]
  (if (and (coll? tree)
           (= :number (first tree)))
    (-> (rest tree)
        str/join
        Integer/parseInt
        roman/decimal->roman)
    tree))

(defn string-and-integer [tree]
  (when (or (string? tree) (integer? tree))
    (str tree)))

(defn tree->string [tree]
  (->> tree flatten (keep string-and-integer) str/join))

(defn parse [source]
  (let [tree (parser source)]
    (if (= instaparse.gll.Failure (type tree))
      (throw (ex-info "Parse Error" {:cause tree}))
      tree)))

(defn decimalize-str [source]
  (try
    (->> source parse (w/postwalk roman->decimal) tree->string)
    (catch clojure.lang.ExceptionInfo e
      (pp/pprint (ex-data e)))))


(defn romanize-str [source]
  (try
    (->> source parse (w/postwalk decimal->roman) tree->string)
    (catch clojure.lang.ExceptionInfo e
      (pp/pprint (ex-data e)))))

(defn decimalize [file]
  (-> file slurp decimalize-str println))

(defn romanize [file]
  (-> file slurp romanize-str println))

(def cli-options
  [["-r" "--roman"]
   ["-c" "--compile"]])

(defn -main [& args]
  (let [opts                      (cli/parse-opts args cli-options)
        {:keys [:roman :compile]} (:options opts)
        transform                 (if roman romanize decimalize)
        [filename]                (:arguments opts)]
    (m/start)
    (cond compile   (-> filename compiler/compile-file println)
          transform (transform filename)
          :else
          (println "Usage: qbasic-parser.jar [-r|--roman] <filename>"))
    (m/stop)))
