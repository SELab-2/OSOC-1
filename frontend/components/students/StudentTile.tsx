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
        if (suggestion.status == 'Yes') {
            yes += 1;
        } else if (suggestion.status == 'No') {
            no += 1;
        } else {
            maybe += 1;
        }
    })

    return (
        <tr key={student.id} className="">
            <td className="">
                <div className="flex flex-row justify-between p-2 my-2 shadow-sm shadow-gray-500">

                        <div className="w-3/4 flex flex-col justify-center">
                            <div className={`flex flex-row ${student.alumn ? 'visible' : 'hidden h-0 w-0'}`}>
                            <p className={`m-0 text-xs bg-green-300 rounded-xl ${student.alumn ? 'visible px-1' : 'invisible px-0 h-0'}`}>
                                Alumn
                            </p>
                            </div>
                    <p className="pl-2"> {student.firstName + " " + student.lastName} </p>
                    </div>
                    <div className="relative w-[10%]">
                <PieChart
                    data={[
                        { title: 'Yes', value: yes, color: '#22c55e' },
                        { title: 'No', value: no, color: '#ef4444' },
                        { title: 'Maybe', value: maybe, color: '#f97316' },
                    ]}
                    lineWidth={25}
                />
                    {/* TODO fix this text size to not be hardcoded */}
                    <i
                        className={`absolute left-1/2 top-1/2 chart-label text-[28px] ${myColor}`}
                    >{myLabel}</i>
                </div>
                </div>
            </td>
        </tr>
    )
}

export default StudentTile;