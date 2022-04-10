#!/bin/python3
import requests
from faker import Faker
import sys
accesstoken = sys.argv[1]
print(requests.get('http://localhost:8080/api/login',
                   data={"email": "tester@mail.com", "password": "tester"}).content)
# token=$(curl -s --location --request POST 'http://localhost:8080/api/login' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'email=tester@mail.com' --data-urlencode 'password=tester' | jq -r '.accessToken')
