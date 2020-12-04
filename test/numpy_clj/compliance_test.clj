(ns numpy-clj.compliance-test
  (:require [numpy-clj.core :as core]
            [clojure.test :refer [deftest run-tests]];;is testing
            [clojure.core.matrix :as mat]
            [clojure.core.matrix.compliance-tester :as t]))

;; (deftest compliance-test
;;   (t/compliance-test :numpy-clj))

(defn -main []
  (t/compliance-test :numpy-clj)
  #_(run-tests))

(comment
  (def im core/cannonical-object)
  (t/instance-test im)
  
  ;;(t/test-implementation im)
  (mat/new-scalar-array im)
  (t/test-impl-scalar-array im)
  (t/test-implementation-key im)
  (t/test-assumptions-for-all-sizes im)
  (t/test-coerce-via-vectors im)
  (t/test-equality im)
  (t/test-methods-existence im)
  (when (mat/supports-dimensionality? im 2)
    (t/matrix-tests-2d im))
  (when (mat/supports-dimensionality? im 1)
    (t/vector-tests-1d im))
  (t/test-array-interop im)
  (t/test-numeric-functions im)
  (t/test-dimensionality im)
  (t/test-row-operations im)
  (t/test-qr im)
  (t/test-new-matrices im))