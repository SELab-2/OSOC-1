import { PieChart } from 'react-minimal-pie-chart';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faCheck,
  faQuestion,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import { Icon } from '@iconify/react';
const check_mark = <FontAwesomeIcon icon={faCheck} />;
const question_mark = <FontAwesomeIcon icon={faQuestion} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;
const tilde_mark = <Icon icon="mdi:tilde" />;

export type Student = {
  id: string;
  firstName: string;
  lastName: string;
  status: string;
  statusSuggestions: StatusSuggestion[];
  alumn: boolean;
};

type StatusSuggestion = {
  coachId: string;
  status: string;
  motivation: string;
};

type StudentProp = {
  student: Student;
};

const StudentTile: React.FC<StudentProp> = ({ student }: StudentProp) => {
  // There is probably a better way of doing this
  let myLabel = tilde_mark;
  let myColor = 'text-check-gray';
  if (student.status == 'Yes') {
    myLabel = check_mark;
    myColor = 'text-check-green';
  } else if (student.status == 'No') {
    myLabel = x_mark;
    myColor = 'text-check-red';
  } else if (student.status == 'Maybe') {
    myLabel = question_mark;
    myColor = 'text-check-orange';
  }

  // There is probably a better way of doing this
  let yes = 0;
  let no = 0;
  let maybe = 0;
  student.statusSuggestions.forEach((suggestion) => {
    if (suggestion.status == 'Yes') {
      yes += 1;
    } else if (suggestion.status == 'No') {
      no += 1;
    } else {
      maybe += 1;
    }
  });

  return (
    <tr key={student.id} className="">
      <td className="">
        <div className="my-2 flex flex-row justify-between p-2 shadow-sm shadow-gray-500">
          {/* basic student info */}
          <div className="flex w-3/4 flex-col justify-center">
            <div
              className={`flex flex-row ${
                student.alumn ? 'visible' : 'hidden h-0 w-0'
              }`}
            >
              <p
                className={`m-0 rounded-xl bg-osoc-bg text-xs ${
                  student.alumn ? 'visible px-1' : 'invisible h-0 px-0'
                }`}
              >
                Alumn
              </p>
            </div>
            <p className="pl-2">{student.firstName + ' ' + student.lastName}</p>
          </div>

          {/* holds the suggestions circle image thing + checkmark */}
          <div className="relative w-[10%]">
            <PieChart
              data={[
                { title: 'Yes', value: yes, color: '#22c55e' },
                { title: 'No', value: no, color: '#ef4444' },
                { title: 'Maybe', value: maybe, color: '#f97316' },
              ]}
              lineWidth={25}
            />
            <i
              className={`chart-label absolute left-1/2 top-1/2 text-[16px] sm:text-[22px] md:text-[12px] lg:text-[20px] xl:text-[22px] ${myColor}`}
            >
              {myLabel}
            </i>
          </div>
        </div>
      </td>
    </tr>
  );
};

export default StudentTile;
