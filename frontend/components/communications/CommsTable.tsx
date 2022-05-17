import { FC } from 'react';
import { StudentComm } from '../../lib/types';
import CommsTableRow from './CommsTableRow';

type CommsTableProps = {
  studentComms: StudentComm[];
};

const CommsTable: FC<CommsTableProps> = ({ studentComms }: CommsTableProps) => {
  return (
    <table className="w-full table-fixed">
      <thead className="sticky top-0 bg-white">
        <tr>
          <th className="w-1/4 py-4 text-left text-lg">Student Name</th>
          <th className="w-1/4 text-right text-lg">
            Communication Information
          </th>
        </tr>
      </thead>
      <tbody>
        {studentComms && studentComms.length ? (
          studentComms
            .sort((a, b) => (a.name >= b.name ? 1 : -1))
            .map((comm) => (
              <CommsTableRow
                key={comm.id}
                studentName={comm.name}
                commsMessage={comm.commMessage}
              />
            ))
        ) : (
          <tr className="h-16">
            <td colSpan={2}>
              <p className="text-center text-xl">No communications to list</p>
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};
export default CommsTable;
