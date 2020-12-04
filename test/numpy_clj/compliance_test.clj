(ns numpy-clj.compliance-test
  (:require [numpy-clj.core :as core]
            [clojure.test :refer [deftest run-tests]];;is testing
            [clojure.core.matrix.compliance-tester :as core-test]))

(deftest compliance-test
  (core-test/compliance-test :numpy-clj))

(defn -main []
  (core-test/compliance-test :numpy-clj)
  #_(run-tests))