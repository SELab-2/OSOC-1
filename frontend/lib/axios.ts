import axios from 'axios';

const envURL: string =
  process.env.NEXT_PUBLIC_API_ENDPOINT || 'http://localhost:8080/api';
const BASEURL = envURL.replace(/\/$/, '');

/**
 * axios instance to be used for unauthenticated requests
 *
 * @remarks
 * this is only used for posting to user endpoint and interacting with login/token endpoint
 */
export default axios.create({
  baseURL: BASEURL,
});

/**
 * axios instance to be used for authenticated (JSON) requests
 */
export const axiosAuthenticatedBase = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * axios instance to be used for authenticated (JSON) requests
 */
// TODO fix this to inherit from axiosAuthenticatedBase so I can put the interceptors there
export const axiosAuthenticated = axios.create({
  baseURL: BASEURL,
  headers: {
    'Content-Type': 'application/json',
  },
});
