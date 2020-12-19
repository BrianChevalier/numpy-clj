(ns numpy-clj.test-runner
  (:require
   [numpy-clj.compliance-test]
   [clojure.test :refer [run-tests]]))

(defn -main []
  (let [{:keys [fail error]}
        (run-tests 'numpy-clj.compliance-test)]
    (shutdown-agents)
    (System/exit (+ fail error))))