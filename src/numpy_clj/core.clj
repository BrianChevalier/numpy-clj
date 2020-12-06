(ns numpy-clj.core
  (:require
   [clojure.core.matrix :as mat]
   [clojure.core.matrix.implementations :as imp]
   ;;[clojure.core.matrix.linear :as li]
   [clojure.core.matrix.protocols :as proto]
   [clojure.core.protocols :refer [Datafiable]]
   [libpython-clj.python :refer [py. py.. py.-] :as py]
   [libpython-clj.require :refer [require-python]]
   [libpython-clj.python.object :as obj]
   [numpy-clj.environment :refer [main!]]
   [numpy-clj.operators :as op]))

(main!)
(require-python '[numpy :as np])
(require-python '[builtins])
(require-python '[numpy.linalg :as lin])
(require-python '[scipy.linalg :as scilin])

(defn validate-index [& indicies]
  (cond
    (not-every? false? (map neg? indicies))
    (throw (Exception. (str "Error: invalid index " indicies)))))

(defn valid-dimension? [ndims dim]
  ;;(tap> {:ndims ndims :dim dim :valid (or (< 0 dim) (> dim ndims))})
    (or (<= 0 dim) (> dim ndims)))

(extend-type (class (np/array [0 0]))

  Datafiable
  (datafy [m]
    (mat/to-nested-vectors m))

  ;;ASeq
  ;; clojure.lang.ISeq
  ;; (-first [m]
  ;;   (-> m
  ;;       .toArray
  ;;       js->clj
  ;;       first))
  ;; (-rest [m]
  ;;   (-> m
  ;;       .toArray
  ;;       js->clj
  ;;       rest)
    ;; #_(let [nRows (-> m .size first)]
    ;;     (for [row (range 1 nRows)]
    ;;       (-> (m/row m row)
    ;;           .toArray
    ;;           js->clj))))

  ;; ====================================================================
  ;; MANDATORY PROTOCOLS FOR ALL IMPLEMENTATIONS
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
    (case
     (valid-dimension? (py.- m :ndim) dimension-number)
      true (nth (np/shape m) dimension-number)
      false (throw (Exception. (str "Error: dimension out of range " dimension-number)))))

  proto/PIndexedAccess
  (get-1d [m row]
    (validate-index row)
    (py/get-item m [row]))
  (get-2d [m row column]
    (validate-index row)
    (py/get-item m [row column]))
  (get-nd [m indexes]
    (validate-index indexes)
    (py/get-item m (vec indexes)))

  proto/PIndexedSetting
  (set-1d [m row v]
    (validate-index row)
    (let [copy (np/copy m)]
      (py/set-item! copy [row] v) copy))
  (set-2d [m row column v]
    (validate-index row column)
    (let [copy (np/copy m)]
      (py/set-item! copy [row column] v) copy))
  (set-nd [m indexes v]
    (validate-index indexes)
    (let [copy (np/copy m)]
      (py/set-item! copy (vec indexes) v) copy))
  (is-mutable? [m] true)

  ;; ====================================================================
  ;; MANDATORY PROTOCOLS FOR MUTABLE MATRICES
  proto/PIndexedSettingMutable
  (set-1d! [m row v]
    (py/set-item! m [row] v) m)
  (set-2d! [m row column v]
    (py/set-item! m [row column] v) m)
  (set-nd! [m indexes v]
    (py/set-item! m (vec indexes) v) m)

  proto/PMatrixCloning
  (clone [m]
    (np/copy m))

  ;; ====================================================================
  ;; OPTIONAL PROTOCOLS
  proto/PZeroDimensionConstruction
  (new-scalar-array
    ([m] (np/array 0))
    ([m value] (np/array value)))

  proto/PSpecialisedConstructors
  (identity-matrix [m dims]
    (np/identity dims))
  (diagonal-matrix [m diagonal-values]
    (np/diag diagonal-values))

  proto/PCoercion
  (coerce-param [m param]
    (cond
      (= (type param) :pyobject)
      param

      (vector? param)
      (np/array (py/->py-list param))

      :else (-> param
                mat/to-nested-vectors
                np/array)))

  proto/PBroadcast
  (broadcast [m target-shape]
    (np/broadcast_to m target-shape))

;;   proto/PConversion
;;   (convert-to-nested-vectors [m]
;;     (obj/python->jvm-copy-persistent-vector m))

  proto/PReshaping
  (reshape [m shape]
    (np/reshape m shape))

  proto/PMatrixSlices
  (get-row [m i] ;; A[i, :]
    (py/get-item m [i]))
  (get-column [m i] ;; A[:, i]
    (py/get-item m [(builtins/slice 0 nil) i]))
  (get-major-slice [m i] ;; A[i, :, :, ...]
    (py/get-item m [i (builtins/slice 0 nil)]))
  (get-slice [m dimension i] ;; A[:, :, ..., i, ..., :]
    (py/get-item m
                 (vec (for [j (range (mat/dimensionality m))]
                        (if (== j dimension) i (builtins/slice 0 nil))))))

  proto/PMatrixRows
  (get-rows [m]
    (builtins/list (builtins/iter m)))

  proto/PMatrixColumns
  (get-columns [m]
    (builtins/list (builtins/iter (np/transpose m))))

  proto/PSliceView
  (get-major-slice-view [m i]
    (py/get-item m [i (builtins/slice 0 nil)]))

  proto/PSubVector
  (subvector [m start length]
    (py/get-item m [(builtins/slice start (+ start length))]))

  proto/PMatrixSubComponents
  (main-diagonal [m]
    (np/diag m))

  ;; ============================================================
  ;; Equality operations

  proto/PValueEquality
  (value-equals [m a]
    (let [v1 (mat/eseq m)
          v2 (mat/eseq a)
          sh-eq? (mat/same-shape? m a)
          val-eq? (every? true?
                          (for [[a b] (map vector v1 v2)]
                            (= a b)))]
      (every? true? [sh-eq? val-eq?]))
    #_(np/all (op/== m a)))

  proto/PMatrixEquality
  (matrix-equals [a b]
    (let [v1 (mat/eseq a)
          v2 (mat/eseq b)
          sh-eq? (mat/same-shape? a b)
          val-eq? (every? true?
                          (for [[a b] (map vector v1 v2)]
                            (== a b)))]
      (every? true? [sh-eq? val-eq?]))
    #_(np/all (op/== a b)))

;;   proto/PMatrixEqualityEpsilon
;;   (matrix-equals-epsilon [a b eps]
;;                          (println ">>>>>>>>")
;;                          (println (str a " " b  " " eps))
;;     (np/allclose a b :atol (py/->py-float eps)))

  ;; ====================================================================
  ;; Mathematical operations

  proto/PMatrixMultiply
  (matrix-multiply [m a]
    (op/mmul m a))
  (element-multiply [m a]
    (op/* m a))

  proto/PMatrixProducts
  (inner-product [m a]
    (np/inner m a))
  (outer-product [m a]
    (np/outer m a))

  proto/PMatrixDivide
  (element-divide
    ([m] (np/reciprocal m))
    ([m a] (op// m a)))

  proto/PMatrixDivideMutable
  (element-divide!
    ([m]
     (py. (np/ones (np/shape m)) __itruediv__ m))
    ([m a]
     (py. m __itruediv__ a)))

  proto/PMatrixMultiplyMutable
  (matrix-multiply! [m a]
    (op/immul m a))
  (element-multiply! [m a]
    (op/*= m a))

  proto/PMatrixScaling
  (scale [m constant]
    (op/* m constant))
  (pre-scale [m constant]
    (op/* m constant))

  proto/PMatrixMutableScaling
  (scale! [m factor]
    (op/*= m factor))
  (pre-scale! [m factor]
    (op/*= m factor))

  proto/PMatrixAdd
  (matrix-add [m a]
    (op/+ m a))
  (matrix-sub [m a]
    (op/- m a))

  proto/PMatrixAddMutable
  (matrix-add! [m a]
    (op/+= m a))
  (matrix-sub! [m a]
    (op/-= m a))

  proto/PSubMatrix
  (submatrix [d dim-ranges]
    ;; [[start len]]
    ;; [start:(start+length), ...]
    (let [inds (vec
                (for [[start len] dim-ranges]
                  (builtins/slice start (if (nil? len)
                                          (obj/py-none)
                                          (+ start len)))))]
      (py/get-item d inds)))

  proto/PTranspose
  (transpose [m]
    (np/transpose m))

  proto/PVectorOps
  (vector-dot [a b]
    (np/dot a b))
  (length [a]
    (lin/norm a :ord 2))
  (length-squared [a]
    (op/** (lin/norm a) 2))
  (normalise [a]
    (op// (py. a :astype "float64") (lin/norm a)))

  proto/PVectorCross
  (cross-product [a b]
    (np/cross a b))
  (cross-product! [a b]
    (let [;;copy (np/copy a)
          cross (np/cross a b)]
      (py/set-attr! a :dtype "float64")
      (np/copyto a cross)))

  proto/PMutableVectorOps
  (normalise! [a]
    (let [b (np/copy a)
          len (lin/norm a)]
      ;; changing dtype in place has weird behavior
      ;; need to copy back original data!
      (py/set-attr! a :dtype "float64")
      (np/copyto a b)
      (op/idiv a len)))

  proto/PMatrixOps
  (trace [m]
    (np/trace m))
  (determinant [m]
    (lin/det m))
  (inverse [m]
    (lin/inv m))

  proto/PSummable
  (element-sum [m]
    (np/sum m))

  proto/PExponent
  (element-pow [m exponent]
    (op/** m exponent))

  proto/PSquare
  (square [m]
    (op/** m 2))

  ;; ====================================================================
  ;; Elementary Row Operation Protocols

;;   proto/PRowOperations
;;   (swap-rows [m i j])
;;   (multiply-row [m i k])
;;   (add-row [m i j k])

  proto/PRowSetting
  (set-row [m i row]
    (let [copy (np/copy m)]
      (py/set-item! copy [i (builtins/slice 0 nil)] row) copy))
  (set-row! [m i row]
    (py/set-item! m [i (builtins/slice 0 nil)] row))

  proto/PColumnSetting
  (set-column [m i column]
    (let [copy (np/copy m)]
      (py/set-item! copy [(builtins/slice 0 nil) i] column) copy))
  (set-column! [m i column]
    (py/set-item! m [(builtins/slice 0 nil) i] column))

  proto/PMathsFunctions
  (abs [m] (np/abs m))
  (acos [m] (np/arccos m))
  (asin [m] (np/arcsin m))
  (atan [m] (np/arctan m))
  (cbrt [m] (np/cbrt m))
  (ceil [m] (np/ceil m))
  (cos [m] (np/cos m))
  (cosh [m] (np/cosh m))
  (exp [m] (np/exp m))
  (floor [m] (np/floor m))
  (log [m] (np/log m))
  (log10 [m] (np/log10 m))
  (round [m] (np/round m))
  (signum [m] (np/sign m))
  (sin [m] (np/sin m))
  (sinh [m] (np/sinh m))
  (sqrt [m] (np/sqrt m))
  (tan [m] (np/tan m))
  (tanh [m] (np/tanh m))
  (to-degrees [m] (np/rad2deg m))
  (to-radians [m] (np/deg2rad))

  proto/PElementCount
  (element-count [m]
    (py.- m :size))

  proto/PElementMinMax
  (element-min [m]
    (np/amin m))
  (element-max [m]
    (np/amax m))
  (element-clamp [m a b]
    (np/clip m a b))

;;   proto/PCompare
;;   (element-compare [a b]
;;     (np/equal a b))
;;   (element-if [m a b])
;;   (element-lt [m a])
;;   (element-le [m a])
;;   (element-gt [m a])
;;   (element-ge [m a])
;;   (element-ne [m a])
;;   (element-eq [m a])

  proto/PFunctionalOperations
  (element-seq [m]
    (obj/python->jvm-copy-persistent-vector (py. m :flatten)))
  (element-map
    ([m f]
     (map f (mat/eseq m)))
    ([m f a]
     (map f (mat/eseq m) a))
    ([m f a more]
     (apply map f (mat/eseq m) a more)))
  ;; (element-map!
  ;;   ([m f])
  ;;   ([m f a])
  ;;   ([m f a more]))
  (element-reduce
    ([m f]
     (reduce f (mat/eseq m)))
    ([m f init]
     (reduce f init (mat/eseq m))))

  proto/PGenericValues
  (generic-zero [m]
    (py/->py-float 0))
  (generic-one [m]
    (py/->py-float 1))
  (generic-value [m]
    (py/->py-float 0))

  proto/PGenericOperations
  (generic-add [m] op/+)
  (generic-mul [m] op/*)
  (generic-negate [m] op/-)
  (generic-div [m] op//)

  ;; ===========================================================
  ;; Protocols for higher-level array indexing

  proto/PSelect
  (select [a args]
    (py/get-item a (apply np/ix_ args)))

  proto/PSelectView
  (select-view [a args]
    (py/get-item a (apply np/ix_ args)))

  ;; =========================================
  ;; LINEAR ALGEBRA PROTOCOLS
  proto/PNorm
  (norm [m p]
    (lin/norm m :ord p))

;;   proto/PQRDecomposition
;;   (qr [m options]
;;     (lin/qr m))

;;   proto/PCholeskyDecomposition
;;   (cholesky [m options]
;;     (lin/cholesky m))

  proto/PLUDecomposition
  (lu [m options]
    (let [[P L U] (scilin/lu m)
          return  (:return options)]
      (select-keys {:P P :L L :U U} return)))

;;   proto/PSVDDecomposition
;;   (svd [m options]
;;     (lin/svd m))

  proto/PEigenDecomposition
  (eigen [m options]
    (let [[vals vecs] (lin/eig m)
          return (:return options)]
      (select-keys {:Q vecs            ;; :Q eigenvectors
                    :A (np/diag vals)} ;; :A diagonal matrix of eigenvals
                   return)))

  proto/PSolveLinear
  (solve [a b]
    (lin/solve a b))

  proto/PLeastSquares
  (least-squares [a b]
    (lin/lstsq a b)))

(def cannonical-object (np/array [0 0]))
(imp/register-implementation :numpy-clj cannonical-object)
(mat/set-current-implementation :numpy-clj)

