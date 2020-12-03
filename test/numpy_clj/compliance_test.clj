(ns numpy-clj.compliance-test
  (:require #_[numpy-clj.core :as core]
            [cljs.test :refer-macros [deftest ]];;is testing run-tests
            [clojure.core.matrix.compliance-tester :as core-test]))

(deftest compliance-test
  (core-test/compliance-test :numpy-clj))