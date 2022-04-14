#!/bin/python3
import requests
import random
from faker import Faker
fake = Faker()
login = requests.post('http://localhost:8080/api/login',
                      data={"email": "tester@mail.com", "password": "tester"}).json()
token = login["accessToken"]
testerid = login["user"]["id"]

authheaders = {'Authorization': f'Basic {token}',
               'Content-Type': 'application/json'}

# adding devs :)
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

studentsids = []
for _ in range(1000):
    studentsids.append(requests.post('http://localhost:8080/api/students',
                                     json={"firstName": fake.first_name(), "lastName": fake.last_name()}, headers=authheaders).json()["id"])

yes = requests.get('http://localhost:8080/api/students',
                   headers=authheaders, params={"pageNumber": 0, "pageSize": 50, "sortBy": "id"}).json()
no = requests.get('http://localhost:8080/api/students',
                  headers=authheaders, params={"pageNumber": 1, "pageSize": 50, "sortBy": "id"}).json()
maybe = requests.get('http://localhost:8080/api/students',
                     headers=authheaders, params={"pageNumber": 2, "pageSize": 50, "sortBy": "id"}).json()

# create 50 yes, no and maybe students
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
projects = []
for _ in range(10):
    projects.append(requests.post('http://localhost:8080/api/projects', json={
        "clientName": fake.company(), "name": fake.catch_phrase(), "description": fake.bs(), "positions": [{"skill": {"skillName": fake.job()}, "amount": random.randint(1, 7)} for _ in range(5)]}, headers=authheaders).json())

# users+coaches
users = []
for _ in range(25):
    users.append(requests.post('http://localhost:8080/api/osoc/users',
                               json={"username": fake.user_name(), "email": fake.ascii_company_email(), "password": "suuuuuperseeeeecret", "role": "Coach"}, headers=authheaders).json())
coachesids = []
for user in users:
    role = random.choice(["Disabled", "Coach", "Admin"])
    if role == "Disabled":
        continue
    if role == "Coach":
        coachesids.append(user["id"])
    requests.post(f'http://localhost:8080/api/users/{user["id"]}/role',
                  json=role, headers=authheaders)
# students to projects
for proj in projects:
    for stud in random.sample(yes, 4):
        requests.post(f'http://localhost:8080/api/projects/{proj["id"]}/assignments', json={
            "student": stud["id"], "position": random.choice(proj["positions"])["id"], "suggester": testerid, "reason": fake.paragraph(nb_sentences=4)}, headers=authheaders)
# communications to students
for studid in random.sample(studentsids, 250):
    requests.post(f'http://localhost:8080/api/communications/{studid}', json={
        "message": fake.paragraph(nb_sentences=4), "type": "Email"}, headers=authheaders)
# conflicts (force atleast 2 conflicts)
projects[0]
conflictstudid = random.choice(studentsids)
requests.post(f'http://localhost:8080/api/projects/{projects[0]["id"]}/assignments', json={
    "student": conflictstudid, "position": random.choice(projects[0]["positions"])["id"], "suggester": testerid, "reason": fake.paragraph(nb_sentences=4)}, headers=authheaders)
requests.post(f'http://localhost:8080/api/projects/{projects[1]["id"]}/assignments', json={
    "student": conflictstudid, "position": random.choice(projects[1]["positions"])["id"], "suggester": testerid, "reason": fake.paragraph(nb_sentences=4)}, headers=authheaders)
