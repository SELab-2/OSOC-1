import '../styles/globals.css';
import type { AppProps } from 'next/app';
import { SessionProvider } from 'next-auth/react';

function App({ Component, pageProps: { session, ...pageProps } }: AppProps) {
  return (
    <>
      {/* Sessionprovider exposes the whole app to next-auth session object (using useSession hook) */}
      <SessionProvider session={session}>
        <Component {...pageProps} />
      </SessionProvider>
    </>
  );
}

export default App;
