WIP

%h %{X-SERVICE-ORIGIN}i %{X-CORRELATION-ID}i %t \"%r\" %>s

curl -F "file=@./examples-data/apache/service1.log" -F "name=service1" "http://localhost:8080/logs/apache"

mvn install exec:java

 java -jar target/services-dependencies-0.0.1-SNAPSHOT.jar db status services_dependencies.yml
 java -jar target/services-dependencies-0.0.1-SNAPSHOT.jar db migrate services_dependencies.yml