#!/bin/bash


set -e


#sed -r -e 's/(:db)([^}]*)/\1 {:url "jdbc:postgresql://localhost:5432/napotetest"/g' config.edn
sed 's/.*:log.*//' config.edn
sed 's/.*:ga.*//' config.edn
