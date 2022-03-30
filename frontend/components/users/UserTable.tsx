import UserTableRow, { User } from './UsertableRow';

type UserTableProps = {
  users: User[]
}

const UserTable: React.FC<UserTableProps> = ({ users }: UserTableProps) => {

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
        users.map((user) => <UserTableRow key={user.id} user={user}/>)
      }
      </tbody>
    </table>
  )
}

export default UserTable;