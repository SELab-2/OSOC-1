import FormContainer from '../components/FormContainer';
import Link from 'next/link';
import { FormEventHandler, useEffect, useRef, useState } from 'react';
import toast from 'react-hot-toast';
import { useRouter } from 'next/router';
import Endpoints from '../lib/endpoints';
import axios from '../lib/axios';
import useInput from '../hooks/useInput';
import { customPasswordRegex, emailRegex, nameRegex } from '../lib/regex';
import Head from 'next/head';

/**
 * Register page for OSOC application
 *
 * @returns Register Page
 */
const register = () => {
  const nameRef = useRef<HTMLInputElement>(null);

  const [name, resetName, nameProps] = useInput('');
  const [email, resetEmail, emailProps] = useInput('');
  const [password, resetPassword, passwordProps] = useInput('');
  const [match, resetMatch, matchProps] = useInput('');

  const nameError =
    "Name cannot contain numbers or any other symbols than ,.'- and spaces.";
  const emailError = 'Email must be a valid email address.';
  const passwordError =
    'Password must contain at least 8 characters and at most 64 characters.';
  const repeatPasswordError = 'Repeated password differs from password.';

  const [nameFocus, setNameFocus] = useState(true);
  const [emailFocus, setEmailFocus] = useState(false);
  const [passwordFocus, setPasswordFocus] = useState(false);
  const [repeatPasswordFocus, setRepeatPasswordFocus] = useState(false);

  const [validName, setValidName] = useState(true);
  const [validEmail, setValidEmail] = useState(true);
  const [validPassword, setValidPassword] = useState(true);
  const [validMatch, setValidMatch] = useState(true);

  useEffect(() => {
    nameRef?.current?.focus();
  }, []);

  useEffect(() => {
    setValidName(nameRegex.test(name));
  }, [name]);

  useEffect(() => {
    setValidEmail(emailRegex.test(email));
  }, [email]);

  useEffect(() => {
    setValidPassword(customPasswordRegex.test(password));

    setValidMatch(customPasswordRegex.test(match) && password === match);
  }, [password, match]);

  const router = useRouter();

  const registerUser: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    if (!(validName && validEmail && validPassword && validMatch)) return;

    const reqBody = {
      username: name,
      email,
      password,
    };

    const resetForm = () => {
      resetName();
      resetEmail();
      resetPassword();
      resetMatch();
    };

    try {
      await axios.post(Endpoints.USERS, JSON.stringify(reqBody), {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      resetForm();
      router.push('/login');
    } catch (err) {
      toast.error(
        'Unknown error while trying to create Account. Please try again later'
      );
    }
  };

  return (
    <>
      <Head>
        <title>Register</title>
      </Head>
      <FormContainer pageTitle="REGISTER">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={registerUser}>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Name
            <input
              className={`mt-1 box-border block h-8 w-full rounded border ${
                validName || name.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="name"
              type="text"
              placeholder="John Doe"
              ref={nameRef}
              {...nameProps}
              onFocus={() => setNameFocus(true)}
              onBlur={() => setNameFocus(false)}
            />
            {!validName && nameFocus && (
              <p className="mb-2 mt-1 inline-block w-full text-left text-sm font-medium text-red-600 opacity-90 hover:cursor-pointer">
                {nameError}
              </p>
            )}
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Email Address
            <input
              className={`mt-1 block h-8 w-full rounded border ${
                validEmail || email.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="email"
              type="email"
              placeholder="you@example.com"
              {...emailProps}
              onFocus={() => setEmailFocus(true)}
              onBlur={() => setEmailFocus(false)}
            />
            {!validEmail && emailFocus && (
              <p className="mb-2 mt-1 inline-block w-full text-left text-sm font-medium text-red-600 opacity-90 hover:cursor-pointer">
                {emailError}
              </p>
            )}
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Password
            <input
              className={`mt-1 block h-8 w-full rounded border ${
                validPassword || password.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="password"
              type="password"
              placeholder="password"
              minLength={8}
              maxLength={64}
              {...passwordProps}
              onFocus={() => setPasswordFocus(true)}
              onBlur={() => setPasswordFocus(false)}
            />
            {!validPassword && passwordFocus && (
              <p className="mb-2 mt-1 inline-block w-full text-left text-sm font-medium text-red-600 opacity-90 hover:cursor-pointer">
                {passwordError}
              </p>
            )}
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Repeat Password
            <input
              className={`mt-1 block h-8 w-full rounded border ${
                validMatch || match.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="repeatPassword"
              type="password"
              placeholder="password"
              minLength={8}
              maxLength={64}
              {...matchProps}
              onFocus={() => setRepeatPasswordFocus(true)}
              onBlur={() => setRepeatPasswordFocus(false)}
            />
            {!validMatch && repeatPasswordFocus && (
              <p className="mb-2 mt-1 inline-block w-full text-left text-sm font-medium text-red-600 opacity-90 hover:cursor-pointer">
                {repeatPasswordError}
              </p>
            )}
          </label>
          <button
            className="m-auto block rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 hover:brightness-95 lg:mb-3"
            type="submit"
          >
            Register
          </button>
          <Link href="/login">
            <p className="mt-2 inline-block text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              back to login
            </p>
          </Link>
        </form>
      </FormContainer>
    </>
  );
};

export default register;
