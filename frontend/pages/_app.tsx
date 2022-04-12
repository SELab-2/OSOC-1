import '../styles/globals.css';
import '../styles/line.css';
import type { AppProps } from 'next/app';
import { RecoilRoot } from 'recoil';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from '../context/AuthProvider';

function App({ Component, pageProps: { pageProps } }: AppProps) {
  return (
    <>
      {/* RecoilRoot exposes the whole application to the Recoil state manager */}
      <RecoilRoot>
        <AuthProvider>
          <Component {...pageProps} />
          <Toaster position="top-right" />
        </AuthProvider>
      </RecoilRoot>
    </>
  );
}

export default App;
