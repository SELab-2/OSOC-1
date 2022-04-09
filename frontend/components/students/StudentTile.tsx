import { PieChart } from 'react-minimal-pie-chart';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck, faQuestion, faXmark } from "@fortawesome/free-solid-svg-icons";
import { Icon } from '@iconify/react';
const check_mark = <FontAwesomeIcon icon={faCheck} />;
const question_mark = <FontAwesomeIcon icon={faQuestion} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;
const tilde_mark = <Icon icon="mdi:tilde" />;

export type Student = {
    id: string,
    firstName: string,
    lastName: string,
    status: string,
    statusSuggestions: StatusSuggestion[],
    alumn: boolean
};

type StatusSuggestion = {
    coachId: string,
    status: string,
    motivation: string
}

type StudentProp = {
    student: Student;
}

const StudentTile: React.FC<StudentProp> = ({ student }: StudentProp) => {

    let myLabel = tilde_mark;
    let myColor = "text-gray-400";
    if (student.status == "Yes"){
        myLabel = check_mark;
        myColor = "text-green-400";
    } else if (student.status == "No") {
        myLabel = x_mark;
        myColor = "text-red-400";
    } else if (student.status == "Maybe") {
        myLabel = question_mark;
        myColor = "text-orange-400";
    }

    let yes = 0;
    let no = 0;
    let maybe = 0;
    student.statusSuggestions.forEach( (suggestion) => {
        console.log(suggestion);
        if (suggestion.status == 'Yes') {
            yes += 1;
        } else if (suggestion.status == 'No') {
            no += 1;
        } else {
            maybe += 1;
        }
    })

    return (
        <tr key={student.id} className="border-y-2 border-collapse">
            <td className="py-4">
                { student.firstName + " " + student.lastName }
            </td>
            {/* TODO Add the alumni thing */}
            <td className="text-right">
                <div className="relative">
                <PieChart
                    data={[
                        { title: 'Yes', value: yes, color: '#22c55e' },
                        { title: 'No', value: no, color: '#ef4444' },
                        { title: 'Maybe', value: maybe, color: '#f97316' },
                    ]}
                    lineWidth={15}
                />
                    {/* TODO fix this size to not be hardcoded */}
                    <i
                        className={`absolute left-1/2 top-1/2 chart-label text-[60px] ${myColor}`}
                    >{myLabel}</i>
                </div>
            </td>
        </tr>
    )
}

export default StudentTile;