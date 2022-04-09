import Endpoints from './endpoints';
import HttpFetcher, { NamedError } from './HttpFetcher';
import { User, UserRole, UUID } from './types';

type AuthToken = string | undefined;
type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  accessTokenTTL: Date;
  user: User;
};

export class InvalidRefreshTokenError extends NamedError {};

class OSOCWebApi {
  _accessToken: AuthToken;
  _refreshToken: AuthToken;
  _accessTokenTTL: Date | undefined;

  constructor() {
    this._accessToken = undefined;
    this._refreshToken = undefined;
    this._accessTokenTTL = undefined;
  }

  setRefreshToken(refreshToken: string) {
    this._refreshToken = refreshToken;
  }

  setAccessToken(accessToken: string) {
    this._accessToken = accessToken;
  }

  setAccessTokenTTL(accessTokenTTL: Date) {
    this._accessTokenTTL = accessTokenTTL;
  }

  getRefreshToken(): AuthToken {
    return this._refreshToken;
  }

  getAccessToken(): AuthToken {

    // Check if access token is still valid

    // if so, return the access token
    return this._accessToken;
    
    // otherwise, refresh the access token and refresh token
    this.refreshAuthTokens(this.getRefreshToken() as string);

    // if that fails, return to login (somehow, or throw a specific error like InvalidRefreshToken)
  }

  setupFields(accessToken: string, refreshToken: string, accessTokenTTL: Date) {
    this.setAccessToken(accessToken);
    this.setRefreshToken(refreshToken);
    this.setAccessTokenTTL(accessTokenTTL);
  }


  async refreshAuthTokens(refreshToken: string) {
    const requestBody = {
      refreshToken
    };

    const response = await HttpFetcher.postURLEncoded({
      endpoint: Endpoints.REFRESH,
      body: requestBody
    });

    if (response.ok) {
      const data = await response.json();
      const { accessToken, refreshToken } = data;

      this.setAccessToken(accessToken);
      this.setRefreshToken(refreshToken);
    } else {
      throw new InvalidRefreshTokenError();
    }
  }

  /**
   * Registers a new user to the underlying database
   * 
   * @param username - the name of the new user
   * @param email - the email of the new user
   * @param password - the password of the user, in plaintext
   * 
   */
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

  /**
   * login using a valid email and password combination
   * 
   * @param email - email of the user that want's to log in
   * @param password - corresponding password of the user
   * @returns an object containing an accessToken, refreshToken
   */
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

  /**
   * get all the users from the unnderlying database
   * 
   * @returns a list containing all the user in User objects
   */
  async getUsers(): Promise<User[]> {
    const response = await HttpFetcher.get({
      endpoint: Endpoints.USERS,
      accessToken: this.getAccessToken()
    });

    const data = await response.json();

    return data;
  }

  /**
   * find a certain user by its id
   * 
   * @param id - the identifier of the user to search for
   * @returns the User object that is identified by the id
   */
  async getUser(id: UUID): Promise<User> {
    const response = await HttpFetcher.get({
      endpoint: Endpoints.USERS + `/${id}`,
      accessToken: this.getAccessToken()
    });

    const data = await response.json();

    return data;
  }

  /**
   * update the role of the user by its id
   * 
   * @param id - the identifier of the user to update
   * @param role - the new role to assign
   * @returns the updated user object
   */
  async updateUserRole(id: UUID, role: UserRole): Promise<User> {
    const response = await HttpFetcher.postJSON({
      endpoint: Endpoints.USERS + `/${id}/role`,
      body: role,
      accessToken: this.getAccessToken()
    });

    const data = await response.json();

    return data;
  }

  /**
   * delete the user by its id
   * 
   * @param id - the identifier of the user to delete
   */
  async deleteUser(id: UUID) {
    await HttpFetcher.delete({
      endpoint: Endpoints.USERS + `/${id}`,
      accessToken: this.getAccessToken()
    });

  }
}

const webApi = new OSOCWebApi();

export default webApi;
