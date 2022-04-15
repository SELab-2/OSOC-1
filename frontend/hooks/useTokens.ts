import { useContext } from "react";
import AuthContext from "../context/AuthProvider";

/**
 * Custom React hook to expose auth tokens
 * 
 * @returns [tokens, setTokens] tuple
 */
const useTokens = () => {
  const { tokens, setTokens } = useContext(AuthContext);
  return [tokens, setTokens] as const;
}

export default useTokens;