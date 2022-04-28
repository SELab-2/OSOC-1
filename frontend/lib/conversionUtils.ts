import {
  Answer,
  Assignment,
  Position,
  Project,
  ProjectBase,
  StatusSuggestion,
  Student,
  StudentBase,
  User,
} from './types';

/**
 * Function to convert a StudentBase object to a Student object
 * This will simply change all Url lists to empty lists of the correct type
 * Use this to avoid rendering errors
 *
 * @param studentBase - base student object as returned by get request
 */
export function convertStudentBase(studentBase: StudentBase): Student {
  const newStudent = {} as Student;
  newStudent.id = studentBase.id;
  newStudent.firstName = studentBase.firstName;
  newStudent.lastName = studentBase.lastName;
  newStudent.status = studentBase.status;
  newStudent.statusSuggestions = [] as StatusSuggestion[];
  newStudent.alumn = studentBase.alumn;
  newStudent.possibleStudentCoach = studentBase.possibleStudentCoach;
  newStudent.skills = studentBase.skills;
  newStudent.communications = []; // TODO set correct type once communication is implemented
  newStudent.answers = [] as Answer[];
  return newStudent as Student;
}

/**
 * Function to convert a ProjectBase object to a Project object
 * This will simply change all Url lists to empty lists of the correct type
 * Use this to avoid rendering errors
 *
 * @param projectBase - base project object as returned by a get request
 */
export function convertProjectBase(projectBase: ProjectBase): Project {
  const newProject = {} as Project;
  newProject.name = projectBase.name;
  newProject.id = projectBase.id;
  newProject.description = projectBase.description;
  newProject.clientName = projectBase.clientName;
  newProject.coaches = [] as User[];
  newProject.positions = [] as Position[];
  newProject.assignments = [] as Assignment[];
  return newProject as Project;
}
