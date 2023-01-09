(ns qbasic-parser.test-helper)

(defn parsed? [x]
  (not (= instaparse.gll.Failure (type x))))
