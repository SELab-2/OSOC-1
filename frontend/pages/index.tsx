import type { NextPage } from 'next';
import Head from 'next/head';
import Header from '../components/Header';
import PersistLogin from '../components/PersistLogin';
import RouteProtection from '../components/RouteProtection';
import { UserRole } from '../lib/types';

const Home: NextPage = () => {
  return (
    <PersistLogin>
      <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
        <div className="h-screen">
          <Head>
            <title>Create Next App</title>
            <link rel="icon" href="/favicon.ico" />
          </Head>

          <Header />
        </div>
      </RouteProtection>
    </PersistLogin>
  );
};

export default Home;
