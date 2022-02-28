# Use cases
## Manage users
#### Sign up
| Use case           | Sign up                                                                                                             |
|--------------------|---------------------------------------------------------------------------------------------------------------------|
| Description        | Register a new user.                                                                                                |
| Actors             | The new user                                                                                                        |
| Pre-conditions     | User does not already exist                                                                                         |
| Normal flow        | 1) User fills out some basic information <br> 2) Submits that information <br> 3) New user is created by the system |
| Post-conditions    | The new user has been created                                                                                       |

### Admin only
#### Set account status(Administrator, Coach, Inactive)

| Use case            | Set account status                                                                                                                                                                           |
|---------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Description         | Set the status of a registered account to be administrator, coach or inactive.                                                                                                               |
| Actors              | Administrator and a user account.                                                                                                                                                            |
| Pre-conditions      | The user is logged in and is an administrator.                                                                                                                                               |
| Normal flow         | 1) The administrator indicates they wish to set the user status. <br> 2) The system requests confirmation. <br> 3) The admin confirms their request. <br> 4) The system changes user status. |
| Post-conditions     | The user status has been updated to match the request of the administrator.                                                                                                                  |
| Alternative flow 3A | The admin cancels the request: <br> User status doesn't change.                                                                                                                              |

#### Search user
| Use case           | Search user                                                                            |
|--------------------|----------------------------------------------------------------------------------------|
| Description        | Search for a user by their names                                                       |
| Actors             | An admin                                                                               |
| Pre-conditions     | The user is logged in and is an administrator.                                         |
| Normal flow        | 1) The admin types in a name <br> 2) Users with a partially matching name are returned |
| Post-conditions    | No data is changed                                                                     |

## Suggest students
#### Suggest student status(yes, maybe, no)
| Use case           | Suggest students                                                                                         |
|--------------------|----------------------------------------------------------------------------------------------------------|
| Description        | Suggest student status(yes, maybe, no) and a possible motivation                                         |
| Actors             | A coach or admin and the student for which we are giving a suggestion                                    |
| Pre-conditions     | The student exists                                                                                       |
| Normal flow        | 1) The coach/admin chooses yes, maybe or no <br> 2) They write a motivation(optional, can be blank) <br> |
| Post-conditions    | There now exists a suggestion for this student.                                                          |
#### Search student
| Use case           | Search student                                                                             |
|--------------------|--------------------------------------------------------------------------------------------|
| Description        | Search a student with a few possible filters.                                              |
| Actors             | Coach or admin                                                                             |
| Pre-conditions     |                                                                                            |
| Normal flow        | 1) The user searches with some possible filters <br> 2) The matching students are returned |
| Post-conditions    |                                                                                            |
#### Get student information
| Use case           | Get student information                                                                                      |
|--------------------|--------------------------------------------------------------------------------------------------------------|
| Description        | Retrieve information about a student                                                                         |
| Actors             | Coach or admin                                                                                               |
| Pre-conditions     | The student exists                                                                                           |
| Normal flow        | 1) Student information is requested <br> 2) Student information is returned and can be displayed in some way |
| Post-conditions    |                                                                                                              |
### Admin only
#### Remove student information(Bonus)
| Use case                  | Remove student                                                                                                                                 |
|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| Description               | Remove all information related to a student                                                                                                    |
| Actors                    | Admin and the student itself                                                                                                                   |
| Pre-conditions            | The student exists                                                                                                                             |
| Normal flow               | 1) A student requests their data to be removed <br> 2) An admin asks the system to remove the data <br> 3) The data is removed from the system |
| Post-conditions           | Data has been removed                                                                                                                          | 

