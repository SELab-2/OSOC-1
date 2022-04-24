import type { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';
import Header from '../components/Header';
import RouteProtection from '../components/RouteProtection';
import { UserRole } from '../lib/types';

const Home: NextPage = () => {
  return (
    <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
      <div className="flex min-h-screen flex-col items-center justify-center py-2">
        <Head>
          <title>Create Next App</title>
          <link rel="icon" href="/favicon.ico" />
        </Head>
        {/* added the header here for easier testing */}
        <Header />

        <Link href="/users">Go to Users Page</Link>
      </div>
    </RouteProtection>
  );
};

export default Home;
