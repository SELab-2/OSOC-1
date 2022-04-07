import { uniqueAtom } from ".";
import { User, UserRole } from "../lib/OSOCWebApi";

export const userAtom = uniqueAtom<User>({
  name: 'userState',
  defaultValue: {
    id: '',
    username: '',
    email: '',
    role: UserRole.Disabled
  }
});

export const tokenAtom = uniqueAtom<{
  refreshToken: string;
  accessToken: string;
  accessTokenTTL: Date;
}>({
  name: 'tokenState',
  defaultValue: {
    refreshToken: '',
    accessToken: '',
    accessTokenTTL: new Date(Date.now())
  }
});