#### Confirm student status(undecided, yes, maybe, no)
| Use case           | Confirm student status                                                                 |
|--------------------|----------------------------------------------------------------------------------------|
| Description        | An admin confirms the final status of a student before they are assigned to a project. |
| Actors             | Admin and the student                                                                  |
| Pre-conditions     | The student exists                                                                     |
| Normal flow        | 1) The admin selects the final status <br> 2) The admin confirms the status            |
| Post-conditions    | The final status for this user is applied after confirmation                           |
## Match students to project
#### Search project
| Use case           | Search project                                                       |
|--------------------|----------------------------------------------------------------------|
| Description        | Search project by name                                               |
| Actors             | Coach or admin                                                       |
| Pre-conditions     |                                                                      |
| Normal flow        | 1) The user types in the name <br> 2) Matching projects are returned |
| Post-conditions    |                                                                      |
#### Draft student for project
| Use case           | Draft student                                                                                    |
|--------------------|--------------------------------------------------------------------------------------------------|
| Description        | Assign a student to work a project                                                               |
| Actors             | Admin/coach, a student and the project itself                                                    |
| Pre-conditions     |                                                                                                  |
| Normal flow        | 1) The student is dragged onto a project <br> 2) The system assigns this student to the project. |
| Post-conditions    | The student is assigned to the selected project                                                  |
#### Remove student from project
| Use case           | Remove student                                                          |
|--------------------|-------------------------------------------------------------------------|
| Description        | Remove student from a project                                           |
| Actors             | Admin/coach, a student and the project itself                           |
| Pre-conditions     | A student is already assigned to a project.                             |
| Normal flow        | 1) A button is clicked <br> 2) The student is removed from this project |
| Post-conditions    | The student is removed from the project                                 |
#### Get student assignment conflicts
| Use case           | Get student assignment conflicts                                                         |
|--------------------|------------------------------------------------------------------------------------------|
| Description        | Get a list of users who are for example working on 2 different projects at the same time |
| Actors             | Admin/coach                                                                              |
| Pre-conditions     |                                                                                          |
| Normal flow        | 1) User clicks on "conflicts" button <br> 2) A list of conflicting users is returned     |
| Post-conditions    |                                                                                          |
#### Get project information
| Use case           | Get project information                                                                                |
|--------------------|--------------------------------------------------------------------------------------------------------|
| Description        | Return all information known about a project                                                           |
| Actors             | Admin/coach                                                                                            |
| Pre-conditions     | The project exists                                                                                     |
| Normal flow        | 1) The information is requested by clicking on the project <br> 2) The project information is returned |
| Post-conditions    |                                                                                                        |
### Admin only
#### Add new project and partner
| Use case           | Add new project and partner                                                                                                                    |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| Description        | Add a project from a partner alongside some information                                                                                        |
| Actors             | Admin                                                                                                                                          |
| Pre-conditions     |                                                                                                                                                |
| Normal flow        | 1) The user enters project/partner information <br> 2) This information is added into the system and users can now be assigned to this project |
| Post-conditions    | The project is added into the system ready to have students be assigned to it                                                                  |

#### Assign a coach to a project
| Use case           | Assign coach                                                                                                                   |
|--------------------|--------------------------------------------------------------------------------------------------------------------------------|
| Description        | Assign a coach to a project(a project is allowed to have multiple coaches)                                                     |
| Actors             | Admin, project                                                                                                                 |
| Pre-conditions     | The project and coach exist and the coach is not already assigned to this project                                              |
| Normal flow        | 1) The admin clicks a button to add a coach <br> 2) The admin selects a coach <br> 3) The system adds the coach to the project |
| Post-conditions    | The coach is now assigned to this project                                                                                      |

## Communication
#### Indicate sent confirmation/rejection/maybe email
| Use case           | Indicate sent confirmation/rejection/maybe email                                                                                                                                 |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Description        | Indicate that you have sent an email to a student                                                                                                                                |
| Actors             | Admin/coach, student                                                                                                                                                             |
| Pre-conditions     |                                                                                                                                                                                  |
| Normal flow        | 1) Indicate the type of email you have sent to a specific student <br> 2) This email is logged into some kind of student logbook that shows all communications with that student |
| Post-conditions    | The email is logged into a list of communications with that student. This allows other admins to know that emails where already sent.                                            |

#### Get confirmation/rejection/maybe emails
| Use case           | Get status email list                                                                             |
|--------------------|---------------------------------------------------------------------------------------------------|
| Description        | Get a list of communications with a student.                                                      |
| Actors             | Admin/coach                                                                                       |
| Pre-conditions     | The student exists                                                                                |
| Normal flow        | 1) The admin/coach requests a log of all communications with a student <br> 2) A list is returned |
| Post-conditions    |                                                                                                   |

## Edition management
#### Create new edition
| Use case           | Create new edition                                                                                                |
|--------------------|-------------------------------------------------------------------------------------------------------------------|
| Description        | Create a new edition for this year                                                                                |
| Actors             | Admin, and all data                                                                                               |
| Pre-conditions     |                                                                                                                   |
| Normal flow        | 1) An admin requests to create a new edition <br> 2) All data from the previous edition besides admins is removed |
| Post-conditions    | All students, coaches and projects have been removed, new projects and students can now be added.                 |
