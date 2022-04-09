export type UUID = string;
export type AuthToken = string;

export type AuthTokens = {
  accessToken: AuthToken;
  refreshToken: AuthToken;
}

export enum UserRole {
  Admin = 'Admin',
  Coach = 'Coach',
  Disabled = 'Disabled'
}

export type User = {
  id: UUID;
  username: string;
  email: string;
  role: UserRole;
}