import Endpoints from './endpoints';
import HttpFetcher from './HttpFetcher';

type AuthToken = string | undefined;
type LoginResponse = {
  accessToken: string;
};

class OSOCWebApi {
  _accessToken: AuthToken;
  _refreshToken: AuthToken;

  constructor() {
    this._accessToken = undefined;
    this._refreshToken = undefined;
  }

  setRefreshToken(refreshToken: string) {
    this._refreshToken = refreshToken;
  }

  setAccessToken(accessToken: string) {
    this._accessToken = accessToken;
  }

  async registerUser(
    username: string,
    email: string,
    password: string
  ): Promise<void> {
    const requestBody = {
      username,
      email,
      password,
    };

    await HttpFetcher.postJSON({
      endpoint: Endpoints.USERS,
      body: requestBody,
    });
  }

  async login(email: string, password: string): Promise<LoginResponse> {
    const response = await HttpFetcher.postURLEncoded({
      endpoint: Endpoints.LOGIN,
      body: {
        email,
        password,
      },
    });

    const data = await response.json();
    return data;
  }
}

const webApi = new OSOCWebApi();

export default webApi;
