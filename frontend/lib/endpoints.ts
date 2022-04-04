const envURL: string = process.env.NEXT_PUBLIC_API_ENDPOINT || '';
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
  USERS: baseURL + '/users',
  LOGIN: baseURL + '/login',
  REFRESH: baseURL + '/token/refresh'
} as const;

export default Endpoints;
