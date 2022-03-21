### Authenticate with username and password

Send a POST request with the user's username and password to the api. The api will return an accessToken and a refreshToken. \
The accessToken is used for authorization, the refreshToken is used to renew the accessToken without having to re-enter your username and password. \
At this moment the refreshToken isn't implemented yet.
```bash
curl --location --request POST 'http://localhost:8080/api/login' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'username=user' \
--data-urlencode 'password=pass'
```

### Access resources using accessToken

Change the accessToken below after "Basic " by the accessToken you've received in the above section.
```bash
curl --location --request GET 'http://localhost:8080/api/students' \
--header 'Authorization: Basic eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTY0NzYxMzUyN30.9InLnBkodw4K9_gkE_eyzIGgOofLPnsrTjcS7bye81k'
```
Above cURL should execute successfully, if you change GET to POST in the above command, then you need to be logged in as admin.
If you are not authenticated or authorized you will get a 403 HTTP status code.