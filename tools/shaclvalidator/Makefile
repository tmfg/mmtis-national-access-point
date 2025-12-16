SHELL:=/bin/bash
CLJ_SOURCES=$(shell find ../../ote/src -name '*.clj')

result: data.ttl
	@./validate.sh 2>&1 | rg -vi "not valid for datatype XSD date$$" &> result
	@echo "Result is now in ./result"

.PHONY: validate
validate:
	@rm -f ./data.ttl
	@$(MAKE) result

data.ttl: $(CLJ_SOURCES)
	@curl -s -o data.ttl http://localhost:3000/rdf

.PHONY: help
help:
	@echo -e "Usage:"
	@echo -e "\tmake help: show this help"
	@echo -e "\tmake: generate result file if clj files have changed"
	@echo -e "\tmake validate: force validation on fresh data"
