import { NextPage } from 'next';
import { useEffect, useState } from 'react';
import Error from '../components/Error';
import Header from '../components/Header';
import UserTable from '../components/users/UserTable';
import useAxiosAuth from '../hooks/useAxiosAuth';
import Endpoints from '../lib/endpoints';
import { User, UserRole, UUID } from '../lib/types';
import { SpinnerCircular } from 'spinners-react';
import { useRouter } from 'next/router';
import useUser from '../hooks/useUser';
import axios, { AxiosError } from 'axios';
import RouteProtection from '../components/RouteProtection';
import PersistLogin from '../components/PersistLogin';
import UserDeleteForm from '../components/users/UserDeleteForm';
import { parseError } from '../lib/requestUtils';
import Head from 'next/head';

/**
 *
 * {@label USERS_PAGE}
 *
 * @returns the users page
 */
const Users: NextPage = () => {
  /**
   * Users to show on the page
   *
   * {@label USERS_PAGE_USERS}
   */
  const [users, setUsers] = useState([] as User[]);

  /**
   * Filtered list of users
   */
  const [filteredUsers, setFilteredUsers] = useState([] as User[]);

  /**
   * name filter on users
   *
   * {@label USERS_PAGE_FILTER}
   */
  const [nameFilter, setNameFilter] = useState('');

  /**
   * Global error to show on the page in big red error box
   *
   * {@label USERS_PAGE_ERROR}
   */
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const [userToDelete, setUserToDelete] = useState<User | undefined>(undefined);
  const [showDeleteForm, setShowDeleteForm] = useState(false);

  const axiosAuth = useAxiosAuth();
  const router = useRouter();
  const [user] = useUser();

  /**
   * Update the role of the local user object
   * @param userId - the id of the user object to update
   * @param role - the new role for the user
   */
  const updateUserLocal = (userId: UUID, role: UserRole) => {
    const updatedUsers = users.map((val) => {
      if (val.id === userId) {
        val.role = role;
      }
      return val;
    });
    setUsers(updatedUsers);
  };

  const deleteUser = async (user: User) => {
    try {
      await axiosAuth.delete(`${Endpoints.USERS}/${user.id}`);

      // update client side
      setUsers((prev) => {
        return prev.filter((u) => u.id !== user.id);
      });
    } catch (err) {
      parseError(err, setError, router);
    }
  };

  /**
   * Update the filtered users with the new users/filters
   */
  useEffect(() => {
    const filterUsers: () => User[] = () => {
      const normalizedNameFilter = nameFilter.trim().toLowerCase();
      return users.filter((val: User) => {
        return val.username.trim().toLowerCase().includes(normalizedNameFilter);
      });
    };

    if (nameFilter) {
      const _filteredUsers = filterUsers();
      setFilteredUsers(_filteredUsers);
    } else {
      setFilteredUsers(users);
    }
  }, [users, nameFilter]);

  /**
   * Fetch all users from backend and update user state
   *
   * runs on mount
   */
  useEffect(() => {
    let isMounted = true;

    const getUsers = async () => {
      try {
        const response = await axiosAuth.get(Endpoints.USERS);
        if (isMounted) {
          setUsers(response.data as User[]);
          setFilteredUsers(response.data as User[]);
          setLoading(false);
        }
      } catch (err) {
        if (axios.isAxiosError(err)) {
          const _err = err as AxiosError;
          if (_err.response?.status === 418) router.push('/login'); // error when trying to refresh refreshtoken
          if (isMounted) {
            setError(_err.response?.statusText || 'An unknown error occurred');
          }
        } else {
          console.error(err);
          setError('Uhoh! It seems like something went wrong');
        }
      }
    };

    getUsers();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <PersistLogin>
      <Head>
        <title>Users</title>
      </Head>
      <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
        <div className="h-screen">
          <Header setError={setError} />
          <div className="mx-auto mt-16 mb-32 w-11/12 p-0 md:w-3/5">
            {loading ? (
              <div className="relative top-1/2 translate-y-1/2">
                <p className="mb-4 text-center text-2xl opacity-75">
                  Fetching Users...
                </p>
                <SpinnerCircular
                  size={100}
                  thickness={80}
                  color="#FCB70F"
                  secondaryColor="rgba(252, 183, 15, 0.4)"
                  className="mx-auto"
                />
              </div>
            ) : (
              <>
                {error && <Error error={error} className="mb-4" />}
                <UserTable
                  users={filteredUsers.filter(Boolean)}
                  updateUsersLocal={updateUserLocal}
                  setGlobalError={setError}
                  setFilter={setNameFilter}
                  nameFilter={nameFilter}
                  isAdmin={user.role === UserRole.Admin}
                  setDeleteUser={setUserToDelete}
                  loggedInUser={user}
                  setShowDeleteForm={setShowDeleteForm}
                />
              </>
            )}
          </div>
        </div>
        {userToDelete && (
          <UserDeleteForm
            userName={userToDelete.username}
            deleteUser={async () => deleteUser(userToDelete)}
            openDeleteForm={showDeleteForm}
            setUserDeleteForm={setUserToDelete}
            setOpenDeleteForm={setShowDeleteForm}
          />
        )}
      </RouteProtection>
    </PersistLogin>
  );
};

export default Users;
