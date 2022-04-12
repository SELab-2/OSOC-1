import FormContainer from '../components/FormContainer';
import Link from 'next/link';
import { FormEventHandler, useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useRouter } from 'next/router';
import Endpoints from '../lib/endpoints';
import axios from '../lib/axios';
import useInput from '../hooks/useInput';
import { customPasswordRegex, emailRegex, nameRegex } from '../lib/regex';

/**
 * Register page for OSOC application
 *
 * @returns Register Page
 */
const register = () => {
  const [name, resetName, nameProps] = useInput('');
  const [email, resetEmail, emailProps] = useInput('');
  const [password, resetPassword, passwordProps] = useInput('');
  const [match, resetMatch, matchProps] = useInput('');

  // TODO: update the errors for the register form
  const [validName, setValidName] = useState(true);
  const [validEmail, setValidEmail] = useState(true);
  const [validPassword, setValidPassword] = useState(true);
  const [validMatch, setValidMatch] = useState(true);

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
      <FormContainer pageTitle="REGISTER">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={registerUser}>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Name
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validName || name.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="name"
              type="text"
              {...nameProps}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Email Address
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validEmail || email.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="email"
              type="email"
              {...emailProps}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Password
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validPassword || password.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="password"
              type="password"
              minLength={8}
              maxLength={64}
              {...passwordProps}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Repeat Password
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validMatch || match.length === 0
                  ? 'border-[#C4C4C4]'
                  : 'border-red-500'
              } p-1 text-sm`}
              name="repeatPassword"
              type="password"
              minLength={8}
              maxLength={64}
              {...matchProps}
            />
          </label>
          <button
            className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 lg:mb-3"
            type="submit"
          >
            Register
          </button>
          <Link href="/login">
            <p className="m-auto w-fit text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              back to login
            </p>
          </Link>
        </form>
      </FormContainer>
    </>
  );
};

export default register;
