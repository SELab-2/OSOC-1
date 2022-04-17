import axios, { AxiosError } from "axios";
import { useRouter } from "next/router";
import { Dispatch, SetStateAction, useState } from "react";
import useAxiosAuth from "../../hooks/useAxiosAuth";
import Endpoints from "../../lib/endpoints";
import { User, UserRole, UUID } from "../../lib/types";
import { ExclamationCircleIcon, RefreshIcon } from "@heroicons/react/solid";
import useUser from "../../hooks/useUser";

/**
 * Properties for User Table Row component
 */
type URTProps = {
  /**
   * user to show in the row
   */
  user: User;

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
   * if the logged in user is an admin
   */
  isAdmin: boolean;
}

/**
 * Row component to list in the Users Table
 * @param URTProps - component properties, @see {@link URTProps}
 * @returns User Table Row component
 */
const UserTableRow: React.FC<URTProps> = ({ user, updateUsersLocal, setGlobalError, isAdmin }: URTProps) => {

  /**
   * individual row error (e.g. when an error occurs trying to update the role)
   */
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState(false);

  const axiosAuth = useAxiosAuth();
  const router = useRouter();
  const [currentUser] = useUser();

  /**
   * Updates the role in the backend service and updates the frontend users state to reflect the new changes
   * 
   * @param userId - the id of the user to update
   * @param userRole - the new role for this user
   */
  const updateRole = async (userId: UUID, userRole: UserRole) => {
    // extra safeguard to protect resources
    if (currentUser.role !== UserRole.Admin) return;
    
    setError('');
    setGlobalError('');
    setUpdating(true);
    
    try {
      await axiosAuth.post(
        `${Endpoints.USERS}/${userId}/role`,
        JSON.stringify(userRole)
      )

      updateUsersLocal(userId, userRole);
      setUpdating(false);

    } catch (_error) {
      if (axios.isAxiosError(_error)) {
        setUpdating(false);
        const err = _error as AxiosError;
        if (err.response?.status === 400) router.push('/login'); // error when trying to refresh refreshtoken
        if (err.response?.status === 403) {
          const message = err.response?.data.message;
          message && setGlobalError(message);
        }
        else { // not an Axios error
          console.error(_error);
          setError('Something went wrong trying to update the role of the user');
        }
      }
    }
  }

  return (
    <tr key={user.id} className="border-y-2 border-collapse">
    <td className="py-4">
      { user.username }
    </td>
    <td className="break-all">
      { user.email }
    </td>
    <td className="text-right">
      <div className="flex flex-row justify-end align-middle">
        {
          error && (
            <div className="mr-2" title={error}>
              <ExclamationCircleIcon className="h-4 w-4 m-auto" color="red"/>
            </div>
          )
        }
        {
          updating
          && (
            <div>
              <RefreshIcon className="h-4 w-4 mr-2 animate-spin-reverse" color="#0A0839"/>
            </div>
          )
        }
        {
          isAdmin
          ? (
            <select value={ user.role } onChange={ (e) => updateRole(user.id, e.target.value as UserRole) }>
            {
              Object.keys(UserRole).map((_role, idx) => (
                <option key={idx} value={ _role }>
                  { _role }
                </option>
              ))
            }
          </select>
          )
        : (
          <div>
            <p>{ user.role }</p>
          </div>
        )
        }
      </div>
    </td>
  </tr>
  )
}

export default UserTableRow;
