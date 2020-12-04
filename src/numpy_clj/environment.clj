(ns numpy-clj.environment
  (:require [libpython-clj.python :as py]))

(defn initialize! []
  (py/initialize!
   :python-executable "~/opt/miniconda3/envs/numpy_clj_testenv/bin/python3.8"
   :library-path "~/opt/miniconda/envs/numpy_clj_testenv/lib/libpython3.8m.dylib"))

(defn main! []
  (initialize!))