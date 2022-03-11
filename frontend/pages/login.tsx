import { getCsrfToken, getProviders, signIn } from 'next-auth/react';
import type { GetServerSideProps } from 'next';
import Link from 'next/link';

type Providers = {
  github: {
    name: string;
    id: string;
  };
};

// before sending page, it will acquire the csrftoken
export const getServerSideProps: GetServerSideProps = async (context) => {
  return {
    props: {
      csrfToken: await getCsrfToken(context),
      providers: await getProviders(),
    },
  };
};

const Login = ({
  csrfToken,
  providers,
}: {
  csrfToken: string | undefined;
  providers: Providers;
}) => {
  console.log(providers);
  return (
    <>
      <div className="h-screen bg-[url('../public/img/login.png')] bg-center">
        <div
          id="login-base"
          className="relative top-1/2 m-auto flex w-11/12 -translate-y-1/2 flex-col items-center rounded-md bg-[#F3F3f3]
                      px-4 py-4 text-center
                     md:w-11/12 md:rounded-[46px] lg:w-10/12"
        >
          <header className="pb-5">
            <h1 className="float-left text-3xl font-bold text-osoc-blue">
              Login
            </h1>
            <img
              src="https://osoc.be/img/logo/logo-osoc-color.svg"
              className="hidden h-16 w-16"
            />
          </header>
          {/* Callback to next-auth endpoint */}
          <form
            className="mb-1 w-11/12"
            method="post"
            action="/api/auth/callback/credentials"
          >
            {/* Necessary for next-auth credentials login */}
            <input name="csrfToken" type="hidden" defaultValue={csrfToken} />
            <label className="mb-4 block text-left">
              Email Address
              <input
                className="mt-1 block h-8 w-full p-1"
                name="email"
                type="email"
              />
            </label>
            <label className="mb-4 block text-left">
              Password
              <input
                className="mt-1 block h-8 w-full p-1"
                name="password"
                type="password"
              />
            </label>
            <button
              className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue"
              type="submit"
            >
              Log in
            </button>
          </form>
          <Link href="/register">
            <p className="text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              no account yet? <span className="block">register here!</span>
            </p>
          </Link>
          {/* Add line styling like in Figma */}
          <p className="pt-4 pb-2 text-sm opacity-80">Or log in using</p>
          {/* Github provider */}
          <button
            className="bg-[#302727] px-4 py-1 text-white"
            onClick={() => signIn(providers.github.id)}
          >
            <p className="text-right">{providers.github.name}</p>
          </button>
        </div>
      </div>
    </>
  );
};
export default Login;
