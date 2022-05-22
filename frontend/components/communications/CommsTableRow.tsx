import { TrashIcon } from '@heroicons/react/outline';
import { Dispatch, FC, SetStateAction, useState } from 'react';

type CommsTableRowProps = {
  studentName: string;
  commsMessage: string;
  registrationTime: Date;
  commsId: string;
  /**
   * set the communication id to delete
   */
  setCommsToDelete: Dispatch<SetStateAction<string>>;

  /**
   * Set whether or not to show the deletion popup
   */
  setShowDeleteForm: Dispatch<SetStateAction<boolean>>;
};

const CommsTableRow: FC<CommsTableRowProps> = ({
  studentName,
  commsMessage,
  registrationTime,
  commsId,
  setCommsToDelete,
  setShowDeleteForm,
}: CommsTableRowProps) => {
  const [hovering, setHovering] = useState(false);

  return (
    <tr
      className={`odd:bg-neutral-50 even:bg-neutral-100`}
      onMouseEnter={() => setHovering(true)}
      onMouseLeave={() => setHovering(false)}
    >
      <td className="m-auto py-4 pl-3">
        <div className="flex flex-row">
          {studentName}
          {hovering && (
            <TrashIcon
              className="h-6 w-6 pl-1 hover:cursor-pointer"
              color="#F14A3B"
              onClick={() => {
                setCommsToDelete(commsId);
                setShowDeleteForm(true);
              }}
            />
          )}
        </div>
      </td>
      <td className="py-4 ">{commsMessage}</td>
      <td className="py-4 pr-3 text-right">
        {new Date(registrationTime).toLocaleString()}
      </td>
    </tr>
  );
};
export default CommsTableRow;
