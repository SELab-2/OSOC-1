#!/bin/python3
import requests
import sys
import random
from faker import Faker
login = requests.post('http://localhost:8080/api/login',
                      data={"email": "tester@mail.com", "password": "tester"}).json()
token = login["accessToken"]
testerid = login["user"]["id"]

authheaders = {'Authorization': f'Basic {token}',
               'Content-Type': 'application/json'}
# activate edition
requests.post('http://localhost:8080/api/editions',
              headers=authheaders, json="ed")
requests.post('http://localhost:8080/api/editions/ed/activate',
              headers=authheaders)
total = 1000
wsl = False
if "--wsl" in sys.argv or "-w" in sys.argv:
    wsl = True
    total = 100

if "--seed" in sys.argv or "-s" in sys.argv:
    Faker.seed(1)
fake = Faker()


def make_student():
    return {
        "eventId": "5f48bdc9-a717-4b7b-983b-db73ff4edec2",
        "eventType": "FORM_RESPONSE",
        "createdAt": "2022-03-05T10:14:14.383Z",
        "data": {
            "responseId": "nrG8oX",
            "submissionId": "nrG8oX",
            "respondentId": "mYd28v",
            "formId": "mRe24n",
            "formName": "#osoc22 student application form",
            "createdAt": "2022-03-05T10:14:14.000Z",
            "fields": [
                {
                    "key": "question_mO70dA",
                    "label": "Will you live in Belgium in July 2022?*",
                    "type": "MULTIPLE_CHOICE",
                    "value": "b8eb0e38-27a0-4acc-9b20-858b2f5b08ec",
                    "options": [
                        {
                            "id": "b8eb0e38-27a0-4acc-9b20-858b2f5b08ec",
                            "text": "Yes"
                        },
                        {
                            "id": "f10aa8b3-100e-4fa4-b301-21c300f11100",
                            "text": "No"
                        }
                    ]
                },
                {
                    "key": "question_mVz8vl",
                    "label": "Are you able to work 128 hours with a student employment agreement, or as a volunteer?*",
                    "type": "MULTIPLE_CHOICE",
                    "value": "0ac65328-5303-4231-9032-d076a9570d00",
                    "options": [
                        {
                            "id": "0ac65328-5303-4231-9032-d076a9570d00",
                            "text": "Yes, I can work with a student employment agreement in Belgium"
                        },
                        {
                            "id": "e65c96a6-2b58-4e97-9c5b-c5027d52c591",
                            "text": "Yes, I can work as a volunteer in Belgium"
                        },
                        {
                            "id": "342454c3-1047-40f9-b907-6dcc34eec683",
                            "text": "No – but I would like to join this experience for free"
                        },
                        {
                            "id": "879f708e-1417-4084-8435-12bb34e9a3fa",
                            "text": "No, I won’t be able to work as a student, as a volunteer or for free."
                        }
                    ]
                },
                {
                    "key": "question_nPz0v0",
                    "label": "Can you work during the month of July, Monday through Thursday (~09:00 to 17:00)?*",
                    "type": "MULTIPLE_CHOICE",
                    "value": "610e6281-6f52-4f89-9ca3-cda9259726ef",
                    "options": [
                        {
                            "id": "610e6281-6f52-4f89-9ca3-cda9259726ef",
                            "text": "Yes"
                        },
                        {
                            "id": "ec96ede4-f123-46e1-ac32-2897763d7f2c",
                            "text": "No, I wouldn't be able to work for the majority of days."
                        }
                    ]
                },
                {
                    "key": "question_3Ex0vL",
                    "label": "Are there any responsibilities you might have which could hinder you during the day?",
                    "type": "TEXTAREA",
                    "value": None if random.random() < 0.75 else fake.paragraph()
                },
                {
                    "key": "question_nroEGL",
                    "label": "Birth name",
                    "type": "INPUT_TEXT",
                    "value": fake.first_name()
                },
                {
                    "key": "question_w4KjAo",
                    "label": "Last name",
                    "type": "INPUT_TEXT",
                    "value": fake.last_name()
                },
                {
                    "key": "question_3jlx59",
                    "label": "Would you like to be called by a different name than your birth name?",
                    "type": "MULTIPLE_CHOICE",
                    "value": "f40dc91e-52be-4c2f-a8a7-d1188c4429c6",
                    "options": [
                        {
                            "id": "8b2e85e3-a380-4b3d-8205-2f25718f9d48",
                            "text": "Yes"
                        },
                        {
                            "id": "f40dc91e-52be-4c2f-a8a7-d1188c4429c6",
                            "text": "No"
                        }
                    ]
                },
                {
                    "key": "question_w2Kr1b",
                    "label": "How would you like to be called?",
                    "type": "INPUT_TEXT",
                    "value": None
                },
                {
                    "key": "question_3xJZ49",
                    "label": "What is your gender?",
                    "type": "MULTIPLE_CHOICE",
                    "value": "610c2d0b-a2fe-4ea6-9f5b-931fd5a9c184",
                    "options": [
                        {
                            "id": "504656c0-14d9-43a6-8d66-7d70afa1da99",
                            "text": "Female"
                        },
                        {
                            "id": "610c2d0b-a2fe-4ea6-9f5b-931fd5a9c184",
                            "text": "Male"
                        },
                        {
                            "id": "4fc6e5af-34af-4a8d-932f-0233b13744cd",
                            "text": "Transgender"
                        },
                        {
                            "id": "498a611f-14a4-4641-98be-4966bda834bf",
                            "text": "Rather not say"
                        }
                    ]
                },
                {
                    "key": "question_mZ2Jvv",
                    "label": "Would you like to add your pronouns?",
                    "type": "MULTIPLE_CHOICE",
                    "value": "787c763d-6cbb-46f8-a7e7-a0220445f443",
                    "options": [
                        {
                            "id": "787c763d-6cbb-46f8-a7e7-a0220445f443",
                            "text": "Yes"
                        },
                        {
                            "id": "c8f418b9-bafb-43fc-bf67-c4a357bb2e7d",
                            "text": "No"
                        }
                    ]
                },
                {
                    "key": "question_3N70Mb",
                    "label": "Which pronouns do you prefer?",
                    "type": "MULTIPLE_CHOICE",
                    "value": "5c4717f1-1c8d-40be-8cd4-ec38c516b52a",
                    "options": [
                        {
                            "id": "2cf8cab1-5365-47c8-a069-4e5a0ffa77fa",
                            "text": "she/her/hers"
                        },
                        {
                            "id": "38c63f9e-6603-4822-818a-b856fafd75cf",
                            "text": "he/him/his"
                        },
                        {
                            "id": "cfd4a5ed-c94c-4ad5-a9bd-b061b461a045",
                            "text": "they/them/theirs"
                        },
                        {
                            "id": "7f32b9b3-becd-4b3d-81e5-720a7f74021c",
                            "text": "ze/hir/hir "
                        },
                        {
                            "id": "5c4717f1-1c8d-40be-8cd4-ec38c516b52a",
                            "text": "by firstname"
                        },
                        {
                            "id": "16723d3f-7e56-4c74-ae84-d1e1b6ac7bfc",
                            "text": "by call name"
                        },
                        {
                            "id": "b952d73e-5558-4282-9912-b30f4ebb2307",
                            "text": "other"
                        }
                    ]
                },
                {
                    "key": "question_3qRPok",
                    "label": "Enter your pronouns",
                    "type": "INPUT_TEXT",
                    "value": None
                },
                {
                    "key": "question_wQ70vk",
                    "label": "What language are you most fluent in?",
                    "type": "MULTIPLE_CHOICE",
                    "value": "f66d2313-00b7-4f07-a339-35a99a6a7e66",
                    "options": [
                        {
                            "id": "f66d2313-00b7-4f07-a339-35a99a6a7e66",
                            "text": "Dutch"
                        },
                        {
                            "id": "9eee109d-3d8a-473b-8e49-2c14af178bd7",
                            "text": "English"
                        },
                        {
                            "id": "ede39a7b-2c6c-4a4a-902e-c6d3c79d9551",
                            "text": "French"
                        },
                        {
                            "id": "9cba69ec-bc24-4905-b9ef-e6f8c3040469",
                            "text": "German"
                        },
                        {
                            "id": "95083975-2d1f-43b3-b720-6d8aa50a742b",
                            "text": "Other"
                        }
                    ]
                },
                {
                    "key": "question_n97lp4",
                    "label": "What language are you most fluent in?",
                    "type": "INPUT_TEXT",
                    "value": None
                },
                {
                    "key": "question_meaEKo",
                    "label": "How would you rate your English?",
                    "type": "MULTIPLE_CHOICE",
                    "value": random.choice(["847b9bb9-6df8-4021-9ecf-e73ba7417929", "e2e0ca25-9540-4a9c-a7f7-ffcd0f4aa431",
                                            "ef5e1910-80af-4811-b493-813222ae4953", "f60a56c8-b04e-4841-9216-465617d27836",
                                            "847b9bb9-6df8-4021-9ecf-e73ba7417929"]),
                    "options": [
                        {
                            "id": "e2e0ca25-9540-4a9c-a7f7-ffcd0f4aa431",
                            "text": "★ I can understand your form, but it is hard for me to reply."
                        },
                        {
                            "id": "ef5e1910-80af-4811-b493-813222ae4953",
                            "text": "★★ I can have simple conversations."
                        },
                        {
                            "id": "f60a56c8-b04e-4841-9216-465617d27836",
                            "text": "★★★ I can express myself, understand people and get a point across."
                        },
                        {
                            "id": "847b9bb9-6df8-4021-9ecf-e73ba7417929",
                            "text": "★★★★ I can have extensive and complicated conversations."
                        },
                        {
                            "id": "f691d321-ad68-4a9a-8ea4-dad6b1322873",
                            "text": "★★★★★ I am fluent."
                        }
                    ]
                },
                {
                    "key": "question_nW80DQ",
                    "label": "Phone number",
                    "type": "INPUT_PHONE_NUMBER",
                    "value": fake.phone_number()
                },
                {
                    "key": "question_wa2GKy",
                    "label": "Your email address\n",
                    "type": "INPUT_EMAIL",
                    "value": fake.ascii_company_email()
                },
                {
                    "key": "question_m6ZxA5",
                    "label": "Upload your CV – size limit 10MB",
                    "type": "FILE_UPLOAD",
                    "value": [
                        {
                            "id": "31XdB1",
                            "name": "test.txt",
                            "url": "https://storage.googleapis.com/tally-response-assets/PAgpmN/e1398bf6-b2f9-4b8d-bd83-366e6961a376/test.txt",
                            "mimeType": "text/plain",
                            "size": 10
                        }
                    ]
                },
                {
                    "key": "question_w7NZ1z",
                    "label": "Or link to your CV",
                    "type": "INPUT_LINK",
                    "value": ""
                },
                {
                    "key": "question_wbWOKE",
                    "label": "Upload your portfolio – size limit 10MB",
                    "type": "FILE_UPLOAD",
                    "value": [
                        {
                            "id": "wMXNGk",
                            "name": "test.txt",
                            "url": "https://storage.googleapis.com/tally-response-assets/PAgpmN/c7cb136f-5833-4af8-bcaf-7882917f6cc0/test.txt",
                            "mimeType": "text/plain",
                            "size": 10
                        }
                    ]
                },
                {
                    "key": "question_wAB8AN",
                    "label": "Or link to your portfolio / GitHub",
                    "type": "INPUT_LINK",
                    "value": None
                },
                {
                    "key": "question_mBxBAY",
                    "label": "Upload your motivation – size limit 10MB",
                    "type": "FILE_UPLOAD",
                    "value": [
                        {
                            "id": "mJqpGK",
                            "name": "test.txt",
                            "url": "https://storage.googleapis.com/tally-response-assets/PAgpmN/e95ae107-1c51-4ebc-bb91-574a23f439c1/test.txt",
                            "mimeType": "text/plain",
                            "size": 10
                        }
                    ]
                },
                {
                    "key": "question_wkNZKj",
                    "label": "Or link to your motivation",
                    "type": "INPUT_LINK",
                    "value": None
                },
                {
                    "key": "question_wvP2E8",
                    "label": "Or write about your motivation",
                    "type": "TEXTAREA",
                    "value": None
                },
                {
                    "key": "question_mKV0vK",
                    "label": "Add a fun fact about yourself",
                    "type": "TEXTAREA",
                    "value": fake.paragraph()
                },
                {
                    "key": "question_wLP0v2",
                    "label": "What do/did you study?",
                    "type": "CHECKBOXES",
                    "value": [
                        "74bdb48e-5ea7-4fdc-b4bc-e7876e86e7db"
                    ],
                    "options": [
                        {
                            "id": "0d317375-0c1d-4f23-83a2-9e5496ffeeba",
                            "text": "Backend development"
                        },
                        {
                            "id": "d98ae09b-2fe0-46d0-8631-dc4dc52b5f5d",
                            "text": "Business management"
                        },
                        {
                            "id": "881ca718-17a1-4b07-b9fe-2680893c469c",
                            "text": "Communication Sciences"
                        },
                        {
                            "id": "74bdb48e-5ea7-4fdc-b4bc-e7876e86e7db",
                            "text": "Computer Sciences"
                        },
                        {
                            "id": "612b9bab-da2a-4e65-884d-6b765235c7da",
                            "text": "Design"
                        },
                        {
                            "id": "38e00fcb-aa5f-4e34-90f6-dbbd0cadd998",
                            "text": "Frontend development"
                        },
                        {
                            "id": "cb1494ec-0eef-4a60-a74f-b1e8100e6f42",
                            "text": "Marketing"
                        },
                        {
                            "id": "a88bd5d4-1b9b-43a5-ac83-f3a0c8a37f98",
                            "text": "Photography"
                        },
                        {
                            "id": "f5a34356-4aec-4631-b698-55e946cf7629",
                            "text": "Videography"
                        },
                        {
                            "id": "6e49c060-a2cb-493c-8b88-21cb7082dcca",
                            "text": "Other"
                        }
                    ]
                },
                {
                    "key": "question_wLP0v2_0d317375-0c1d-4f23-83a2-9e5496ffeeba",
                    "label": "What do/did you study? (Backend development)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_d98ae09b-2fe0-46d0-8631-dc4dc52b5f5d",
                    "label": "What do/did you study? (Business management)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_881ca718-17a1-4b07-b9fe-2680893c469c",
                    "label": "What do/did you study? (Communication Sciences)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_74bdb48e-5ea7-4fdc-b4bc-e7876e86e7db",
                    "label": "What do/did you study? (Computer Sciences)",
                    "type": "CHECKBOXES",
                    "value": True
                },
                {
                    "key": "question_wLP0v2_612b9bab-da2a-4e65-884d-6b765235c7da",
                    "label": "What do/did you study? (Design)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_38e00fcb-aa5f-4e34-90f6-dbbd0cadd998",
                    "label": "What do/did you study? (Frontend development)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_cb1494ec-0eef-4a60-a74f-b1e8100e6f42",
                    "label": "What do/did you study? (Marketing)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_a88bd5d4-1b9b-43a5-ac83-f3a0c8a37f98",
                    "label": "What do/did you study? (Photography)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_f5a34356-4aec-4631-b698-55e946cf7629",
                    "label": "What do/did you study? (Videography)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wLP0v2_6e49c060-a2cb-493c-8b88-21cb7082dcca",
                    "label": "What do/did you study? (Other)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_npDVRE",
                    "label": "What do/did you study?",
                    "type": "INPUT_TEXT",
                    "value": None
                },
                {
                    "key": "question_319EDL",
                    "label": "What kind of diploma are you currently going for?",
                    "type": "CHECKBOXES",
                    "value": [
                        random.choice(["8ab95749-ef8f-465b-a1f8-3152da88cf91",  "1d23a375-3645-40f6-b141-c00ad54625d3", "ef50be02-cee7-40d0-9378-f137ec0cb976",
                                      "1dd53b7e-6bcc-406c-9e97-a458fae9636f", "ffbbcf42-0489-4494-a75f-7701460ab7cd", "77ad76ce-20aa-43f4-8eac-07b1f5baf07c"])
                    ],
                    "options": [
                        {
                            "id": "1d23a375-3645-40f6-b141-c00ad54625d3",
                            "text": "A professional Bachelor"
                        },
                        {
                            "id": "8ab95749-ef8f-465b-a1f8-3152da88cf91",
                            "text": "An academic Bachelor"
                        },
                        {
                            "id": "ef50be02-cee7-40d0-9378-f137ec0cb976",
                            "text": "An associate degree"
                        },
                        {
                            "id": "1dd53b7e-6bcc-406c-9e97-a458fae9636f",
                            "text": "A master's degree"
                        },
                        {
                            "id": "ffbbcf42-0489-4494-a75f-7701460ab7cd",
                            "text": "Doctoral degree"
                        },
                        {
                            "id": "77ad76ce-20aa-43f4-8eac-07b1f5baf07c",
                            "text": "No diploma, I am self taught"
                        },
                        {
                            "id": "46da24d6-4b82-44be-91fd-5672d68a22de",
                            "text": "Other"
                        }
                    ]
                },
                {
                    "key": "question_319EDL_1d23a375-3645-40f6-b141-c00ad54625d3",
                    "label": "What kind of diploma are you currently going for? (A professional Bachelor)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_319EDL_8ab95749-ef8f-465b-a1f8-3152da88cf91",
                    "label": "What kind of diploma are you currently going for? (An academic Bachelor)",
                    "type": "CHECKBOXES",
                    "value": True
                },
                {
                    "key": "question_319EDL_ef50be02-cee7-40d0-9378-f137ec0cb976",
                    "label": "What kind of diploma are you currently going for? (An associate degree)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_319EDL_1dd53b7e-6bcc-406c-9e97-a458fae9636f",
                    "label": "What kind of diploma are you currently going for? (A master's degree)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_319EDL_ffbbcf42-0489-4494-a75f-7701460ab7cd",
                    "label": "What kind of diploma are you currently going for? (Doctoral degree)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_319EDL_77ad76ce-20aa-43f4-8eac-07b1f5baf07c",
                    "label": "What kind of diploma are you currently going for? (No diploma, I am self taught)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_319EDL_46da24d6-4b82-44be-91fd-5672d68a22de",
                    "label": "What kind of diploma are you currently going for? (Other)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_wME5v0",
                    "label": "What kind of diploma are you currently going for?",
                    "type": "INPUT_TEXT",
                    "value": None
                },
                {
                    "key": "question_mJO0X7",
                    "label": "How many years does your degree take?",
                    "type": "INPUT_NUMBER",
                    "value": 5
                },
                {
                    "key": "question_wg94YK",
                    "label": "Which year of your degree are you in?",
                    "type": "INPUT_TEXT",
                    "value": str(random.randint(0, 5))
                },
                {
                    "key": "question_3yJDjW",
                    "label": "What is the name of your college or university?",
                    "type": "INPUT_TEXT",
                    "value": random.choice(["UGent", "VUB", "KULeuven", "Hogent"])
                },
                {
                    "key": "question_3X4q1V",
                    "label": "Which role are you applying for?",
                    "type": "CHECKBOXES",
                    "value": [
                        random.choice(["19b68be9-6c2b-41f8-95f6-439cfb20f792", "2fd881eb-68de-4012-988d-7957de663c4f",
                                      "d27de5b9-3370-44e6-b114-60ddc243a4d8", "eb4b0022-4673-4f96-9c15-c01d870a253f",
                                       "f812f2d5-b438-49f4-9d95-0b415add300f", "3f34960d-1248-49ca-b6c7-fed702c73979",
                                       "5df0feb4-87ce-4767-bf99-092c27bc9b24",  "ee956527-6f34-479e-89a8-feb5e73d8979",
                                       "aa26de30-7ec2-4255-a949-0e5388dd58be"])
                    ],
                    "options": [
                        {
                            "id": "2fd881eb-68de-4012-988d-7957de663c4f",
                            "text": "Front-end developer"
                        },
                        {
                            "id": "19b68be9-6c2b-41f8-95f6-439cfb20f792",
                            "text": "Back-end developer"
                        },
                        {
                            "id": "d27de5b9-3370-44e6-b114-60ddc243a4d8",
                            "text": "UX / UI designer"
                        },
                        {
                            "id": "eb4b0022-4673-4f96-9c15-c01d870a253f",
                            "text": "Graphic designer"
                        },
                        {
                            "id": "f812f2d5-b438-49f4-9d95-0b415add300f",
                            "text": "Business Modeller"
                        },
                        {
                            "id": "3f34960d-1248-49ca-b6c7-fed702c73979",
                            "text": "Storyteller"
                        },
                        {
                            "id": "9bcb7761-3c86-4ea2-8abc-d45187a007ee",
                            "text": "Marketer"
                        },
                        {
                            "id": "5df0feb4-87ce-4767-bf99-092c27bc9b24",
                            "text": "Copywriter"
                        },
                        {
                            "id": "ee956527-6f34-479e-89a8-feb5e73d8979",
                            "text": "Video editor"
                        },
                        {
                            "id": "aa26de30-7ec2-4255-a949-0e5388dd58be",
                            "text": "Photographer"
                        },
                        {
                            "id": "ce472fdf-723c-4b94-bcad-a9136d0d8443",
                            "text": "Other"
                        }
                    ]
                },
                {
                    "key": "question_3X4q1V_2fd881eb-68de-4012-988d-7957de663c4f",
                    "label": "Which role are you applying for? (Front-end developer)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_19b68be9-6c2b-41f8-95f6-439cfb20f792",
                    "label": "Which role are you applying for? (Back-end developer)",
                    "type": "CHECKBOXES",
                    "value": True
                },
                {
                    "key": "question_3X4q1V_d27de5b9-3370-44e6-b114-60ddc243a4d8",
                    "label": "Which role are you applying for? (UX / UI designer)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_eb4b0022-4673-4f96-9c15-c01d870a253f",
                    "label": "Which role are you applying for? (Graphic designer)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_f812f2d5-b438-49f4-9d95-0b415add300f",
                    "label": "Which role are you applying for? (Business Modeller)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_3f34960d-1248-49ca-b6c7-fed702c73979",
                    "label": "Which role are you applying for? (Storyteller)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_9bcb7761-3c86-4ea2-8abc-d45187a007ee",
                    "label": "Which role are you applying for? (Marketer)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_5df0feb4-87ce-4767-bf99-092c27bc9b24",
                    "label": "Which role are you applying for? (Copywriter)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_ee956527-6f34-479e-89a8-feb5e73d8979",
                    "label": "Which role are you applying for? (Video editor)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_aa26de30-7ec2-4255-a949-0e5388dd58be",
                    "label": "Which role are you applying for? (Photographer)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_3X4q1V_ce472fdf-723c-4b94-bcad-a9136d0d8443",
                    "label": "Which role are you applying for? (Other)",
                    "type": "CHECKBOXES",
                    "value": False
                },
                {
                    "key": "question_w8Ze6o",
                    "label": "Which role are you applying for that is not in the list above?",
                    "type": "INPUT_TEXT",
                    "value": None
                },
                {
                    "key": "question_n0ePZQ",
                    "label": "Which skill would you list as your best one?",
                    "type": "INPUT_TEXT",
                    "value": "some skill"
                },
                {
                    "key": "question_wz7eGE",
                    "label": "Have you participated in osoc before?",
                    "type": "MULTIPLE_CHOICE",
                    "value": random.choice(["689451da-305b-451a-8039-c748ff06ec82", "41576a04-8f7a-4276-93b4-0dcc0c75bf0b"]),
                    "options": [
                        {
                            "id": "41576a04-8f7a-4276-93b4-0dcc0c75bf0b",
                            "text": "No, it's my first time participating in osoc"
                        },
                        {
                            "id": "689451da-305b-451a-8039-c748ff06ec82",
                            "text": "Yes, I have been part of osoc before"
                        }
                    ]
                },
                {
                    "key": "question_w5Z2eb",
                    "label": "Would you like to be a student coach this year?",
                    "type": "MULTIPLE_CHOICE",
                    "value": random.choice(["d2091172-9678-413a-bb3b-0d9cf6d5fa0b", "67613eae-b7fa-41f3-920d-3ccc1e58ea87"]),
                    "options": [
                        {
                            "id": "67613eae-b7fa-41f3-920d-3ccc1e58ea87",
                            "text": "No, I don't want to be a student coach"
                        },
                        {
                            "id": "d2091172-9678-413a-bb3b-0d9cf6d5fa0b",
                            "text": "Yes, I'd like to be a student coach"
                        }
                    ]
                }
            ]
        }
    }


