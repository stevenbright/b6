
NBIN		      = ./node_modules/.bin
CASPER_ARGS   = --ignore-ssl-errors=yes --ssl-protocol=tlsv1 --pre=init/prepare.js --xunit=target/report.xml --stage=Prod

.PHONY: test

# Targets

all: test clean

node_modules:
	npm install --save-dev

target:
	mkdir target

clean:
	rm -rf target

test: export PHANTOMJS_EXECUTABLE = $(NBIN)/phantomjs
test: target node_modules
	$(NBIN)/casperjs $(CASPER_ARGS) test ./test/hello-twitter-test.js


