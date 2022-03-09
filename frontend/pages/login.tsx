import { getCsrfToken, getProviders, signIn } from "next-auth/react"
import type { GetServerSideProps } from "next"
import Link from "next/link";

type Providers = {
  github: {
    name: string,
    id: string
  }
}

// before sending page, it will acquire the csrftoken
export const getServerSideProps: GetServerSideProps = async (context) => {
  return {
    props: {
      csrfToken: await getCsrfToken(context),
      providers: await getProviders()
    }
  };
}

const Login = ({ csrfToken, providers }: {csrfToken: string | undefined, providers: Providers}) => {
  console.log(providers);
  return (
    <>
      <div className="h-screen bg-[url('../public/img/login.png')] bg-center">
        <div id="login-base" 
          className="bg-[#F3F3f3] relative top-1/2 -translate-y-1/2 m-auto w-11/12 rounded-md flex flex-col items-center
                      text-center px-4 py-4
                     md:w-11/12 md:rounded-[46px] lg:w-10/12"
        >
          <header className="pb-5">
            <h1 className="float-left text-3xl text-osoc-blue font-bold">Login</h1>
            <img src="https://osoc.be/img/logo/logo-osoc-color.svg" className="hidden w-16 h-16"/>
          </header>
          {/* Callback to next-auth endpoint */}
          <form className="mb-1 w-11/12" method="post" action="/api/auth/callback/credentials">
            {/* Necessary for next-auth credentials login */}
            <input name="csrfToken" type="hidden" defaultValue={csrfToken}/>
            <label className="block mb-4 text-left">
              Email Address
              <input className="block mt-1 w-full h-8 p-1" name="email" type="email"/>
            </label>
            <label className="block mb-4 text-left">
              Password
              <input className="block mt-1 w-full h-8 p-1" name="password" type="password"/>
            </label>
            <button className="bg-osoc-btn-primary text-osoc-blue px-4 py-1 rounded-sm font-medium" type="submit">Log in</button>
          </form>
            <Link href="/register">
              <p className="text-xs underline underline-offset-1 hover:cursor-pointer opacity-90">
              no account yet? <span className="block">register here!</span>
              </p>
            </Link>
          {/* Add line styling like in Figma */}
          <p className="text-sm pt-4 opacity-80 pb-2">Or login using</p>
          {/* Github provider */}
          <button className="bg-[#302727] text-white px-4 py-1" onClick={() => signIn(providers.github.id)}>
            
            <p className="text-right">
              {providers.github.name}
            </p>
          </button>
        </div>
      </div>
    </>
  )
}
export default Login