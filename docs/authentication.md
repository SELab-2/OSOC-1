### Authenticate with email and password

Send a POST request with the user's email and password to the api. The api will return an access token, a refresh token and the user object of the authenticated user. \
The access token is used for authorization, the refresh token is used to renew the access token without having to re-enter your email and password. \
```bash
curl --location --request POST 'http://localhost:8080/api/login' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'email=email here' \
--data-urlencode 'password=password here'
```

### Access resources using access token

Change the access token below after "Basic " by the access token you've received in the above section.
```bash
curl --location --request GET 'http://localhost:8080/api/students' \
--header 'Authorization: Basic eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTY0NzYxMzUyN30.9InLnBkodw4K9_gkE_eyzIGgOofLPnsrTjcS7bye81k'
```
Above cURL should execute successfully if the user you authenticated with is a Coach or Admin.
If you are not authenticated or authorized you will get a 401 HTTP status code.

### Renew access token using refresh token

Change the refresh token below after "refreshToken=" by the refresh token you've received after authentication.
```bash
curl --location --request POST 'http://localhost:8080/api/token/refresh' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'refreshToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0eW1lbi52YW5oaW1tZUB1Z2VudC5iZSIsImlzQWNjZXNzVG9rZW4iOmZhbHNlLCJleHAiOjE2NDg5NDY5NDAsImF1dGhvcml0aWVzIjpbIlJPTEVfRElTQUJMRUQiXX0.552E41DThqtriGjOpYMHFbe0pCNC8KP8bnb0S_MHzd8'
```