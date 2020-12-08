PATH  := $(PATH):$(PWD)/bin:$(HOME)/miniconda3/bin
.PHONY: dev test

dev:
	clj -A:dev

env:
	#pip install numpy scipy
	#conda create -n numpy_clj_testenv python=3.8 numpy scipy=1.5.2
	conda install python=3.7 numpy scipy=1.5.2

test:
	clojure -M:core-matrix -m numpy-clj.compliance-test

target/install-clojure:
	mkdir -p target
	curl https://download.clojure.org/install/linux-install-1.10.1.727.sh -o target/install-clojure
	chmod +x target/install-clojure

install/clojure: target/install-clojure
	sudo ./target/install-clojure

install/conda:
	mkdir -p ~/miniconda3
	wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O ~/miniconda3/miniconda.sh
	bash ~/miniconda3/miniconda.sh -b -u -p ~/miniconda3
	rm -rf ~/miniconda3/miniconda.sh
	~/miniconda3/bin/conda init bash

install: install/clojure install/conda 

target: target/install-clojure

conda_config:
	conda config --set always_yes yes

ci: install conda_config target env test