import { getCsrfToken, getProviders, signIn } from "next-auth/react"
import type { GetServerSideProps } from "next"
import Link from "next/link";

type Provider = {
  name: string,
  id: string
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

const Login = ({ csrfToken, providers }: {csrfToken: string | undefined, providers: Provider[]}) => {

  return (
    <>
      {/* TODO: add correct background */}
      <div className="h-screen bg-gradient-to-br from-green-500 to-blue-500">
        <div id="login-base" className="">
          <h1 className="">Login</h1>
          <img src="https://osoc.be/img/logo/logo-osoc-color.svg" className="w-32 h-32"/>
          {/* Callback to next-auth endpoint */}
          <form method="post" action="/api/auth/callback/credentials">
            {/* Necessary for next-auth credentials login */}
            <input name="csrfToken" type="hidden" defaultValue={csrfToken}/>
            <label>
              Email Address
              <input name="email" type="email"/>
            </label>
            <label>
              Password
              <input name="password" type="password"/>
            </label>
            <button type="submit">Sign in</button>
          </form>
          <Link href="/register">
            no account yet? register here!
          </Link>

          <p>Or login using</p>

          {/* Filter the Credentials provider out of here because the above form already takes care of that */}
          {
            Object.values(providers).filter((provider) => provider.name !== 'Credentials').map((provider: Provider) => (
              <div key={provider.name}>
                <button onClick={() => signIn(provider.id)}>
                  {provider.name}
                </button>
              </div>
            ))
          }
        </div>
      </div>
    </>
  )
}
export default Login