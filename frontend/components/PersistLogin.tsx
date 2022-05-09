import { FC, useEffect, useState } from "react"
import useEdition from "../hooks/useEdition";
import useRefreshToken from "../hooks/useRefreshToken";
import useTokens from "../hooks/useTokens";
import useUser from "../hooks/useUser";

/**
 * Login Persistence component that tries to reestablish a previous session by setting refresh-/accesstoken, user and edition
 * 
 * @example
 * ```
 * const Page = () => {
 *   ...
 *  return (
 *    <>
 *      <PersistLogin>
 *        page content
 *      </PersistLogin>
 *    </>
 *  )
 * }
 * ```
 *
 *
 * @returns PersistLogin component
 */
const PersistLogin: FC = ({ children }) => {
  
  const [loading, setLoading] = useState(true);
  const refresh = useRefreshToken();
  const [tokens] = useTokens();
  const [, setEdition] = useEdition();
  const [, setUser] = useUser();

  useEffect(() => {

    /**
     * Try to refresh the refreshToken and accessToken if one is available in local storage.
     * Also does the same for current user and edition.
     */
    const verifyRefreshToken = async () => {
      try {
        
        if (typeof window !== "undefined") {
          const edition = localStorage.getItem('edition');
          const user = localStorage.getItem('user');

          if (edition) {
            setEdition(edition);
          }

          if (user) {
            setUser(JSON.parse(user));
          }

        }

        await refresh();
      } catch(err) {
        console.log(err);
      } finally {
        setLoading(false);
      }
    }

    tokens.accessToken ? setLoading(false) : verifyRefreshToken();
  }, []);

  return (
    <>
      {
        loading
        ? undefined
        : children
      }
    </>
  )
}
export default PersistLogin