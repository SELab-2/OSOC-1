import { axiosAuthenticated } from '../lib/axios';
import useTokens from './useTokens';
import useRefreshToken from './useRefreshToken';
import { useEffect } from 'react';
import { useMutex } from 'react-context-mutex';
import { useRouter } from 'next/router';

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
  const MutexRunner = useMutex();
  const mutex = new MutexRunner('myUniqueKey1');
  const router = useRouter();

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

    let refreshTokenPromise: Promise<string> | null = null;

    const responseIntercept = axiosAuthenticated.interceptors.response.use(
      (response) => response,
      async (error) => {
        const prevRequest = error?.config;
        if (
          UNAUTHORIZED_STATUSES.includes(error?.response?.status) &&
          !prevRequest?.sent
        ) {
          mutex.run(async () => {
            try {
              mutex.lock();
              prevRequest.sent = true;

              // check for an existing in-progress request
              if (refreshTokenPromise === null) {
                refreshTokenPromise = refresh()
                  .then((token) => {
                    refreshTokenPromise = null; // clear state
                    mutex.unlock();
                    return token; // resolve with the new token
                  })
                  .catch((errRefresh) => {
                    mutex.unlock();
                    return Promise.reject(errRefresh);
                  });
              }

              refreshTokenPromise
                .then((newAccessToken) => {
                  prevRequest.headers[
                    'Authorization'
                  ] = `Basic ${newAccessToken}`;
                  mutex.unlock();
                  return axiosAuthenticated(prevRequest);
                })
                .catch((errRefresh) => {
                  mutex.unlock();
                  // This error does not get caught unless we do it here
                  if (errRefresh.response?.status === 418) {
                    router.push('/login');
                    return;
                  }
                  return Promise.reject(errRefresh);
                });
            } catch (err: unknown) {
              mutex.unlock();
              return Promise.reject(err);
            }
          });
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
