import Link from "next/link";
import { useRouter } from "next/router";
import { PropsWithChildren } from "react";

type HeaderProps = PropsWithChildren<unknown>;

const Header: React.FC<HeaderProps> = () => {
  const router = useRouter();
  const current_path = router.pathname;

  return (
    <header className="h-fit flex flex-col sm:flex-row shadow-lg sm:h-12 px-4 items-center w-full justify-between">
      <div className="flex flex-row items-center">
        <img
          src="https://osoc.be/img/logo/logo-osoc-color.svg"
          className="hidden sm:inline-block sm:h-12 sm:w-12"
          alt="The OSOC logo"
        />
        <h2 className="inline ml-2 text-xl font-semibold text-osoc-blue">
          Selections
        </h2>
      </div>
      <nav className="text-center">
        <ul className="m-0 p-0">
          <li
            className={`hover:underline sm:inline ${
              current_path === '/students' ? 'underline' : ''
            }`}
          >
            <Link href="/students">Select Students</Link>
          </li>
          <li
            className={`ml-3 hover:underline sm:inline ${
              current_path === '/projects' ? 'underline' : ''
            }`}
          >
            <Link href="/projects">Projects</Link>
          </li>
          <li
            className={`ml-3 hover:underline sm:inline ${
              current_path === '/users' ? 'underline' : ''
            }`}
          >
            <Link href="/users">Manage Users</Link>
          </li>
          <li
            className={`ml-3 hover:underline sm:inline ${
              current_path === '/editions' ? 'underline' : ''
            }`}
          >
            <Link href="/editions">Manage Editions</Link>
          </li>
          <li className={`ml-3 hover:underline sm:inline`}>
            <Link href="/logout">Log Out</Link>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
