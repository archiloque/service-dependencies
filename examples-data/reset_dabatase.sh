#!/usr/bin/env bash

# Reset database : drop the database, recreate it, and recreate the schema

set -e # fail on error
set -x # echo on

dropdb services-dependencies
createdb --owner=services-dependencies services-dependencies
cd ..
mvn install # build to ensure we have the latest migration file
java -jar target/services-dependencies-0.0.1-SNAPSHOT.jar db migrate services_dependencies.yml
