(ns numpy-clj.operators
  "Wraps the Python special methods
   ex: (+ a b) will do a.__add__(b)"
  (:refer-clojure :exclude [+ - * / < <= == >= >])
  (:require 
   [libpython-clj.require :refer [require-python]]
   [libpython-clj.python :refer [py.] :as py]))

(require-python '[numpy :as np])

;; Binary Operators
(defn +
  ([a]   (py. a :__pos__))
  ([a b] (py. a :__add__ b)))
(defn -
  ([a]   (py. a :__neg__))
  ([a b] (py. a :__sub__ b)))
(defn *
  ([a b] (py. a :__mul__ b)))
(defn floordiv
  ([a b] (py. a :__floordiv__ b)))
(defn /
  ([a b] (py. a :__truediv__ b)))
(defn %
  ([a b] (py. a :__mod__ b)))
(defn **
  ([a b] (py. a :__pow__ b)))
(defn mmul
  ([a b] (py. a :__matmul__ b)))

;; Extended Assignment
(defn +=
  ([a b] (np/add a b :out a :casting "unsafe")))
(defn -=
  ([a b] (py. a :__isub__ b)))
(defn *=
  ([a b] (np/multiply a b :out a :casting "unsafe")))
(defn idiv
  ([a b] (np/divide a b :out a :casting "unsafe")))
(defn ifloordiv
  ([a b] (py. a :__ifloordiv__ b)))
(defn immul
  ([a b] (py. a :__imatmul__ b)))

;; Comparison Operators
(defn <
  ([a b] (py. a :__lt__ b)))
(defn <=
  ([a b] (py. a :__le__ b)))
(defn ==
  ([a b] (py. a :__eq__ b)))
(defn !=
  ([a b] (py. a :__ne__ b)))
(defn >=
  ([a b] (py. a :__ge__ b)))
(defn >
  ([a b] (py. a :__gt__ b)))