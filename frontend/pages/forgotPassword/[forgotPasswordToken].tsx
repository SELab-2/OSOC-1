import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { FormEventHandler, useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import FormContainer from '../../components/FormContainer';
import useInput from '../../hooks/useInput';
import axios from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import { customPasswordRegex } from '../../lib/regex';
import Head from 'next/head';

/**
 * This page asks the user for its new password.
 *
 * {@label ENTER_PASSWORD_PAGE}
 *
 * @returns ForgotPassword page
 */
const ForgotPassword: NextPage = () => {
  /**
   * Get forgotPasswordToken from url.
   */
  const router = useRouter();
  const token = router.query.forgotPasswordToken as string;

  /**
   * Value of password input field.
   */
  const [password, , passwordProps] = useInput('');
  const [validPassword, setValidPassword] = useState(true);

  /**
   * Check if entered password is secure enough.
   */
  useEffect(() => {
    setValidPassword(customPasswordRegex.test(password));
  }, [password]);

  /**
   * Post entered password to backend if it is secure enough.
   *
   * @param e - event
   */
  const doSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    if (!validPassword) return;

    try {
      await axios.patch(Endpoints.FORGOTPASSWORD + '/' + token, password, {
        headers: { 'Content-Type': 'text/plain' },
      });

      toast.success(
        (t) => (
          <span>
            <b>Password reset</b> <br />
            Password has been reset. <br />
            <button
              onClick={() => {
                toast.dismiss(t.id);
                router.push('/login');
              }}
              className="okButton"
            >
              OK
            </button>
          </span>
        ),
        { duration: 12000 }
      );
    } catch (err) {
      toast.error('An error occurred while trying to reset password.');
    }
  };

  return (
    <>
      <Head>Reset Password</Head>
      <FormContainer pageTitle="Reset Password">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={doSubmit}>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            New Password
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
              required
            />
          </label>
          <button
            className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 hover:brightness-95 lg:mb-4"
            type="submit"
          >
            Change password
          </button>
          <Link href="/login">
            <p className="mt-2 text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              Go back to login
            </p>
          </Link>
        </form>
      </FormContainer>
    </>
  );
};

export default ForgotPassword;
