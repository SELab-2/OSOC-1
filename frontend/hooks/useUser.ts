import { useContext } from "react";
import AuthContext from "../context/AuthProvider";

const useUser = () => {
  const { user, setUser } = useContext(AuthContext);
  return [user, setUser] as const;
}

export default useUser;