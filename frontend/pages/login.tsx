import Link from 'next/link';
import { useRouter } from 'next/router';
import { FormEventHandler, useState } from 'react';
import toast from 'react-hot-toast';
import FormContainer from '../components/FormContainer';
import useTokens from '../hooks/useTokens';
import useUser from '../hooks/useUser';
import { UserRole } from '../lib/types';
import axios from '../lib/axios';

const LOGIN_URL = '/login';

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
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [, setUser] = useUser();
  const [, setTokens] = useTokens();

  const router = useRouter();

  const doSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();

    if (email && password) {
      try {
        const response = await axios.post(
          LOGIN_URL,
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

          if (user.role === UserRole.Disabled) {
            router.push('/wait');
          } else {
            router.push('/');
          }
        } else {
          toast.error('Something went wrong trying to process the request.');
        }
      } catch (err) {
        toast.error('An error occurred while trying to log in.');
      }
    }
  };

  return (
    <>
      <FormContainer pageTitle="LOGIN">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={doSubmit}>
          <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            Email Address
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            Password
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>
          <button
            className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 lg:mb-4"
            type="submit"
          >
            Log in
          </button>
          <Link href="/register">
            <p className="mt-2 text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              no account yet? <br /> register here!
            </p>
          </Link>
          <p className="hr-sect pt-4 pb-2 text-sm font-medium opacity-80 lg:pb-4">
            Or log in using
          </p>
          {/* Github provider. Right now, this doesn't work*/}
          <button
            className="bg-[#302727] px-4 py-1 text-white shadow-sm shadow-gray-300"
            onClick={() => 'click'}
            disabled={true}
          >
            <p className="text-right">Github</p>
          </button>
        </form>
      </FormContainer>
    </>
  );
};
export default Login;
