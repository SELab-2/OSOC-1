import { NextPage } from 'next';
import { FormEvent, useEffect, useState } from 'react';
import Error from '../components/Error';
import Header from '../components/Header';
import UserTable from '../components/users/UserTable';
import useAxiosAuth from '../hooks/useAxiosAuth';
import Endpoints from '../lib/endpoints';
import { User, UserRole, UUID } from '../lib/types';
import { SpinnerCircular } from 'spinners-react';
import { useRouter } from 'next/router';
import useUser from '../hooks/useUser';
import RouteProtection from '../components/RouteProtection';
import PersistLogin from '../components/PersistLogin';
import UserDeleteForm from '../components/users/UserDeleteForm';
import { parseError } from '../lib/requestUtils';
import Head from 'next/head';
import { emailRegex } from '../lib/regex';

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
  const [retry, setRetry] = useState(false);

  const [userToDelete, setUserToDelete] = useState<User | undefined>(undefined);
  const [showDeleteForm, setShowDeleteForm] = useState(false);

  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteLoading, setInviteLoading] = useState(false);

  const axiosAuth = useAxiosAuth();
  const router = useRouter();
  const [user] = useUser();
  let controller = new AbortController();

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

  const invite = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    setInviteLoading(true);

    if (!emailRegex.test(inviteEmail)) {
      setError('Please provide a valid email address');
    } else {
      await axiosAuth.post(Endpoints.INVITE, inviteEmail, {
        headers: {
          'Content-Type': 'text/plain',
        },
      });

      setInviteEmail('');
      setError('');
      setInviteLoading(false);
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
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    (async () => {
      await getUsers(signal);
    })();
    return () => {
      controller.abort();
    };
  }, []);

  /**
   * Fetch all users from backend and update user state
   * When page is reloaded, first request will fail with 401, retry once
   */
  useEffect(() => {
    if (!retry) {
      return;
    }
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    (async () => {
      await getUsers(signal);
    })();
    return () => {
      controller.abort();
    };
  }, [retry]);

  const getUsers = async (signal: AbortSignal) => {
    try {
      const response = await axiosAuth.get(Endpoints.USERS, { signal: signal });
      if (!signal.aborted) {
        setUsers(response.data as User[]);
        setFilteredUsers(response.data as User[]);
        setLoading(false);
      }
    } catch (err) {
      parseError(err, setError, router, signal);
      setRetry(true);
    }
  };

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
                {error && (
                  <Error error={error} setError={setError} className="mb-4" />
                )}
                <form
                  className="mb-2 flex w-full flex-row items-center justify-center gap-2 px-4"
                  onSubmit={invite}
                >
                  <label htmlFor="userEmail" className="ml-1 font-normal">
                    Invite User:
                  </label>
                  <input
                    id="userEmail"
                    type="email"
                    className="w-2/5 rounded border-2 border-gray-200 px-1 py-1"
                    required
                    value={inviteEmail}
                    onChange={(e) => setInviteEmail(e.target.value)}
                  />
                  {inviteLoading ? (
                    <SpinnerCircular
                      size={40}
                      thickness={150}
                      speed={150}
                      color="#FCB70F"
                      secondaryColor="rgba(252, 183, 15, 0.4)"
                    />
                  ) : (
                    <button
                      type="submit"
                      className="rounded-sm bg-osoc-yellow px-2 py-1 font-medium text-white shadow-sm shadow-gray-300 hover:brightness-95"
                    >
                      invite
                    </button>
                  )}
                </form>
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
