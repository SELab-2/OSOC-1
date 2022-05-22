import '../styles/globals.css';
import '../styles/line.css';
import type { AppProps } from 'next/app';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from '../context/AuthProvider';
import Head from 'next/head';

function App({ Component, pageProps: { pageProps } }: AppProps) {
  return (
    <>
      <Head>
        <link rel="shortcut icon" href="/favicon.ico" />
      </Head>
      <AuthProvider>
        <Component {...pageProps} />
        <Toaster position="top-right" />
      </AuthProvider>
    </>
  );
}

export default App;
