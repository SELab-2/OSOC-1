import { Dispatch, SetStateAction } from 'react';
import { User, UserRole, UUID } from '../../lib/types';
import UserTableRow from './UsertableRow';

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
}

/**
 * Table to list all users
 * 
 * @param UserTableProps - properties used in User Table
 * @returns User Table component
 */
const UserTable: React.FC<UserTableProps> = ({ users, updateUsersLocal, setGlobalError }: UserTableProps) => {

  return (
    <table className="w-full table-fixed">
      <thead className="sticky top-0 bg-white">
      <tr>
        <th className="w-1/4 text-left text-lg py-4">
          Name
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
        ? users.map((user) => <UserTableRow key={user.id} user={user} updateUsersLocal={updateUsersLocal} setGlobalError={setGlobalError}/>)
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