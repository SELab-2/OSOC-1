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

  /* eslint-disable */
  const [email, resetEmail, emailProps] = usePersistentInput('email', '');
  const [password, setPassword] = useState('');

  const [, setUser] = useUser();
  const [, setTokens] = useTokens();
  const [, setEdition] = useEdition();

  const router = useRouter();

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
            // TODO this is a temporary fix
            const response = await axios.get<Edition>(Endpoints.EDITIONACTIVE, {
              headers: { Authorization: `Basic ${accessToken}` },
            });
            if (response) {
              const editionName = response.data.name;
              setEdition(editionName);
              if (typeof window !== 'undefined' && editionName) {
                localStorage.setItem('edition', editionName);
              }
            }
            router.push('/');
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
      <FormContainer pageTitle="LOGIN">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={doSubmit}>
          <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            Email Address
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="email"
              type="email"
              {...emailProps}
              ref={emailRef}
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
        </form>
      </FormContainer>
    </>
  );
};
export default Login;
