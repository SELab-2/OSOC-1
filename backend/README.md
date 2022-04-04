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
For this ```/backend/src/test/resources/application.properties``` is used. \
This is setup to automatically create and start docker containers, so make sure docker is installed. \
On windows this means docker desktop should be up and running. \
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
