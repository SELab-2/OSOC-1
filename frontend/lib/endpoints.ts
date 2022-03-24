const envURL: string = process.env.NEXT_PUBLIC_API_ENDPOINT || '';
const baseURL = envURL.replace(/\/$/, '');

/**
 * Typescript doesn't allow computed enums, this is a little work-around to get the required functionality
 */
type Endpoints = typeof Endpoints[keyof typeof Endpoints];
const Endpoints = {
  USERS: baseURL + '/users',
  LOGIN: baseURL + '/login',
} as const;

export default Endpoints;
