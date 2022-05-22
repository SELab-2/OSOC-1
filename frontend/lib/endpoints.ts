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
  COMMS = '/communications',
  SUGGESTIONS = '/suggestions',
  STATUS = '/status',
  CONFLICTS = '/projects/conflicts',
  INVITE = '/invite',
  LOGOUT = '/logout',
}

export default Endpoints;
