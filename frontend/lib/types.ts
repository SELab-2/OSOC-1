export type UUID = string;
export type AuthToken = string;

export type AuthTokens = {
  /**
   *  accessToken linked to the current user
   */
  accessToken: AuthToken;

  /**
   *  refreshToken linked to the current user
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
   * Email address of the user
   */
  email: string;

  /**
   * Current role of the user
   * @see {@link UserRole}
   */
  role: UserRole;
};
