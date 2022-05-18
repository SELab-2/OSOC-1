import { TrashIcon } from '@heroicons/react/outline';
import { Dispatch, FC, SetStateAction, useState } from 'react';

type CommsTableRowProps = {
  studentName: string;
  commsMessage: string;
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
      <td className="flex flex-row py-4">
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
      </td>
      <td>{commsMessage}</td>
    </tr>
  );
};
export default CommsTableRow;
