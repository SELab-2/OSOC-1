import { useRouter } from 'next/router';
import { FC, PropsWithChildren, useEffect, useState } from 'react';
import useEdition from '../hooks/useEdition';
import useRefreshToken from '../hooks/useRefreshToken';
import useTokens from '../hooks/useTokens';
import useUser from '../hooks/useUser';

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
const PersistLogin: FC<PropsWithChildren<unknown>> = ({
  children,
}: PropsWithChildren<unknown>) => {
  const [loading, setLoading] = useState(true);
  const refresh = useRefreshToken();
  const [tokens] = useTokens();
  const [, setEdition] = useEdition();
  const [, setUser] = useUser();
  const router = useRouter();

  useEffect(() => {
    /**
     * Try to refresh the refreshToken and accessToken if one is available in local storage.
     * Also does the same for current user and edition.
     */
    const verifyRefreshToken = async () => {
      try {
        if (typeof window !== 'undefined') {
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
      } catch (err) {
        // currently nothing happens here, but we can choose to
        // push users to the login page from here if that pleases us
      } finally {
        setLoading(false);
      }
    };

    tokens.accessToken ? setLoading(false) : verifyRefreshToken();
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    const signal = controller.signal;
    if (typeof window !== 'undefined') {
      window.addEventListener(
        'storage',
        (e) => {
          if (e.key === 'refreshToken' && !e.newValue) {
            // automatically logout (we only need to return to login because another browser window already did the rest)
            router.push('/login');
            controller.abort();
          }
        },
        { signal }
      );
    }
  }, []);

  return <>{loading ? undefined : children}</>;
};
export default PersistLogin;
