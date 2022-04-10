export type UUID = string;
export type AuthToken = string;

export type AuthTokens = {
  /**
   *
   */
  accessToken: AuthToken;

  /**
   *
   */
  refreshToken: AuthToken;
};

export enum UserRole {
  Admin = 'Admin',
  Coach = 'Coach',
  Disabled = 'Disabled',
}

export type User = {
  /**
   * Unique identifier for the user
   */
  id: UUID;

  /**
   * Visible username of the user
   */
  username: string;

  /**
   * email address of the user
   */
  email: string;

  /**
   * current role of the user
   * @see {@link UserRole}
   */
  role: UserRole;
};
