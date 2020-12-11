# numpy-clj

[![Build Status](https://github.com/brianchevalier/numpy-clj/workflows/Clojure%20CI/badge.svg)](https://github.com/BrianChevalier/numpy-clj/actions?query=workflow%3A%22Clojure+CI%22)
[![Binder](https://mybinder.org/badge_logo.svg)](https://mybinder.org/v2/gh/BrianChevalier/core.matrix-examples/main?urlpath=lab/tree/Notebooks)


A Clojure [core.matrix](https://github.com/mikera/core.matrix) implementation using [NumPy](https://github.com/numpy/numpy) via [libpython-clj](https://github.com/clj-python/libpython-clj). `numpy-clj` allows you to use an idiomatic Clojure library, while still interoperating with Numpy, SciPy and the entire Python ecosystem.

Try out `numpy-clj` in JupyterLab via Binder along with other `core.matrix` implementations. [Click here](https://mybinder.org/v2/gh/BrianChevalier/core.matrix-examples/main?urlpath=lab/tree/Notebooks) or click the 'launch binder' badge above.

## Usage

`numpy-clj` does not directly provide an API, but is instead used by `core.matrix`. `numpy-clj` extends `core.matrix` to translate to equivalent `numpy` functions. Include `core.matrix` in your project and use the [core.matrix API](https://cljdoc.org/d/net.mikera/core.matrix/0.62.0/api/clojure.core.matrix).

If you're new to core.matrix check out [matrix-compare](https://brianchevalier.github.io/matrix-compare/) to see how to translate common operations from MATLAB and NumPy into core.matrix.

### Examples
To ensure the `numpy-clj` implementation is registered, make sure to require it in your namespace and set it as the current implementation.
```clojure
(ns your.namespace
  (:require [numpy-clj.core]
            [core.matrix :as m]))

(m/set-current-implementation :numpy-clj)

(m/array [[0 1 2]] [3 4 5])
=> [[0 1 2] ;; this will be a numpy object
    [3 4 5]]
```

You can also directly interoperate using `libpython-clj` and use `numpy` functions directly. Read the [official documentation](https://clj-python.github.io/libpython-clj/Usage.html) to learn more on writing Python in Clojure.

```clojure
(ns your.namespace
  (:require [numpy-clj.core]
            [core.matrix :as m]
            [libpython-clj.require :refer [require-python]]))

(require-python '[numpy :as np])

(np/linspace 0 10 100)
=> [ 0.          0.1010101 ...
```

### Using numpy-clj in your project
You can include any commit of this project as a [git dependency](https://clojure.org/guides/deps_and_cli#_using_git_libraries). Copy and paste the sha hash from any commit and add it to your `deps.edn`, like below.

```clojure
{:deps
 {brianchevalier/numpy-clj
  {:git/url "https://github.com/brianchevalier/numpy-clj"
   :sha ""}}}
```

## Development

Note: `numpy-clj` currently uses my fork of core.matrix ([via GitHub](https://clojure.org/guides/deps_and_cli#_using_git_libraries)) which adds a deps.edn file. When you run `make test` it will automatically checkout my fork of `core.matrix` with this change (branch: [deps.edn](https://github.com/BrianChevalier/core.matrix/tree/deps.edn)). Make sure to use the `:core-matrix` [alias](https://clojure.org/reference/deps_and_cli#_aliases) to use my branch.

Useful resources:
* [core.matrix protocols](https://cljdoc.org/d/net.mikera/core.matrix/0.62.0/api/clojure.core.matrix.protocols)
* [Clojure protocols](https://clojure.org/reference/protocols)
* [core.matrix implmenetation guide](https://github.com/mikera/core.matrix/wiki/Implementation-Guide)
* [libpython-clj documentation](https://clj-python.github.io/libpython-clj/libpython-clj.python.html)
* Slack channels:
    * [clojurians/core.matrix](https://clojurians.slack.com/archives/C0533TY12)
    * [clojurians/libpython-clj](https://clojurians.slack.com/archives/CLR5FD4ET)

### Testing
To run the core.matrix compliance tests run the following make rule.

```
make test
```

### Continuous Integration (CI)
* The continous integration here uses uses [GitHub Actions](https://github.com/features/actions)

To test changes against the exact CI environment:
* Install [Docker](https://docs.docker.com/get-docker/)
* Install [act](https://github.com/nektos/act) (eg. with homebrew: `brew install act`)
* Run the following make rule. Warning: This will require an 18GB(!) download but will fully replicate the GitHub Actions environment

```
make ci/local
```