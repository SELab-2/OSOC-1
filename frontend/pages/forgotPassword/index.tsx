import { NextPage } from 'next';
import { FormEventHandler, useEffect, useRef } from 'react';
import toast from 'react-hot-toast';
import FormContainer from '../../components/FormContainer';
import usePersistentInput from '../../hooks/usePersistentInput';
import axios from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import Head from 'next/head';

/**
 * When a user has forgotten its password, it can enter its email address in this page.
 * The user will then receive an email with a link to reset its password. This link will lead to {@link RESET_PASSWORD_PAGE}.
 *
 * @returns ForgotPassword page
 */
const ForgotPassword: NextPage = () => {
  /**
   * Find first input field.
   * */
  const emailRef = useRef<HTMLInputElement>(null);

  /**
   * Value of email input field.
   */
  const [email, , emailProps] = usePersistentInput('email', '');

  /**
   * Focus on first input field.
   */
  useEffect(() => {
    emailRef?.current?.focus();
  }, []);

  /**
   * Post entered email address to backend.
   * For security reasons, this page always shows a success toast, no matter if the given email address is valid or not.
   */
  const doSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();

    if (email) {
      try {
        await axios.post(Endpoints.FORGOTPASSWORD, email, {
          headers: { 'Content-Type': 'text/plain' },
        });

        toast.success(
          (t) => (
            <span>
              <b>Email sent</b> <br />
              An email has been sent to {email} <br />
              You might want to look in your spam folder. <br />
              <button onClick={() => toast.dismiss(t.id)} className="okButton">
                OK
              </button>
            </span>
          ),
          { duration: 12000 }
        );
      } catch (err) {
        toast.error('An error occurred while trying to reset password.');
      }
    }
  };

  return (
    <>
      <Head>Reset Password</Head>
      <FormContainer pageTitle="Password forgotten">
        <form className="mb-1 w-11/12 max-w-md" onSubmit={doSubmit}>
          <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            Email Address
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="email"
              type="email"
              {...emailProps}
              ref={emailRef}
              required
            />
          </label>
          <button
            className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 lg:mb-4"
            type="submit"
          >
            Change password
          </button>
        </form>
      </FormContainer>
    </>
  );
};

export default ForgotPassword;
