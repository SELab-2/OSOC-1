import { useRouter } from 'next/router';
import { FC, PropsWithChildren, useEffect, useState } from 'react';
import useUser from '../hooks/useUser';
import { UserRole } from '../lib/types';

type RouteProtectionProps = PropsWithChildren<{
  /**
   * array of allowed roles that can visit this page passed as child component
   *
   * @see {@link UserRole}
   */
  allowedRoles: UserRole[];

  /**
   * The path to fall back to if the current page isn't allowed to be visited
   * @defaultValue redirect to login page
   */
  fallbackPath?: string;
}>;

/**
 * Route Protection component that needs to be used to protect certain pages from certain users
 *
 * @example
 * ```
 * const Page = () => {
 *   ...
 *  return (
 *    <>
 *      <RouteProtection
 *        allowedRoles = {[UserRole.ADMIN]}
 *      >
 *        page content
 *      </RouteProtection>
 *    </>
 *  )
 * }
 * ```
 *
 * @see {@link RouteProtectionProps}
 *
 * @returns RouteProtection component
 */
const RouteProtection: FC<RouteProtectionProps> = ({
  children,
  allowedRoles,
  fallbackPath,
}: RouteProtectionProps) => {
  const [loading, setLoading] = useState(true);
  const [user] = useUser();
  const router = useRouter();

  /**
   * Check to see if the current user is allowed to access this page.
   * If not, we push them to the login page or a given fallbackPath
   *
   * runs when `router` or `user` is updated
   */
  useEffect(() => {
    if (!user || !allowedRoles.includes(user.role)) {
      router.push(fallbackPath || '/login');
    } else {
      setLoading(false);
    }
  }, [user, router]);

  return <>{loading ? undefined : children}</>;
};

export default RouteProtection;
