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
        <ul className="p-0 m-0">
          <li className={`sm:inline hover:underline ${ current_path === '/students' ? 'underline' : '' }`}>
            <Link href="/students">
            Select Students
            </Link>
          </li>
          <li className={`sm:inline ml-3 hover:underline ${ current_path === '/projects' ? 'underline' : '' }`}>
          <Link href="/projects">
            Projects
            </Link>
          </li>
          <li className={`sm:inline ml-3 hover:underline ${ current_path === '/users' ? 'underline' : '' }`}>
          <Link href="/users">
            Manage Users
            </Link>
          </li>
          <li className={`sm:inline ml-3 hover:underline`}>
            <Link href="/logout">
            Log Out
            </Link>
          </li>
        </ul>
      </nav>
    </header>
  )
}

export default Header;
