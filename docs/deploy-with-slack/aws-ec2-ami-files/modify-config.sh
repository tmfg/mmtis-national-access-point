#!/bin/bash

set -e

sed -r -e 's/(:dev-mode?)([^}]*)/\1 false/g' \
-e '/.*:log.*/d' \
-e '/.*:ga.*/d' \
config.edn > config.edn.tmp && \

mv config.edn.tmp config.edn
