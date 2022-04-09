import { useContext } from "react";
import AuthContext from "../context/AuthProvider";

const useTokens = () => {
  const { tokens, setTokens } = useContext(AuthContext);
  return [tokens, setTokens] as const;
}

export default useTokens;