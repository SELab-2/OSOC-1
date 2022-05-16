import { FC } from "react";
import { Student } from "../../lib/types";


type CommsTableRowProps = {
  studentName: string;
  commsMessage: string;
};

const CommsTableRow: FC<CommsTableRowProps> = ({ studentName, commsMessage }: CommsTableRowProps) => {
  
  return (
    <tr>
      <td>
        { studentName }
      </td>
      <td>
        { commsMessage }
      </td>
    </tr>
  )
}
export default CommsTableRow