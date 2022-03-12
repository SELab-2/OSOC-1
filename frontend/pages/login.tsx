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
  return (
    <>
      <div className="h-screen bg-[url('../public/img/login.png')] bg-center">
        {/* Left side images */}
        <div
          id="login-base"
          className="lg:rounded-5xl relative top-1/2 m-auto flex w-11/12 max-w-md -translate-y-1/2 flex-col items-center
                      rounded-md bg-[#F3F3f3] px-4 py-4 text-center
                     md:w-11/12 lg:grid lg:w-10/12 lg:max-w-7xl lg:grid-cols-2 lg:gap-2 xl:grid-cols-3"
        >
          <div className="hidden max-w-lg place-self-center xl:block">
            <img
              src="https://osoc.be/img/pictures/osoc17-1.jpg"
              alt="image of 4 people posing in front of a wall with post-its"
              className="object-scale-down shadow-sm shadow-gray-600 xl:mb-4"
            ></img>
            <img
              src="https://i0.wp.com/blog.okfn.org/files/2018/08/image3.jpg?fit=1200%2C800&ssl=1"
              alt="Group of people cheering on OSOC"
              className="object-scale-down shadow-sm shadow-gray-600"
            ></img>
          </div>
          {/* Main login component */}
          <div className="flex max-h-full max-w-full flex-col items-center justify-center">
            <header className="flex flex-row items-center justify-center gap-4 pb-5 lg:align-top">
              <h1 className="float-left text-3xl font-bold text-osoc-blue sm:text-4xl">
                LOGIN
              </h1>
              <img
                src="https://osoc.be/img/logo/logo-osoc-color.svg"
                className="hidden h-16 w-16 sm:inline-block md:h-24 md:w-24 lg:h-32 lg:w-32"
              />
            </header>
            {/* Callback to next-auth endpoint */}
            <form
              className="mb-1 w-11/12 max-w-md"
              method="post"
              action="/api/auth/callback/credentials"
            >
              {/* Necessary for next-auth credentials login */}
              <input name="csrfToken" type="hidden" defaultValue={csrfToken} />
              <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
                Email Address
                <input
                  className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
                  name="email"
                  type="email"
                />
              </label>
              <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
                Password
                <input
                  className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
                  name="password"
                  type="password"
                />
              </label>
              <button
                className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 lg:mb-4"
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
            <p className="pt-4 pb-2 text-sm font-medium opacity-80 lg:pb-4">
              Or log in using
            </p>
            {/* Github provider */}
            <button
              className="bg-[#302727] px-4 py-1 text-white shadow-sm shadow-gray-300"
              onClick={() => signIn(providers.github.id)}
            >
              <p className="text-right">{providers.github.name}</p>
            </button>
          </div>
          {/* Right side images */}
          <div className="hidden max-w-lg place-self-center lg:block">
            <img
              src="https://osoc.be/img/pictures/osoc17-2.jpg"
              alt="image of 4 people standing around a wall with post-its"
              className="object-scale-down shadow-sm shadow-gray-600 lg:mb-4"
            ></img>
            <img
              src="https://osoc.be/img/pictures/osoc17-3.jpg"
              alt="image of someone trying to give you a fistbump"
              className="object-scale-down shadow-sm shadow-gray-600"
            ></img>
          </div>
        </div>
      </div>
    </>
  );
};
export default Login;
