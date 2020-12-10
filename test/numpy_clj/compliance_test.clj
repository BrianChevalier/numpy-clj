(ns numpy-clj.compliance-test
  (:require numpy-clj.environment))

(numpy-clj.environment/main!)

(require '[numpy-clj.core :as core]
         '[clojure.test :refer [deftest is run-tests testing]];;is testing
         '[clojure.core.matrix.compliance-tester :as t])

(deftest compliance-test
  (t/compliance-test :numpy-clj))

(defn -main []
  (numpy-clj.environment/main!)
  #_(run-tests)
  (t/compliance-test :numpy-clj))
