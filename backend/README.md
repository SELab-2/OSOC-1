# Backend readme

## Error handling properties
from: https://reflectoring.io/spring-boot-exception-handling/
these are necessary to show the error messages in the response bodies of thrown exceptions.
```
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
server.error.include-exception=false
```

## Running tests
When running integration tests, Spring needs to load the entire application. \
This means Spring will also need a database to connect to. \
For this ```/backend/src/test/resources/application.properties``` is used. \
These properties are setup as used by github actions, to run tests locally change \
```spring.datasource.url=jdbc:postgresql://postgres:5432/osoc```
to ```spring.datasource.url=jdbc:postgresql://localhost:5432/osoc``` \
Then simply run the test command in /backend to run all tests.
```
./mvnw test
```
An overview of test success or error per test class can be found in ```backend/target/surefire-reports```. \
A coverage report can be found in ```backend/target/site/jacoco```. \
The jacoco.xml file gives an overview by method, the html files can be used 
to get a line based coverage report for every class in a human readable format. \
To remove old reports and force a rebuild you can run
```
./mvnw clean
```
#### Note on integration tests
Integration tests (using testcontainers) will not use /api in request paths.
They ignore ```server.servlet.context-path=/api```.
