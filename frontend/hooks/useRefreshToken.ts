import useTokens from "./useTokens";
import axios from "../lib/axios";
import { AuthToken } from "../lib/types";
import Endpoints from "../lib/endpoints";

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
    const response = await axios.post(
      Endpoints.REFRESH, 
      new URLSearchParams({
        refreshToken: tokens.refreshToken
      }),
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    );

    setTokens({
      accessToken: response.data.accessToken,
      refreshToken: response.data.refreshToken
    });

    return response.data.accessToken;
  }

  return refresh;
}

export default useRefreshToken;