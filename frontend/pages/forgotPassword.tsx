import { NextPage } from 'next';
import { useRouter } from 'next/router';
import { FormEventHandler, useEffect, useRef } from 'react';
import toast from 'react-hot-toast';
import FormContainer from '../components/FormContainer';
import axios from '../lib/axios';
import Endpoints from '../lib/endpoints';
import usePersistentInput from '../hooks/usePersistentInput';

const ForgotPassword: NextPage = () => {
  const emailRef = useRef<HTMLInputElement>(null);

  /* eslint-disable */
  const [email, resetEmail, emailProps] = usePersistentInput('email', '');

  useEffect(() => {
    emailRef?.current?.focus();
  }, []);

  const router = useRouter();

  const doSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();

    if (email) {
      try {
        const response = await axios.post(Endpoints.FORGOTPASSWORD, email, {
          headers: { 'Content-Type': 'text/plain' },
        });

        toast.success(
          (t) => (
            <span>
              <b>Email sent</b> <br />
              An email has been sent to {email} <br />
              You might want to look in spam. <br />
              <button onClick={() => toast.dismiss(t.id)} className="okButton">
                OK
              </button>
            </span>
          ),
          { duration: 12000 }
        );
      } catch (err) {
        console.log(err);
        toast.error('An error occurred while trying to reset password.');
      }
    }
  };

  return (
    <>
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
