import { NextPage } from "next";
import Header from "../components/Header";
import UserTable from "../components/users/UserTable";

const users = [
  {
    id: '1',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '2',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '3',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '4',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '5',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '6',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '7',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '8',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '9',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '10',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '11',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '12',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '91',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '92',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '93',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '94',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '95',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '96',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '97',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '98',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '99',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  },
  {
    id: '910',
    username: 'Dipper Pines',
    email: 'dipper.pines@email.com',
    role: 'Admin'
  },
  {
    id: '911',
    username: 'Mabel Pines',
    email: 'mabel.pines@email.com',
    role: 'Disabled'
  },
  {
    id: '912',
    username: 'Wendy Corduroy',
    email: 'wendy.corduroy@email.com',
    role: 'Coach'
  }
]

const Users: NextPage = () => {
  return (
    <div className="h-screen">
      <Header/>
      <div className="mx-auto w-3/5 p-0 mt-16 mb-32">
        <UserTable
          users={users}
        />
      </div>
    </div>
  );
}

export default Users;