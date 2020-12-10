(ns numpy-clj.environment
  (:require [libpython-clj.python :as py]
            [clojure.string :as str]))

(def user-home (System/getProperty "user.home"))
(def user-dir (System/getProperty "user.dir"))
(def os-name (System/getProperty "os.name"))

(defn system-path []
  (let [home   (case (str/starts-with? user-dir "/github")
                 true "/github/home"
                 false user-home)
        prefix (case os-name
                 "Mac OS X" "opt/"
                 "")
        env-name "numpy_clj"]
    (str home "/" prefix "miniconda3/envs/" env-name)))

(defn initialize! []
  (py/initialize!
   :python-executable (str (system-path) "/bin/python3.8")
   :library-path (str (system-path) "/lib/libpython3.8m.dylib")))

(defn main! []
  (initialize!))
