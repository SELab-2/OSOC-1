import FormContainer from '../components/FormContainer';
import Link from 'next/link';
import { FormEventHandler } from 'react';
import { useRecoilState, useRecoilValue } from 'recoil';
import {
  nameState,
  emailState,
  passwordState,
  repeatPasswordState,
  validNameState,
  validEmailState,
  validPasswordState,
  validRepeatPasswordState,
} from '../atoms/registerAtoms';
import toast from 'react-hot-toast';
import { useRouter } from 'next/router';

// BUG: for some reason, state gets saved after a refresh, until one of the states change

/**
 * Register page for OSOC application
 *
 * @returns Register Page
 */
const register = () => {
  const [name, setName] = useRecoilState(nameState);
  const [email, setEmail] = useRecoilState(emailState);
  const [password, setPassword] = useRecoilState(passwordState);
  const [repeatPassword, setRepeatPassword] =
    useRecoilState(repeatPasswordState);

  const validName = useRecoilValue(validNameState);
  const validEmail = useRecoilValue(validEmailState);
  const validPassword = useRecoilValue(validPasswordState);
  const validRepeatPassword = useRecoilValue(validRepeatPasswordState);

  const router = useRouter();

  const registerUser: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    if (!(validName && validEmail && validPassword && validRepeatPassword))
      return;

    const reqBody = {
      username: name,
      email: email,
      role: 'Disabled', // TODO: NEEDS TO BE MADE DEFAULT IN BE
      password: password,
    };

    const url =
      ((process.env.API_ENDPOINT as string) || 'http://localhost:8080/api') +
      '/users';

    const req = fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(reqBody),
    });

    const res = await req;

    if (res.ok) {
      const timeout = 1000;
      toast.success('Succesfully registered\nReturning to login...', {
        duration: timeout,
      });

      setTimeout(() => {
        setName('');
        setEmail('');
        setPassword('');
        setRepeatPassword('');
        router.push('/login');
      }, timeout);
    } else {
      setName('');
      setEmail('');
      setPassword('');
      setRepeatPassword('');
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
                validName ? 'border-[#C4C4C4]' : 'border-red-500'
              } p-1 text-sm`}
              name="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Email Address
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validEmail ? 'border-[#C4C4C4]' : 'border-red-500'
              } p-1 text-sm`}
              name="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Password
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validPassword ? 'border-[#C4C4C4]' : 'border-red-500'
              } p-1 text-sm`}
              name="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Repeat Password
            <input
              className={`mt-1 box-border block h-8 w-full border-2 ${
                validRepeatPassword ? 'border-[#C4C4C4]' : 'border-red-500'
              } p-1 text-sm`}
              name="repeatPassword"
              type="password"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
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
