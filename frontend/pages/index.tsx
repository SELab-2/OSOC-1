import type { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';
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

        <Link href="/users">Go to Users Page</Link>
      </div>
    </RouteProtection>
  );
};

export default Home;
