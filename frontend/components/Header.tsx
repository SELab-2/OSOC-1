import Link from 'next/link';
import { useRouter } from 'next/router';
import { PropsWithChildren } from 'react';
import useUser from '../hooks/useUser';
import { UserRole } from '../lib/types';
import useEdition from '../hooks/useEdition';

type HeaderProps = PropsWithChildren<unknown>;

const Header: React.FC<HeaderProps> = () => {
  const router = useRouter();
  const [user] = useUser();
  const [edition] = useEdition();
  const current_path = router.pathname;

  return (
    <header className="flex h-fit w-full flex-col items-center justify-between px-4 shadow-lg sm:h-12 sm:flex-row">
      <div className="flex flex-row items-center">
        <img
          src="https://osoc.be/img/logo/logo-osoc-color.svg"
          className="hidden sm:inline-block sm:h-12 sm:w-12"
          alt="The OSOC logo"
        />
        <h2 className="ml-2 inline text-xl font-semibold text-osoc-blue">
          Selections
        </h2>
      </div>
      <nav className="text-center">
        <ul className="m-0 p-0">
          {edition && (
            <>
              <li
                className={`hover:underline sm:inline ${
                  current_path.endsWith('/students') ? 'underline' : ''
                }`}
              >
                <Link href={`/${edition}/students`}>Select Students</Link>
              </li>
              <li
                className={`ml-3 hover:underline sm:inline ${
                  current_path.endsWith('/projects') ? 'underline' : ''
                }`}
              >
                <Link href={`/${edition}/projects`}>Projects</Link>
              </li>
            </>
          )}
          <li
            className={`ml-3 hover:underline sm:inline ${
              current_path === '/users' ? 'underline' : ''
            }`}
          >
            <Link href="/users">Manage Users</Link>
          </li>

          {[UserRole.Admin].includes(user.role) ? (
            <li
              className={`ml-3 hover:underline sm:inline ${
                current_path === '/editions' ? 'underline' : ''
              }`}
            >
              <Link href="/editions">Manage Editions</Link>
            </li>
          ) : undefined}
          <li className={`ml-3 hover:underline sm:inline`}>
            <Link href="/logout">Log Out</Link>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
