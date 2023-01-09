(ns qbasic-parser.core
  (:gen-class)
  (:require
   [clojure.string :as str]
   [clojure.tools.cli :as cli]
   [clojure.walk :as w]
   [instaparse.core :as insta]
   [mount.core :as m]
   [qbasic-parser.ebnf :as ebnf]
   [qbasic-parser.roman :as roman]))

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

(defn decimalize-str [source]
  (->> source
       parser
       (w/postwalk roman->decimal)
       tree->string))

(defn romanize-str [source]
  (->> source
       parser
       (w/postwalk decimal->roman)
       tree->string))

(defn decimalize [file]
  (-> file slurp decimalize-str println))

(defn romanize [file]
  (-> file slurp romanize-str println))

(def cli-options
  [["-r" "--roman"]])

(defn -main [& args]
  (let [opts             (cli/parse-opts args cli-options)
        {:keys [:roman]} (:options opts)
        transform        (if roman romanize decimalize)
        [filename]       (:arguments opts)]
    (m/start)
    (if filename
      (transform filename)
      (println "Usage: qbasic-parser.jar [-r|--roman] <filename>"))
    (m/stop)))
