(ns dev
  (:require [clojure.java.io :as io]
            [clojure.repl :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.tools.namespace.repl :refer [refresh]]
            [mount.core :as mount :refer [start stop]]))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(defn reset []
  (stop)
  (refresh :after 'mount.core/start))
