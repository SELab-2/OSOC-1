import { Dispatch, FC, SetStateAction } from 'react';
import { StudentComm } from '../../lib/types';
import CommsTableRow from './CommsTableRow';

type CommsTableProps = {
  studentComms: StudentComm[];

  /**
   * set the communication id to delete
   */
  setCommsToDelete: Dispatch<SetStateAction<string>>;

  /**
   * Set whether or not to show the deletion popup
   */
  setShowDeleteForm: Dispatch<SetStateAction<boolean>>;
};

const CommsTable: FC<CommsTableProps> = ({
  studentComms,
  setCommsToDelete,
  setShowDeleteForm,
}: CommsTableProps) => {
  return (
    <table className="w-full table-fixed">
      <thead className="sticky top-0 bg-white">
        <tr>
          <th className="w-1/4 py-4 text-left text-lg">Student Name</th>
          <th className="w-1/4 text-left text-lg">
            Information
          </th>
          <th className="w-1/4 text-right text-lg">
            Time
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
                commsId={comm.id}
                studentName={comm.name}
                commsMessage={comm.commMessage}
                registrationTime={comm.registrationTime}
                setCommsToDelete={setCommsToDelete}
                setShowDeleteForm={setShowDeleteForm}
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
