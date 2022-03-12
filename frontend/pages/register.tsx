import FormContainer from '../components/FormContainer';
import Link from 'next/link';

const register = () => {
  return (
    <>
      <FormContainer pageTitle="REGISTER">
        <form className="mb-1 w-11/12 max-w-md" method="post" action="#">
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Name
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="name"
              type="text"
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Email Address
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="email"
              type="email"
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Password
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="password"
              type="password"
            />
          </label>
          <label className="mx-auto mb-4 block text-left lg:mb-4 lg:max-w-sm">
            Repeat Password
            <input
              className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
              name="password"
              type="password"
            />
          </label>
          <button
            className="rounded-sm bg-osoc-btn-primary px-4 py-1 font-medium text-osoc-blue shadow-sm shadow-gray-300 lg:mb-3"
            type="submit"
          >
            Register
          </button>
          <Link href="/login">
            <p className="text-xs underline underline-offset-1 opacity-90 hover:cursor-pointer">
              back to login
            </p>
          </Link>
        </form>
      </FormContainer>
    </>
  );
};

export default register;
