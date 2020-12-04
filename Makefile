.PHONY: dev test

dev:
	clj -A:dev

env:
	conda create -n numpy_clj_testenv python=3.8 numpy scipy=1.5.2

test:
	clojure -M:core-matrix:dev -m numpy-clj.compliance-test