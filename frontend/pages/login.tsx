import { getCsrfToken, getProviders, signIn } from 'next-auth/react';
import type { GetServerSideProps } from 'next';
import Link from 'next/link';
import FormContainer from '../components/FormContainer';

/**
 * a Providers type to use in the OAuth2 buttons, this will eventually be removed when
 * authentication is fixed
 *
 * @see {@link https://next-auth.js.org/configuration/providers/oauth | NextAuth Providers}
 */
type Providers = {
  github: {
    name: string;
    id: string;
  };
};

/**
 * NextJS SSR function to load required props into the React component
 * {@label LoginSSR}
 *
 * @remarks
 * Before sending the page, it will acquire the csrftoken and the providers.
 * The providers here are the OAuth2 providers, this will be removed eventually.
 * The csrfToken is necessary for the credentials login form.
 *
 * @see {@link https://nextjs.org/docs/basic-features/data-fetching/get-server-side-props | GetServerSideProps function}
 * @see {@link https://next-auth.js.org/configuration/providers/credentials | Credentials Login}
 * @see {@link https://next-auth.js.org/configuration/providers/oauth | NextAuth Providers}
 *
 * @param context - Next application context
 * @returns server-side props csrfToken and providers
 */
export const getServerSideProps: GetServerSideProps = async (context) => {
  return {
    props: {
      csrfToken: await getCsrfToken(context),
      providers: await getProviders(),
    },
  };
};

/**
 * Login page for OSOC application
 *
 * @see {@link LoginSSR | Login Server-Side Rendering props}
 * @returns Login Page
 */
const Login = ({
  csrfToken,
  providers,
}: {
  csrfToken: string | undefined;
  providers: Providers;
}) => {
  return (
    <>
      <FormContainer pageTitle="LOGIN">
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
          <Link href="/register">
            <p className="text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              no account yet? <span className="block">register here!</span>
            </p>
          </Link>
          {/* TODO: Add line styling like in Figma */}
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
        </form>
      </FormContainer>
    </>
  );
};
export default Login;
