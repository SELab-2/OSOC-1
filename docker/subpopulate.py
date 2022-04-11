#!/bin/python3
import requests
import random
from faker import Faker
fake = Faker()
token = requests.post('http://localhost:8080/api/login',
                      data={"email": "tester@mail.com", "password": "tester"}).json()["accessToken"]

# adding devs
authheaders = {'Authorization': f'Basic {token}',
               'Content-Type': 'application/json'}

requests.post('http://localhost:8080/api/students',
              json={"firstName": "Lars", "lastName": "Van Cauter"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})
requests.post('http://localhost:8080/api/students',
              json={"firstName": "Tom", "lastName": "Alard"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})
requests.post('http://localhost:8080/api/students',
              json={"firstName": "Maarten", "lastName": "Steevens"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})
requests.post('http://localhost:8080/api/students',
              json={"firstName": "Michael", "lastName": "Meuleman"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})
requests.post('http://localhost:8080/api/students',
              json={"firstName": "Jitse", "lastName": "Willaert"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})
requests.post('http://localhost:8080/api/students',
              json={"firstName": "Niels", "lastName": "Praet"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})
requests.post('http://localhost:8080/api/students',
              json={"firstName": "Tymen", "lastName": "Van Himme"}, headers={'Authorization': f'Basic {token}', 'Content-Type': 'application/json'})

for _ in range(1000):
    requests.post('http://localhost:8080/api/students',
                  json={"firstName": fake.first_name(), "lastName": fake.last_name()}, headers=authheaders)

yes = requests.get('http://localhost:8080/api/students',
                   headers=authheaders, params={"pageNumber": 0, "pageSize": 50, "sortBy": "id"}).json()
no = requests.get('http://localhost:8080/api/students',
                  headers=authheaders, params={"pageNumber": 0, "pageSize": 50, "sortBy": "firstName"}).json()
maybe = requests.get('http://localhost:8080/api/students',
                     headers=authheaders, params={"pageNumber": 0, "pageSize": 50, "sortBy": "lastName"}).json()

# create 50 yes, no and maybe students (maybe these overlap, but doesn't really matter)
for stud in yes:
    requests.post(
        f'http://localhost:8080/api/students/{stud["id"]}/status', json="Yes", headers=authheaders)

for stud in no:
    requests.post(
        f'http://localhost:8080/api/students/{stud["id"]}/status', json="No", headers=authheaders)

for stud in maybe:
    requests.post(
        f'http://localhost:8080/api/students/{stud["id"]}/status', json="Maybe", headers=authheaders)

# create 10 random projects with 5 random positions
for _ in range(10):
    print(requests.post('http://localhost:8080/api/projects', json={
        "clientName": fake.company(), "name": fake.catch_phrase(), "description": fake.bs(), "positions": [{"skill": {"skillName": fake.job()}, "amount": random.randint(1, 7)} for _ in range(5)]}, headers=authheaders).json())

# users+coaches
# suggestions to students
# students to projects
# communications to students
# conflicts


# print(requests.get('http://localhost:8080/api/students',
#                    headers=authheaders, params={"pageNumber": 0, "pageSize": 50, "sortBy": "id"}).json())
# print(requests.get('http://localhost:8080/api/students', headers=authheaders).json())
print(requests.get('http://localhost:8080/api/projects', headers=authheaders).json())
