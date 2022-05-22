import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { User, UserRole, UUID } from '../../lib/types';
import UserTableRow from './UsertableRow';
import { SearchIcon } from '@heroicons/react/outline';

type UserTableProps = {
  /**
   * Array of all users to show
   */
  users: User[];

  /**
   * Function to update the role of local user object
   *
   * @see {@link USERS_PAGE_USERS}
   */
  updateUsersLocal: (id: UUID, role: UserRole) => void;

  /**
   * Function to update the error message on the users page
   *
   * @see {@link USERS_PAGE_ERROR}
   */
  setGlobalError: Dispatch<SetStateAction<string>>;

  /**
   * Function to update filter string
   *
   * @see {@link USERS_PAGE_FILTER}
   */
  setFilter: Dispatch<SetStateAction<string>>;

  /**
   * string containing the name filter
   *
   * @see {@link USERS_PAGE_FILTER}
   */
  nameFilter: string;

  /**
   * if the logged in user is an admin
   */
  isAdmin: boolean;

  /**
   * update the current user to delete
   */
  setDeleteUser: Dispatch<SetStateAction<User | undefined>>;

  /**
   * logged in user
   */
  loggedInUser: User;

  /**
   * state update function that sets wether the user delete form needs to be shown
   */
  setShowDeleteForm: Dispatch<SetStateAction<boolean>>;
};

/**
 * Table to list all users
 *
 * @param UserTableProps - properties used in User Table
 * @returns User Table component
 */
const UserTable: React.FC<UserTableProps> = ({
  users,
  updateUsersLocal,
  setGlobalError,
  setFilter,
  nameFilter,
  isAdmin,
  setDeleteUser,
  loggedInUser,
  setShowDeleteForm,
}: UserTableProps) => {
  const filterRef = useRef<HTMLInputElement>(null);

  const [showFilter, setShowFilter] = useState(false);

  useEffect(() => {
    if (showFilter) {
      filterRef?.current?.focus();
    } else {
      setFilter('');
    }
  }, [showFilter]);

  return (
    <table className="w-full table-fixed">
      <thead className="sticky top-0 bg-white">
        <tr>
          <th className="w-1/4 py-4">
            <div className="flex flex-row items-center justify-start">
              {showFilter ? (
                <>
                  <input
                    type="text"
                    className="box-border border-2 pl-5 sm:w-11/12 xl:w-3/5"
                    ref={filterRef}
                    value={nameFilter}
                    onChange={(e) => setFilter(e.target.value)}
                  />
                  <button
                    className="absolute ml-1 block h-4 w-4 rounded-[50%] text-2xl leading-[14px]"
                    onClick={() => setShowFilter(false)}
                  >
                    &times;
                  </button>
                </>
              ) : (
                <>
                  <div
                    className="flex flex-row items-center hover:cursor-pointer"
                    onClick={() => setShowFilter(true)}
                    title="search user"
                  >
                    <p className="text-left text-lg">Name</p>
                    <div className="ml-2 h-fit w-fit hover:cursor-pointer">
                      <SearchIcon className="h-4 w-4" />
                    </div>
                  </div>
                </>
              )}
            </div>
          </th>
          <th className="w-1/2 text-left text-lg">Email</th>
          <th className="w-1/4 text-right text-lg">Account Status</th>
        </tr>
      </thead>
      <tbody>
        {users && users.length ? (
          users.map((_user) => (
            <UserTableRow
              key={_user.id}
              user={_user}
              updateUsersLocal={updateUsersLocal}
              setGlobalError={setGlobalError}
              isAdmin={isAdmin}
              setDeleteUser={setDeleteUser}
              loggedInUserId={loggedInUser.id}
              setShowDeleteForm={setShowDeleteForm}
            />
          ))
        ) : (
          <tr className="h-16">
            <td colSpan={3}>
              <p className="text-center text-xl">No users to list</p>
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};

export default UserTable;
