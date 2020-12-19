PATH  := $(PATH):$(PWD)/bin:$(HOME)/miniconda3/bin
.PHONY: dev test

dev:
	clj -A:dev

env:
	pip install -r requirements.txt

test:
	clojure -M:core-matrix -m numpy-clj.test-runner

lint:
	clojure -M:kondo --lint dev src test

target/install-clojure:
	mkdir -p target
	curl https://download.clojure.org/install/linux-install-1.10.1.727.sh -o target/install-clojure
	chmod +x target/install-clojure

install/clojure: target/install-clojure
	sudo ./target/install-clojure

install: install/clojure

target: target/install-clojure

ci: install target env lint test

ci/local:
	act -P ubuntu-latest=nektos/act-environments-ubuntu:18.04

repl:
	clojure -Sdeps '{:deps {nrepl {:mvn/version "0.8.2"} cider/cider-nrepl {:mvn/version "0.23.0"} clj-kondo {:mvn/version "2020.04.05"}}}'  -m nrepl.cmdline --middleware "[cider.nrepl/cider-middleware]"