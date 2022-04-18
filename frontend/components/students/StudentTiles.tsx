import { Student } from '../../lib/types';
import StudentTile from './StudentTile';

type StudentTableProps = {
  students: Student[];
};

const StudentTiles: React.FC<StudentTableProps> = ({
  students,
}: StudentTableProps) => {
  return (
    <table className="w-full table-auto">
      <thead className="top-0">
        <tr>
          <th className="col-span-full border-b-2 border-gray-400 pb-1 text-right text-xs font-normal">
            {/* TODO this should be actual functionality */}
            {students.length + '/' + students.length + ' shown'}
          </th>
        </tr>
      </thead>
      <tbody>
        {students.map((student) => (
          <StudentTile key={student.id} student={student} />
        ))}
      </tbody>
    </table>
  );
};

export default StudentTiles;
