import { useContext } from 'react';
import AuthContext from '../context/AuthProvider';

/**
 * Custom React hook to expose current viewed edition
 *
 * @returns [edition, setEdition] tuple
 */
const useEdition = () => {
  const { edition, setEdition } = useContext(AuthContext);
  return [edition, setEdition] as const;
};

export default useEdition;
