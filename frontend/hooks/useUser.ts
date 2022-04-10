import { useContext } from "react";
import AuthContext from "../context/AuthProvider";

/**
 * Custom React hook to expose logged in user object
 * 
 * @returns [user, setUser] tuple
 */
const useUser = () => {
  const { user, setUser } = useContext(AuthContext);
  return [user, setUser] as const;
}

export default useUser;