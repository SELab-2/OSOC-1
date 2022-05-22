import {
  Answer,
  Assignment,
  Position,
  Project,
  ProjectBase,
  Skill,
  StatusSuggestion,
  StatusSuggestionCountList,
  Student,
  StudentBase,
  StudentBaseExtra,
  StudentBaseList,
  Url,
  User,
} from './types';

/**
 * Function to convert a StudentBase object to a Student object
 * This will simply change all Url lists to empty lists of the correct type
 * Use this to avoid rendering errors
 *
 * @param studentBase - base student object as returned by get request with view Full
 */
export function convertStudentBase(studentBase: StudentBase): Student {
  const newStudent = convertStudentAny(studentBase) as Student;
  newStudent.skills = studentBase.skills;
  return newStudent as Student;
}

/**
 * Function to convert a StudentBase object to a Student object
 * This will simply change all Url lists to empty lists of the correct type
 * Use this to avoid rendering errors
 *
 * @param studentBaseList - base student object as returned by get request with view List
 */
export function convertStudentBaseList(
  studentBaseList: StudentBaseList
): Student {
  return convertStudentAny(studentBaseList) as Student;
}

/**
 * Helper function since conversion difference between view List and view Full is only the skills
 *
 * @param studentAny - one of StudentBaseList or StudentBase
 */
function convertStudentAny(studentAny: StudentBaseList | StudentBase): Student {
  const newStudent = {} as Student;
  newStudent.id = studentAny.id;
  newStudent.firstName = studentAny.firstName;
  newStudent.lastName = studentAny.lastName;
  newStudent.status = studentAny.status;
  newStudent.statusSuggestions = [] as StatusSuggestion[];
  newStudent.alumn = studentAny.alumn;
  newStudent.possibleStudentCoach = studentAny.possibleStudentCoach;
  newStudent.skills = [] as Skill[];
  newStudent.communications = []; // WONTFIX does not get used set correct type once communication is implemented
  newStudent.answers = [] as Answer[];
  return newStudent as Student;
}

/**
 * Function to convert a StudentBase object to a Student object
 * This will simply change all Url lists to empty lists of the correct type
 * Use this to avoid rendering errors
 *
 * @param studentBaseList - base student object as returned by get request with view List
 */
export function convertStudentBaseListToExtra(
  studentBaseList: StudentBaseList
): StudentBaseExtra {
  const newStudent = {} as StudentBaseExtra;
  newStudent.id = studentBaseList.id;
  newStudent.firstName = studentBaseList.firstName;
  newStudent.lastName = studentBaseList.lastName;
  newStudent.assignments = [] as Url[];
  newStudent.skills = [] as Skill[];
  return newStudent as StudentBaseExtra;
}

/**
 * Function to convert a StudentBase object into a StudentBaseList object
 * Be aware that statusSuggestionsCount of the new object will simply be an empty object
 * This function is only used to avoid typing errors between components StudentView and StudentHolder
 *
 * @param studentBase - object to convert into a StudentBaseList object
 */
export function convertStudentFullToList(
  studentBase: StudentBase
): StudentBaseList {
  const newStudent = {} as StudentBaseList;
  newStudent.id = studentBase.id;
  newStudent.firstName = studentBase.firstName;
  newStudent.lastName = studentBase.lastName;
  newStudent.alumn = studentBase.alumn;
  newStudent.possibleStudentCoach = studentBase.possibleStudentCoach;
  newStudent.statusSuggestionCount = {} as StatusSuggestionCountList;
  newStudent.status = studentBase.status;
  return newStudent;
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
