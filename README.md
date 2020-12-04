# numpy-clj

A [core.matrix](https://github.com/mikera/core.matrix) implementation using [NumPy](https://github.com/numpy/numpy) via [libpython-clj](https://github.com/clj-python/libpython-clj). You can use the Python interop functionality from libpython-clj directly, and/or use this library  Check core.matrix [API documentation](https://cljdoc.org/d/net.mikera/core.matrix/0.62.0/api/clojure.core.matrix) for usage. If you're new to core.matrix check out [matrix-compare](https://brianchevalier.github.io/matrix-compare/) to see how to accomplish common tasks in core.matrix, MATLAB, NumPy, etc.

## Development

Still in early development. Running tests requires a version of core.matrix with a deps.edn to build the correct classpath. You can clone my fork of core.matrix into the same directory of numpy-clj.

### Testing

    make test