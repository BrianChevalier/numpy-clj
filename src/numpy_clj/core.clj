(ns numpy-clj.core
  (:require
   [clojure.core.matrix :as core]
   [libpython-clj.python :refer [py. py.. py.-] :as py]
   [libpython-clj.require :refer [require-python]]
   [clojure.core.matrix.implementations :as imp]
   [clojure.core.matrix.protocols :as proto]
   #_[clojure.core.protocols :refer [Datafiable]]))

(require-python '[numpy :as np])

(extend-type (class (np/array [0 0]))
  proto/PImplementation
  (implementation-key [m]
    :numpy-clj)
  (meta-info [m]
    {:doc "NumPy implementation"})
  (construct-matrix [m data]
    (np/array data))
  (new-vector [m length]
    (np/zeros [length]))
  (new-matrix [m rows columns]
    (np/zeros [rows columns]))
  (new-matrix-nd [m shape]
    (np/zeros shape))
  (supports-dimensionality? [m dimensions] true)

  proto/PDimensionInfo
  (dimensionality [m]
    (py.- m :ndim))
  (get-shape [m]
    (np/shape m))
  (is-scalar? [m]
    (np/isscalar m))
  (is-vector? [m]
    (= 1 (py.- m :ndim)))
  (dimension-count [m dimension-number]
    (nth (np/shape m) dimension-number))

  proto/PIndexedAccess
  (get-1d [m row]
    (py/get-item m [row]))
  (get-2d [m row column]
    (py/get-item m [row column]))
  (get-nd [m indexes]
    (py/get-item m (vec indexes)))

  proto/PIndexedSetting
  (set-1d [m row v]
    (let [copy (np/copy m)]
      (py/set-item! copy [row] v) copy))
  (set-2d [m row column v]
    (let [copy (np/copy m)]
      (py/set-item! copy [row column] v) copy))
  (set-nd [m indexes v]
    (let [copy (np/copy m)]
      (py/set-item! copy (vec indexes) v) copy))
  (is-mutable? [m] true)

  proto/PIndexedSettingMutable
  (set-1d! [m row v]
    (py/set-item! m [row] v) m)
  (set-2d! [m row column v]
    (py/set-item! m [row column] v) m)
  (set-nd! [m indexes v]
    (py/set-item! m (vec indexes) v) m)

  proto/PMatrixCloning
  (clone [m] (np/copy m)))

(imp/register-implementation :numpy-clj (np/array [0 0]))
(core/set-current-implementation :numpy-clj)