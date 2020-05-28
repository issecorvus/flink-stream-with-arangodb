Prerequisites
---------------
Sample data can also be found here (airports.csv, flights.csv):
https://github.com/arangodb/example-datasets

1) Install ArangoDB (https://www.arangodb.com/)...Create it with user: root and password: openSesame,
otherwise change the application.properties file in src/main/resources to reflect your own.
2) Install Airports data (located in src/main/resources/data of this project):
arangoimport --file <<path to airports.csv on your machine>> --collection airports --create-collection true --type csv
3) Install Flighs data:
arangoimport --file <<path to flights.csv on your machine>> --collection flights --create-collection true --type csv --create-collection-type edge 
4) Check the collections are there on http://localhost:8529

Building/Running
----------------
1) Build with mvn clean install -DskipTests (if database is up, mvn clean install)
2) Run with mvn spring-boot:run
3) Should print out the results of two windows similar to:
2020-05-28 17:21:07.762 INFO  [Legacy Source Thread - Source: direct-routes (1/1)] FLINK-STREAM - Using ArangoDBAllRouteSource
BIS -> DEN distance = 832
BIS -> DEN -> LAS distance = 1842
IAD -> JFK distance = 366


