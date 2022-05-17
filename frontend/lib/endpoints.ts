/**
 * A collection of all available endpoint URLs
 * {@label ENDPOINT_ENUM}
 */
enum Endpoints {
  USERS = '/users',
  LOGIN = '/login',
  REFRESH = '/token/refresh',
  EDITIONS = '/editions',
  EDITIONACTIVE = '/editions/active',
  PROJECTS = '/projects',
  STUDENTS = '/students',
  SKILLS = '/skills',
  FORGOTPASSWORD = '/forgotPassword',
  CONFLICTS = '/projects/conflicts',
}

export default Endpoints;
