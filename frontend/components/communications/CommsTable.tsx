import {
  Dispatch,
  FC,
  SetStateAction,
  useEffect,
  useRef,
  useState,
} from 'react';
import { StudentComm } from '../../lib/types';
import CommsTableRow from './CommsTableRow';
import { SearchIcon } from '@heroicons/react/outline';

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

  /**
   * string containing the name filter
   *
   * @see {@link USERS_PAGE_FILTER}
   */
  nameFilter: string;

  /**
   * Function to update filter string
   *
   * @see {@link USERS_PAGE_FILTER}
   */
  setNameFilter: Dispatch<SetStateAction<string>>;
};

function compareStudentComm(a: StudentComm, b: StudentComm): number {
  if (a.name !== b.name) return a.name >= b.name ? 1 : -1;
  return a.registrationTime <= b.registrationTime ? 1 : -1;
}

const CommsTable: FC<CommsTableProps> = ({
  studentComms,
  setCommsToDelete,
  setShowDeleteForm,
  nameFilter,
  setNameFilter,
}: CommsTableProps) => {
  const filterRef = useRef<HTMLInputElement>(null);
  const [showFilter, setShowFilter] = useState(false);

  useEffect(() => {
    if (showFilter) {
      filterRef?.current?.focus();
    } else {
      setNameFilter('');
    }
  }, [showFilter]);

  return (
    <table className="w-full table-fixed">
      <thead className="sticky top-[48px] bg-white">
        <tr>
          <th className="w-1/4 py-4 text-left text-lg">
            <div className="flex flex-row items-center justify-start">
              {showFilter ? (
                <input
                  type="text"
                  className="border-2"
                  ref={filterRef}
                  value={nameFilter}
                  onChange={(e) => setNameFilter(e.target.value)}
                />
              ) : (
                <p className="text-left text-lg">Student Name</p>
              )}
              <div
                className="ml-2 h-fit w-fit hover:cursor-pointer"
                onClick={() => setShowFilter(!showFilter)}
              >
                <SearchIcon className="h-4 w-4" />
              </div>
            </div>
          </th>
          {/*<th className="w-1/4 py-4 text-left text-lg">Student Name</th>*/}
          <th className="w-1/4 text-left text-lg">Information</th>
          <th className="w-1/4 text-right text-lg">Time</th>
        </tr>
      </thead>
      <tbody>
        {studentComms && studentComms.length ? (
          studentComms
            .sort((a, b) => compareStudentComm(a, b))
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
