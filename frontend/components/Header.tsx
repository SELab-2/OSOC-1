import Link from 'next/link';
import { useRouter } from 'next/router';
import { FC, PropsWithChildren } from 'react';
import useUser from '../hooks/useUser';
import { UserRole } from '../lib/types';
import useEdition from '../hooks/useEdition';

type HeaderProps = PropsWithChildren<unknown>;

type EditionHeaderLinkProps = HeaderLinkProps;

const EditionHeaderLink: FC<EditionHeaderLinkProps> = ({ href, children }) => {
  const [edition] = useEdition();

  return <HeaderLink href={`/${edition}${href}`}>{children}</HeaderLink>;
};

type HeaderLinkProps = PropsWithChildren<{
  href: string;
}>;

const HeaderLink: FC<HeaderLinkProps> = ({
  href,
  children,
}: HeaderLinkProps) => {
  const router = useRouter();
  const current_path = router.pathname;

  return (
    <li
      className={`ml-3 hover:underline sm:inline ${
        current_path.endsWith(href) ? 'underline' : ''
      }`}
    >
      <Link href={href}>{children}</Link>
    </li>
  );
};

const Header: React.FC<HeaderProps> = () => {
  const [user] = useUser();
  const [edition] = useEdition();

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
              <EditionHeaderLink href="/students">
                Select Students
              </EditionHeaderLink>
              <EditionHeaderLink href="/projects">Projects</EditionHeaderLink>
              <EditionHeaderLink href="/communications">
                Communications
              </EditionHeaderLink>
            </>
          )}
          <HeaderLink href="/users">Manage Users</HeaderLink>

          {[UserRole.Admin].includes(user.role) && (
            <HeaderLink href="/editions">Manage Editions</HeaderLink>
          )}
          <li className={`ml-3 hover:underline sm:inline`}>
            <Link href="/logout">Log Out</Link>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
