#!/usr/bin/env bash
# Inject all example data
# Requires curl and jq https://stedolan.github.io/jq/

set -e # fail on error
set -x # echo on

echo "Declare the applications"
exterior_id=$(curl -H "Content-Type: application/json" -X POST -d '{"name": "exterior"}' http://localhost:8080/services/applications | jq '.id')
frontend1_id=$(curl -H "Content-Type: application/json" -X POST -d '{"name": "frontend1"}' http://localhost:8080/services/applications | jq '.id')
frontend2_id=$(curl -H "Content-Type: application/json" -X POST -d '{"name": "frontend2"}' http://localhost:8080/services/applications | jq '.id')
backend_id=$(curl -H "Content-Type: application/json" -X POST -d '{"name": "backend"}' http://localhost:8080/services/applications | jq '.id')

echo "Upload the swagger declarations"
curl -X POST -F "file=@./swagger/frontend1.json" "http://localhost:8080/services/applications/$frontend1_id/swagger"
curl -X POST -F "file=@./swagger/frontend2.json" "http://localhost:8080/services/applications/$frontend2_id/swagger"
curl -X POST -F "file=@./swagger/backend.json" "http://localhost:8080/services/applications/$backend_id/swagger"

echo "Upload the logs"
curl -X POST -F "file=@./apache/frontend1.log" "http://localhost:8080/services/applications/$frontend1_id/logs/apache"
curl -X POST -F "file=@./apache/frontend2.log" "http://localhost:8080/services/applications/$frontend2_id/logs/apache"
curl -X POST -F "file=@./apache/backend.log" "http://localhost:8080/services/applications/$backend_id/logs/apache"

echo "Check the result"
curl "http://localhost:8080/services/dependencies" | jq '.'
