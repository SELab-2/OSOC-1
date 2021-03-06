import axios, { AxiosError } from 'axios';
import { useRouter } from 'next/router';
import { Dispatch, SetStateAction, useState } from 'react';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import Endpoints from '../../lib/endpoints';
import { User, UserRole, UUID } from '../../lib/types';
import { ExclamationCircleIcon, RefreshIcon } from '@heroicons/react/solid';
import useUser from '../../hooks/useUser';
import { TrashIcon } from '@heroicons/react/outline';

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

  /**
   * setter to update user to delete
   */
  setDeleteUser: Dispatch<SetStateAction<User | undefined>>;

  /**
   * Current logged in user id
   */
  loggedInUserId: string;

  /**
   * state update function that sets wether the user delete form needs to be shown
   */
  setShowDeleteForm: Dispatch<SetStateAction<boolean>>;
};

/**
 * Row component to list in the Users Table
 * @param URTProps - component properties, @see {@link URTProps}
 * @returns User Table Row component
 */
const UserTableRow: React.FC<URTProps> = ({
  user,
  updateUsersLocal,
  setGlobalError,
  isAdmin,
  setDeleteUser,
  loggedInUserId,
  setShowDeleteForm,
}: URTProps) => {
  /**
   * individual row error (e.g. when an error occurs trying to update the role)
   */
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState(false);

  const [hovering, setHovering] = useState(false);

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
      );

      updateUsersLocal(userId, userRole);
      setUpdating(false);
    } catch (_error) {
      if (axios.isAxiosError(_error)) {
        setUpdating(false);
        const err = _error as AxiosError;
        if (err.response?.status === 400) router.push('/login'); // error when trying to refresh refreshtoken
        if (err.response?.status === 403) {
          const message = (err.response?.data as { message: string }).message;
          message && setGlobalError(message);
        } else {
          setError(
            'Something went wrong trying to update the role of the user'
          );
        }
      }
    }
  };

  return (
    <tr
      key={user.id}
      className="border-collapse border-y-2"
      onMouseOver={() => setHovering(true)}
      onMouseLeave={() => setHovering(false)}
    >
      <td className="flex flex-row py-4">
        {user.username}
        {hovering && user.id !== loggedInUserId && (
          <TrashIcon
            className="h-6 w-6 pl-1 hover:cursor-pointer"
            color="#F14A3B"
            onClick={() => {
              setDeleteUser(user);
              setShowDeleteForm(true);
            }}
          />
        )}
      </td>
      <td className="break-all">{user.email}</td>
      <td className="text-right">
        <div className="flex flex-row justify-end align-middle">
          {error && (
            <div className="mr-2" title={error}>
              <ExclamationCircleIcon className="m-auto h-4 w-4" color="red" />
            </div>
          )}
          {updating && (
            <div>
              <RefreshIcon
                className="mr-2 h-4 w-4 animate-spin-reverse"
                color="#0A0839"
              />
            </div>
          )}
          {isAdmin ? (
            <select
              value={user.role}
              onChange={(e) => updateRole(user.id, e.target.value as UserRole)}
            >
              {Object.keys(UserRole).map((_role, idx) => (
                <option key={idx} value={_role}>
                  {_role}
                </option>
              ))}
            </select>
          ) : (
            <div>
              <p>{user.role}</p>
            </div>
          )}
        </div>
      </td>
    </tr>
  );
};

export default UserTableRow;
