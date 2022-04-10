import axios from 'axios';

const envURL: string =
  process.env.NEXT_PUBLIC_API_ENDPOINT || 'http://localhost:8080';
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
export const axiosAuthenticated = axios.create({
  baseURL: BASEURL,
  headers: {
    'Content-Type': 'application/json',
  },
});
