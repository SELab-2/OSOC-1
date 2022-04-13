import { NextPage } from 'next';
import { useEffect, useState } from 'react';
import Error from '../components/Error';
import Header from '../components/Header';
import UserTable from '../components/users/UserTable';
import useAxiosAuth from '../hooks/useAxiosAuth';
import Endpoints from '../lib/endpoints';
import { User } from '../lib/types';
import { SpinnerCircular } from 'spinners-react';

const users = [
  {
    id: '1',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '2',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '3',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '4',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '5',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '6',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '7',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '8',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '9',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '10',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '11',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '12',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '91',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '92',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '93',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '94',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '95',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '96',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '97',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '98',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '99',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
  {
    id: '910',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin',
  },
  {
    id: '911',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled',
  },
  {
    id: '912',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach',
  },
];

const Users: NextPage = () => {
  const [users, setUsers] = useState([] as User[]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const axiosAuth = useAxiosAuth();

  useEffect(() => {
    let isMounted = true;

    const getUsers = async () => {
      const response = await axiosAuth.get('/bogus' + Endpoints.USERS);
      if (isMounted) setUsers(response.data as User[]);
    }

    try {
      getUsers();
      if (isMounted) setLoading(false);
    } catch (err: unknown) {
      setError(err as string);
      if (isMounted) setLoading(false);
    }

    return () => {
      isMounted = false;
    }

  }, []);

  return (
    <div className="h-screen">
      <Header />
      <div className="mx-auto mt-16 mb-32 w-3/5 p-0">
        {
        loading
        ? (
          <div className="relative top-1/2 translate-y-1/2">
            <p className="text-2xl text-center opacity-75 mb-4">Fetching Users...</p>
            <SpinnerCircular
              size={100}
              thickness={80}
              color="#FCB70F"
              secondaryColor="rgba(252, 183, 15, 0.4)"
              className="mx-auto"
            />
          </div>
        )
        : error
          ? (
            <>
            <Error
              error={error}
              className="mb-4"
            />
            <UserTable users={users} />
            </>
          )
          : <UserTable users={users} />
        }
      </div>
    </div>
  );
};

export default Users;
