import '../styles/globals.css';
import '../styles/line.css';
import type { AppProps } from 'next/app';
import { RecoilRoot } from 'recoil';
import { Toaster } from 'react-hot-toast';

function App({ Component, pageProps: { pageProps } }: AppProps) {
  return (
    <>
      {/* RecoilRoot exposes the whole application to the Recoil state manager */}
      <RecoilRoot>
        <Component {...pageProps} />
        <Toaster position="top-right" />
      </RecoilRoot>
    </>
  );
}

export default App;
