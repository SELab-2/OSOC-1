import { Context, createContext, Dispatch, PropsWithChildren, SetStateAction, useState } from "react";
import { AuthTokens, User, UserRole } from "../lib/types";

type ProviderProps = Record<string, unknown>;

type AuthContextProps = {
  user: User;
  setUser: Dispatch<SetStateAction<User>>;
  tokens: AuthTokens;
  setTokens: Dispatch<SetStateAction<AuthTokens>>;
}

// @ts-ignore
const AuthContext: Context<AuthContextProps> = createContext({});

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

