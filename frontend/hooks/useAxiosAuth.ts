import { axiosAuthenticated } from '../lib/axios';
import useTokens from './useTokens';
import useRefreshToken from './useRefreshToken';
import { useEffect } from 'react';

const UNAUTHORIZED_STATUSES = [401, 403];

/**
 * Custom React hook to expose authenticated axios instance
 *
 * @remarks
 * Add a request interceptor to add the current accessToken to the authorization header
 * of every request made with this axios instance.
 *
 * Add a response interceptor to retry a failed authenticated request **once** with a refreshed
 * access token. If that still fails, we throw an error
 *
 * @throws an axios error
 *
 * @returns authenticated axios instance
 */
const useAxiosAuth = () => {
  const refresh = useRefreshToken();
  const [tokens] = useTokens();

  useEffect(() => {
    const requestIntercept = axiosAuthenticated.interceptors.request.use(
      (config) => {
        if (!config) {
          return;
        }

        if (!config.headers) {
          config.headers = {};
        }

        if (!config.headers['Authorization']) {
          config.headers['Authorization'] = `Basic ${tokens.accessToken}`;
        }

        return config;
      },
      (error) => Promise.reject(error)
    );

    const responseIntercept = axiosAuthenticated.interceptors.response.use(
      (response) => response,
      async (error) => {
        const prevRequest = error?.config;
        if (
          UNAUTHORIZED_STATUSES.includes(error?.response?.status) &&
          !prevRequest?.sent
        ) {
          prevRequest.sent = true;
          const newAccessToken = await refresh();
          prevRequest.headers['Authorization'] = `Basic ${newAccessToken}`;
          return axiosAuthenticated(prevRequest);
        }
        return Promise.reject(error);
      }
    );

    // Cleanup function to remove interceptors so that they don't stack up
    return () => {
      axiosAuthenticated.interceptors.request.eject(requestIntercept);
      axiosAuthenticated.interceptors.response.eject(responseIntercept);
    };
  }, [refresh, tokens]);

  return axiosAuthenticated;
};

export default useAxiosAuth;