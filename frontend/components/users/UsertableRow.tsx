import { useState } from "react";

export type User = {
  id: string,
  username: string,
  email: string,
  role: string
};

type URTProps = {
  user: User;
}

const roles = {
  'Admin': 0,
  'Coach': 1,
  'Disabled': 2
}

const UserTableRow: React.FC<URTProps> = ({ user }: URTProps) => {

  const [role, setRole] = useState(user.role);

  return (
    <tr key={user.id} className="border-y-2 border-collapse">
    <td className="py-4">
      { user.username }
    </td>
    <td>
      { user.email }
    </td>
    <td className="text-right">
      <select value={ role } onChange={ (e) => setRole(e.target.value) }>
        {
          Object.keys(roles).map((_role, idx) => (
            <option key={idx} value={ _role }>
              { _role }
            </option>
          ))
        }
      </select>
    </td>
  </tr>
  )
}

export default UserTableRow;