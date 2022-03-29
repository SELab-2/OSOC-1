import Endpoints from './endpoints';
import HttpFetcher from './HttpFetcher';

type UUID = string;

enum UserRole {
  Admin,
  Coach,
  Disabled
}

type AuthToken = string | undefined;
type LoginResponse = {
  accessToken: string;
};

type User = {
  id: UUID;
  name: string;
  email: string;
  role: UserRole;
}

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

  getAccessToken() {
    return this._accessToken;
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

  // TODO: Update with pagination
  async getUsers(): Promise<User[]> {
    const response = await HttpFetcher.get({
      endpoint: Endpoints.USERS,
      accessToken: this.getAccessToken()
    });

    const data = await response.json();

    return data;
  }

  async getUser(id: UUID): Promise<User> {
    const response = await HttpFetcher.get({
      endpoint: Endpoints.USERS + `/${id}`,
      accessToken: this.getAccessToken()
    });

    const data = await response.json();

    return data;
  }

  async updateUserRole(id: UUID, role: UserRole): Promise<User> {
    const response = await HttpFetcher.postJSON({
      endpoint: Endpoints.USERS + `/${id}/role`,
      body: role,
      accessToken: this.getAccessToken()
    });

    const data = await response.json();

    return data;
  }

  async deleteUser(id: UUID): Promise<unknown> {
    const response = await HttpFetcher.delete({
      endpoint: Endpoints.USERS + `/${id}`,
      accessToken: this.getAccessToken()
    });

    return response;
  }
}

const webApi = new OSOCWebApi();

export default webApi;
