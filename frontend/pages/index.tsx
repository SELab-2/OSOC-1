import type { NextPage } from 'next';
import Head from 'next/head';
import Header from '../components/Header';
import RouteProtection from '../components/RouteProtection';
import { UserRole } from '../lib/types';

const Home: NextPage = () => {
  return (
    <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
      <div className="h-screen min-h-screen">
        <Head>
          <title>Create Next App</title>
          <link rel="icon" href="/favicon.ico" />
        </Head>
        {/* added the header here for easier testing */}
        <Header />
      </div>
    </RouteProtection>
  );
};

export default Home;
