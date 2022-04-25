<<<<<<< HEAD
import Link from "next/link";
import { useRouter } from "next/router";
import { PropsWithChildren } from "react";
=======
import Link from 'next/link';
import { useRouter } from 'next/router';
import { PropsWithChildren } from 'react';
>>>>>>> 0bf7868db3282b93bf44c09c64097b8fe62b0d1c

type HeaderProps = PropsWithChildren<unknown>;

const Header: React.FC<HeaderProps> = () => {
<<<<<<< HEAD

=======
>>>>>>> 0bf7868db3282b93bf44c09c64097b8fe62b0d1c
  const router = useRouter();
  const current_path = router.pathname;

  return (
<<<<<<< HEAD
    <header className="h-fit flex flex-col sm:flex-row shadow-lg sm:h-12 px-4 items-center w-full justify-between">
=======
    <header className="flex h-fit w-full flex-col items-center justify-between px-4 shadow-lg sm:h-12 sm:flex-row">
>>>>>>> 0bf7868db3282b93bf44c09c64097b8fe62b0d1c
      <div className="flex flex-row items-center">
        <img
          src="https://osoc.be/img/logo/logo-osoc-color.svg"
          className="hidden sm:inline-block sm:h-12 sm:w-12"
          alt="The OSOC logo"
        />
<<<<<<< HEAD
        <h2 className="inline ml-2 text-xl font-semibold text-osoc-blue">
=======
        <h2 className="ml-2 inline text-xl font-semibold text-osoc-blue">
>>>>>>> 0bf7868db3282b93bf44c09c64097b8fe62b0d1c
          Selections
        </h2>
      </div>
      <nav className="text-center">
<<<<<<< HEAD
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
          <li className={`sm:inline ml-3 hover:underline ${ current_path === '/editions' ? 'underline' : '' }`}>
            <Link href="/users">
              Manage Editions
            </Link>
          </li>
          <li className={`sm:inline ml-3 hover:underline`}>
            <Link href="/logout">
            Log Out
            </Link>
=======
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
          <li className={`ml-3 hover:underline sm:inline`}>
            <Link href="/logout">Log Out</Link>
>>>>>>> 0bf7868db3282b93bf44c09c64097b8fe62b0d1c
          </li>
        </ul>
      </nav>
    </header>
<<<<<<< HEAD
  )
}
=======
  );
};
>>>>>>> 0bf7868db3282b93bf44c09c64097b8fe62b0d1c

export default Header;
