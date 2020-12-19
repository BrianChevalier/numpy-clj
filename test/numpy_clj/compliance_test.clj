(ns numpy-clj.compliance-test
  (:require numpy-clj.environment))

(numpy-clj.environment/main!)

(require '[numpy-clj.core :as core]
         '[clojure.test :refer [deftest is run-tests testing]]
         '[clojure.core.matrix.compliance-tester :as t])

(deftest compliance-test
  (t/compliance-test :numpy-clj))
