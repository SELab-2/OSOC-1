import useTokens from './useTokens';
import axios from '../lib/axios';
import { AuthToken } from '../lib/types';
import Endpoints from '../lib/endpoints';

/**
 * Custom React hook exposing a refresh function to refresh access token and refresh token.
 * This refresh function also returns the newly received access token.
 *
 * @throws axios based error
 * @returns the function that can be called to refresh the tokens
 */
const useRefreshToken = () => {
  const [tokens, setTokens] = useTokens();

  const refresh: () => Promise<AuthToken> = async () => {
    let _refreshToken = tokens.refreshToken;

    if (!_refreshToken && typeof window !== 'undefined') {
      // Check to see if there is a refreshToken stored in localStorage when tokens are still null
      _refreshToken = localStorage.getItem('refreshToken') || '';
    }

    const response = await axios.post(
      Endpoints.REFRESH,
      new URLSearchParams({
        refreshToken: _refreshToken,
      }),
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      }
    );

    const refreshToken = response.data.refreshToken;

    if (typeof window !== 'undefined') {
      localStorage.setItem('refreshToken', refreshToken);
    }

    setTokens({
      accessToken: response.data.accessToken,
      refreshToken,
    });

    return response.data.accessToken;
  };

  return refresh;
};

export default useRefreshToken;
