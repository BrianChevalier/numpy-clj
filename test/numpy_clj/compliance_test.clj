(ns numpy-clj.compliance-test
  (:require [numpy-clj.core :as core]
            [clojure.test :refer [deftest is run-tests testing]];;is testing
            [clojure.core.matrix :as mat]
            [numpy-clj.operators :as op]
            [libpython-clj.python :refer [py. py.. py.-] :as py]
            [clojure.core.matrix.compliance-tester :as t]
            [libpython-clj.require :refer [require-python]]))

(require-python '[numpy :as np])
(require-python '[builtins])
(require-python '[numpy.linalg :as lin])
(require-python '[scipy.linalg :as scilin])

(deftest compliance-test
  ;;(with-redefs [inc (fn [n] (op/+ n 1))]
   (t/compliance-test :numpy-clj));)

; 980 tests finished, problems found. 😭 errors: 27, failures: 2, ns: 1, vars: 2


(deftest coerce-test
  ;; equals calls matrix-equals
  ;;   * determines if two arrays are numerically equivalent 
  ;; e= calls value-equals
  ;; e== cals equals
  (testing
   (is (mat/equals (np/array [0 1]) (np/array [0 1])))
    (is (mat/equals (np/array [[0 1]]) (np/array [[0 1]])))
    (is (mat/e= (np/array [0 1]) (np/array [0 1])))
    (is (mat/e== (np/array [0 1]) (np/array [0 1]))))
  (testing
   (is (mat/equals (mat/coerce [0 1]) (np/array [0 1])))
    (is (mat/equals (mat/coerce [[0 1]]) (np/array [[0 1]])))
    (is (mat/e= (mat/coerce [0 1]) (np/array [0 1])))
    (is (mat/e== (mat/coerce [0 1]) (np/array [0 1])))))


;; (defn -main []
;;   #_(t/compliance-test :numpy-clj)
;;   (run-tests))

(comment
  (mat/select (mat/array [[0 1 2] [0 1 2]]) [0] :all))

(comment
  (def im core/cannonical-object)

  (t/instance-test im)
  (mat/equals (mat/scalar-array 0) (mat/new-scalar-array im))
  (mat/new-scalar-array im)
  (t/test-impl-scalar-array im)
  (t/test-implementation-key im)
  (t/test-double-array-ops im)
  (t/test-dimensionality-assumptions im)
  (mat/dimension-count im -1)
  (t/test-immutable-assumptions im)
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