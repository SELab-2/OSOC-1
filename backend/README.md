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
