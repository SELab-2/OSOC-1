import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { User, UserRole, UUID } from '../../lib/types';
import UserTableRow from './UsertableRow';
import { SearchIcon } from "@heroicons/react/outline";

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
}

/**
 * Table to list all users
 * 
 * @param UserTableProps - properties used in User Table
 * @returns User Table component
 */
const UserTable: React.FC<UserTableProps> = ({ users, updateUsersLocal, setGlobalError, setFilter, nameFilter, isAdmin }: UserTableProps) => {
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
            {
              showFilter 
              ? (
              <input
                type="text"
                className="border-2"
                ref={filterRef}
                value={nameFilter}
                onChange={(e) => setFilter(e.target.value)}
              />
              )
              : (<p className="text-left text-lg">Name</p>)
            }
            <div className="w-fit h-fit hover:cursor-pointer ml-2" onClick={() => setShowFilter(!showFilter)}>
              <SearchIcon className="h-4 w-4"/>
            </div>
          </div>
        </th>
        <th className="w-1/2 text-lg text-left">
          Email
        </th>
        <th className="w-1/4 text-right text-lg">
          Account Status
        </th>
      </tr>
      </thead>
      <tbody>
      {
        users && users.length
        ? users.map((user) => <UserTableRow key={user.id} user={user} updateUsersLocal={updateUsersLocal} setGlobalError={setGlobalError} isAdmin={isAdmin} />)
        : (
          <tr className="h-16">
            <td colSpan={3}>
              <p className="text-xl text-center">No users to list</p>
            </td>
          </tr>
        )
      }
      </tbody>
    </table>
  )
}

export default UserTable;
