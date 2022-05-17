import { FC } from 'react';

type CommsTableRowProps = {
  studentName: string;
  commsMessage: string;
};

const CommsTableRow: FC<CommsTableRowProps> = ({
  studentName,
  commsMessage,
}: CommsTableRowProps) => {
  return (
    <tr className={`odd:bg-neutral-50 even:bg-neutral-100`}>
      <td>{studentName}</td>
      <td>{commsMessage}</td>
    </tr>
  );
};
export default CommsTableRow;
