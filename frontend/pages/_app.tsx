import '../styles/globals.css';
import '../styles/line.css';
import type { AppProps } from 'next/app';
import { SessionProvider } from 'next-auth/react';
import { RecoilRoot } from 'recoil';
import { Toaster } from 'react-hot-toast';

function App({ Component, pageProps: { session, ...pageProps } }: AppProps) {
  return (
    <>
      {/* RecoilRoot exposes the whole application to the Recoil state manager */}
      <RecoilRoot>
        {/* Sessionprovider exposes the whole app to next-auth session object (using useSession hook) */}
        <SessionProvider session={session}>
          <Component {...pageProps} />
          <Toaster position="top-right" />
        </SessionProvider>
      </RecoilRoot>
    </>
  );
}

export default App;
