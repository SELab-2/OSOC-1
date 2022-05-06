import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { FormEventHandler, useEffect, useRef, useState } from 'react';
import toast from 'react-hot-toast';
import FormContainer from '../../components/FormContainer';
import axios from '../../lib/axios';
import Endpoints from '../../lib/endpoints';

const ResetPassword: NextPage = () => {
  const router = useRouter();
  const token = router.query.resetPasswordToken as string;

  const [password, setPassword] = useState('');

  const doSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();

    if (password) {
      try {
        const response = await axios.patch(
          Endpoints.RESETPASSWORD + "/" + token,
          password,
          {
            headers: { 'Content-Type' : 'text/plain' }
          }
        );
        // router.push('/');
        if (response?.data) {
          toast.success(
            (t) => (
              <span>
                <b>Password reset</b> <br />
                Password has been reset to {password} <br />
                <button onClick={() => toast.dismiss(t.id)} className="okButton">OK</button>
              </span>
            ),
            { duration: 12000 }
          );
        }
        toast.success(
          (t) => (
            <span>
              <b>Success</b> <br />
              Password has been reset <br />
              <button onClick={() => toast.dismiss(t.id)} className="okButton">OK</button>
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
			<FormContainer pageTitle="Reset Password">
				<form className="mb-1 w-11/12 max-w-md" onSubmit={doSubmit}>
        <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
            New password
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
            Change password
          </button>
          <Link href="/login">
            <p className="mt-2 text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              Got back to login
            </p>
          </Link>
				</form>
			</FormContainer>
		</>
  );
};

export default ResetPassword;
