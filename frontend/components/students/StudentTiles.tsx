import StudentTile, { Student } from './StudentTile';

type StudentTableProps = {
    students: Student[]
}

const StudentTiles: React.FC<StudentTableProps> = ({ students }: StudentTableProps) => {

    return (
        <table className="w-full table-auto">
            <thead className="top-0 bg-white">
            <tr>
                {/* TODO outline right does not want to work */}
                <th className="col-span-full">
                    {/* TODO this should be something else once endpoint is dont */}
                    { students.length + "/" + students.length + " shown" }
                </th>
            </tr>
            </thead>
            <tbody>
            {
                students.map((student) => <StudentTile key={student.id} student={student}/>)
            }
            </tbody>
        </table>
    )
}

export default StudentTiles;