studentsids = []
for _ in range(total):
    studentsids.append(requests.post('http://localhost:8080/api/ed/students',
                                     json=make_student(), headers=authheaders).json()["id"])
if wsl:
    login = requests.post('http://localhost:8080/api/login',
                          data={"email": "tester@mail.com", "password": "tester"}).json()
    token = login["accessToken"]
    authheaders = {'Authorization': f'Basic {token}',
                   'Content-Type': 'application/json'}

yes = requests.get('http://localhost:8080/api/ed/students',
                   headers=authheaders, params={"pageNumber": 0, "pageSize": total//20, "sortBy": "id"}).json()["collection"]
no = requests.get('http://localhost:8080/api/ed/students',
                  headers=authheaders, params={"pageNumber": 1, "pageSize": total//20, "sortBy": "id"}).json()["collection"]
maybe = requests.get('http://localhost:8080/api/ed/students',
                     headers=authheaders, params={"pageNumber": 2, "pageSize": total//20, "sortBy": "id"}).json()["collection"]

# create 50 yes, no and maybe students
for stud in yes:
    requests.post(
        f'http://localhost:8080/api/ed/students/{stud["id"]}/status', json="Yes", headers=authheaders)

for stud in no:
    requests.post(
        f'http://localhost:8080/api/ed/students/{stud["id"]}/status', json="No", headers=authheaders)

for stud in maybe:
    requests.post(
        f'http://localhost:8080/api/ed/students/{stud["id"]}/status', json="Maybe", headers=authheaders)

# create 10 random projects with 5 random positions
projects = []
for _ in range(10):
    projects.append(requests.post('http://localhost:8080/api/ed/projects', json={
        "clientName": fake.company(), "name": fake.catch_phrase(), "description": fake.bs(), "positions": [{"skill": {"skillName": fake.job()}, "amount": random.randint(1, 7)} for _ in range(5)]}, headers=authheaders).json())

# users+coaches
users = []
for _ in range(25):
    users.append(requests.post('http://localhost:8080/api/users',
                               json={"username": fake.user_name(), "email": fake.ascii_company_email(), "password": "suuuuuperseeeeecret", "role": "Coach"}, headers=authheaders).json())
coaches = []
for user in users:
    role = random.choice(["Disabled", "Coach", "Admin"])
    if role == "Disabled":
        continue
    if role == "Coach":
        coaches.append(user)
    requests.post(f'http://localhost:8080/api/users/{user["id"]}/role',
                  json=role, headers=authheaders)
# suggestions to students
for coach in coaches:
    coach_token = requests.post('http://localhost:8080/api/login',
                                data={"email": coach["email"], "password": "suuuuuperseeeeecret"}).json()["accessToken"]
    for studid in studentsids[:total//4]:
        requests.post(f'http://localhost:8080/api/ed/students/{studid}/suggestions', json={"suggester": f"http://localhost:8080/api/users/{coach['id']}", "status": random.choice(
            ["Yes", "No", "Maybe"]), "motivation": fake.paragraph(nb_sentences=4)}, headers={'Authorization': f'Basic {coach_token}', 'Content-Type': 'application/json'})
# students to projects
# coaches to projects
index = len(projects[0]["positions"][0]) - \
    projects[0]["positions"][0][::-1].index("/") - 1
for proj in projects:
    for stud in random.sample(yes, 4):
        requests.post(f'http://localhost:8080/api/ed/projects/{proj["id"]}/assignments', json={
            "student": stud["id"], "position": random.choice(proj["positions"])[index+1:], "suggester": testerid, "reason": fake.paragraph(nb_sentences=4)}, headers=authheaders)
    requests.post(
        f'http://localhost:8080/api/ed/projects/{proj["id"]}/coaches', headers=authheaders, json=random.choice(coaches)["id"])
# communications to students
for studid in random.sample(studentsids, total//4):
    requests.post(f'http://localhost:8080/api/ed/communications/{studid}', json={
        "message": fake.paragraph(nb_sentences=4), "type": "Email"}, headers=authheaders)
# conflicts (force atleast 2 conflicts)
conflictstudid = random.choice(studentsids)
requests.post(f'http://localhost:8080/api/ed/projects/{projects[0]["id"]}/assignments', json={
    "student": conflictstudid, "position": random.choice(projects[0]["positions"])[index+1:], "suggester": testerid, "reason": fake.paragraph(nb_sentences=4)}, headers=authheaders)
requests.post(f'http://localhost:8080/api/ed/projects/{projects[1]["id"]}/assignments', json={
    "student": conflictstudid, "position": random.choice(projects[1]["positions"])[index+1:], "suggester": testerid, "reason": fake.paragraph(nb_sentences=4)}, headers=authheaders)
