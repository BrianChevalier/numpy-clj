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
   (t/compliance-test :numpy-clj))

(defn -main []
  (t/compliance-test :numpy-clj))
