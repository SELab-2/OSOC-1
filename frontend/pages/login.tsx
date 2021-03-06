import Link from 'next/link';
import { useRouter } from 'next/router';
import { FormEventHandler, useEffect, useRef, useState } from 'react';
import toast from 'react-hot-toast';
import FormContainer from '../components/FormContainer';
import useTokens from '../hooks/useTokens';
import useUser from '../hooks/useUser';
import { Edition, UserRole } from '../lib/types';
import axios from '../lib/axios';
import Endpoints from '../lib/endpoints';
import usePersistentInput from '../hooks/usePersistentInput';
import useEdition from '../hooks/useEdition';
import Head from 'next/head';
import Info from '../components/Info';

/**
 * Login page for OSOC application
 *
 * @remarks
 * The login page sets the correct user and tokens in the {@link AuthProvider} on valid login and
 * it's context.
 *
 * @returns Login Page
 */
const Login = () => {
  const emailRef = useRef<HTMLInputElement>(null);

  const [email, , emailProps] = usePersistentInput('email', '');
  const [password, setPassword] = useState('');

  const [, setUser] = useUser();
  const [, setTokens] = useTokens();
  const [, setEdition] = useEdition();

  const router = useRouter();
  const message = (router.query.message as string) || '';

  useEffect(() => {
    emailRef?.current?.focus();
  }, []);

  const doSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();

    if (email && password) {
      try {
        const response = await axios.post(
          Endpoints.LOGIN,
          new URLSearchParams({
            email,
            password,
          }),
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
          }
        );

        if (response?.data) {
          const { accessToken, refreshToken, user } = response.data;
          setUser(user);
          setTokens({
            accessToken,
            refreshToken,
          });

          if (typeof window !== 'undefined') {
            if (refreshToken)
              localStorage.setItem('refreshToken', refreshToken);
            if (user) localStorage.setItem('user', JSON.stringify(user));
          }

          if (user.role === UserRole.Disabled) {
            router.push('/wait');
          } else {
            const response = await axios.get<Edition>(Endpoints.EDITIONACTIVE, {
              headers: { Authorization: `Basic ${accessToken}` },
            });
            if (response.data) {
              const editionName = response.data.name;
              setEdition(editionName);
              if (typeof window !== 'undefined' && editionName) {
                localStorage.setItem('edition', editionName);
              }
              router.push(`/${editionName}${Endpoints.PROJECTS}`);
            } else if (user.role === UserRole.Admin) {
              router.push(Endpoints.EDITIONS);
            } else {
              router.push('/users');
            }
          }
        } else {
          toast.error('Something went wrong trying to process the request.');
        }
      } catch (err) {
        console.log(err);
        toast.error('An error occurred while trying to log in.');
      }
    }
  };

  return (
    <>
      <Head>
        <title>Login</title>
      </Head>
      {message && (
        <Info
          info={message}
          className="absolute top-8 left-1/2 w-fit -translate-x-1/2 px-6"
        />
      )}
      <FormContainer pageTitle="LOGIN">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={doSubmit}>
          <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            Email Address
            <input
              className="mt-1 block h-8 w-full rounded border border-[#C4C4C4] p-1 text-sm"
              name="email"
              type="email"
              placeholder="you@example.com"
              {...emailProps}
              ref={emailRef}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            Password
            <Link href="/forgotPassword">
              <p className="float-right mt-2 inline-block text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
                Forgot password?
              </p>
            </Link>
            <input
              className="mt-1 block h-8 w-full rounded border border-[#C4C4C4] p-1 text-sm"
              name="password"
              type="password"
              placeholder="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>
          <button
            className="m-auto block rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 hover:brightness-95 lg:mb-4"
            type="submit"
          >
            Log in
          </button>
          <Link href="/register">
            <p className="mt-2 inline-block text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              no account yet? <br /> register here!
            </p>
          </Link>
        </form>
      </FormContainer>
    </>
  );
};
export default Login;
