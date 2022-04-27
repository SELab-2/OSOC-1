import {
  Context,
  createContext,
  Dispatch,
  PropsWithChildren,
  SetStateAction,
  useState,
} from 'react';
import { AuthTokens, User } from '../lib/types';

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

  /**
   * name of currently opened edition
   */
  edition: string;

  /**
   * React setState function to set current edition
   */
  setEdition: Dispatch<SetStateAction<string>>;
};

const AuthContext: Context<AuthContextProps> = createContext(
  {} as AuthContextProps
);

/**
 * Provider object to expose authentication data to all components
 * @param children - the child components
 *
 * @returns Provider component of the AuthContext
 */
export const AuthProvider = ({
  children,
}: PropsWithChildren<ProviderProps>) => {
  const [user, setUser] = useState({} as User);
  const [tokens, setTokens] = useState({} as AuthTokens);
  const [edition, setEdition] = useState('');

  return (
    <AuthContext.Provider
      value={{ user, setUser, tokens, setTokens, edition, setEdition }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
