(ns numpy-clj.operators
  "Wraps the Python special methods
   ex: (+ a b) will do a.__add__(b)"
  (:refer-clojure :exclude [+ - * / < <= == >= >])
  (:require [libpython-clj.python :refer [py.] :as py]))

(defn ->py [a]
  a #_(cond
    (type 2)   (py/->py-long a)
    (type 2.0) (py/->py-float a)
    :else a))

;; Binary Operators
(defn +
  ([a]   (py. a :__pos__))
  ([a b] (py. a :__add__ (->py b))))
(defn -
  ([a]   (py. a :__neg__))
  ([a b] (py. a :__sub__ (->py b))))
(defn *
  ([a b] (py. (->py a) :__mul__ (->py b))))
(defn floordiv
  ([a b] (py. a :__floordiv__ (->py b))))
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
  ([a b] (py. a :__iadd__ b)))
(defn -=
  ([a b] (py. a :__isub__ b)))
(defn *=
  ([a b] (py. a :__imul__ b)))
(defn idiv
  ([a b] (py. a :__itruediv__ b)))
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