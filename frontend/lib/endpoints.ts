const envURL: string =
  process.env.NEXT_PUBLIC_API_ENDPOINT || 'http://localhost:8080/api'; // TODO remove this before merging
const baseURL = envURL.replace(/\/$/, '');

/**
 * A collection of all available endpoint URLs
 * {@label ENDPOINT_ENUM}
 *
 * @remarks
 * Typescript doesn't allow computed enums, this is a little work-around to get the required functionality
 */
type Endpoints = typeof Endpoints[keyof typeof Endpoints];
const Endpoints = {
  BASEURL: baseURL,
  USERS: baseURL + '/users',
  LOGIN: '/login',
  REFRESH: '/token/refresh',
  PROJECTS: baseURL + '/projects',
  STUDENTS: baseURL + '/students',
} as const;

export default Endpoints;
