import { Context, createContext, Dispatch, PropsWithChildren, SetStateAction, useState } from "react";
import { AuthTokens, User, UserRole } from "../lib/types";

type ProviderProps = Record<string, unknown>;

type AuthContextProps = {
  /**
   * User object of the logged in user
   * 
   * @remarks
   * @see {@link User} for all fields
   */
  user: User;

  /**
   * React setState function to set user
   */
  setUser: Dispatch<SetStateAction<User>>;

  /**
   * Tokens object representing the access token and refresh token container
   * 
   * @remarks
   * @see {@link AuthTokens} for more information
   */
  tokens: AuthTokens;

  /**
   * React setState function to set tokens
   */
  setTokens: Dispatch<SetStateAction<AuthTokens>>;
}

const AuthContext: Context<AuthContextProps> = createContext({} as AuthContextProps);

/**
 * Provider object to expose authentication data to all components
 * @param children - the child components
 * 
 * @returns Provider component of the AuthContext
 */
export const AuthProvider = ({ children }: PropsWithChildren<ProviderProps>) => {
  const [user, setUser] = useState({
    id: '',
    username: '',
    email: '',
    role: UserRole.Disabled
  });
  const [tokens, setTokens] = useState({
    refreshToken: '',
    accessToken: ''
  });

  return (
    <AuthContext.Provider value={{ user, setUser, tokens, setTokens }}>
      { children }
    </AuthContext.Provider>
  )
}

export default AuthContext;

