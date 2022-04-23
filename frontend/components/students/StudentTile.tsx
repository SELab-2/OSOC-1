import { PieChart } from 'react-minimal-pie-chart';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faCheck,
  faQuestion,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import { Icon } from '@iconify/react';
import {
  ItemTypes,
  Project,
  StatusSuggestionStatus,
  Student,
  UUID,
} from '../../lib/types';
import { useDrag } from 'react-dnd';
const check_mark = <FontAwesomeIcon icon={faCheck} />;
const question_mark = <FontAwesomeIcon icon={faQuestion} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;
const tilde_mark = <Icon icon="mdi:tilde" />;

type StudentProp = {
  student: Student;
};

type statusSuggestionStatusToNumberDict = {
  [key in StatusSuggestionStatus]: number;
};

type stringToArrayDict = {
  [key: string]: [JSX.Element, string];
};

/**
 * This is used for the icon + icon color for the student status pie chart
 * If the student status is different from Yes, No, Maybe, Undecided then Default will be used
 */
const chartHelper = {
  Yes: [check_mark, 'text-check-green'],
  No: [x_mark, 'text-check-red'],
  Maybe: [question_mark, 'text-check-orange'],
  Undecided: [tilde_mark, 'text-check-gray'],
  Default: [tilde_mark, 'text-check-gray'],
} as stringToArrayDict;

const StudentTile: React.FC<StudentProp> = ({ student }: StudentProp) => {
  /**
   * This counts the different status suggestions to create the pie chart
   * The dict is empty without default values so when using suggestionCounts
   * you should add '|| 0' to avoid getting an undefined error
   */
  const suggestionCounts = {} as statusSuggestionStatusToNumberDict;
  student.statusSuggestions.forEach((suggestion) => {
    suggestionCounts[suggestion.status] =
      suggestionCounts[suggestion.status] + 1 || 1;
  });

  /**
   * This hook allows dragging the StudentTile
   * It can be dropped onto a ProjectTile and will then open assignment functionality
   */
  const [{ isDragging }, drag] = useDrag(
    () => ({
      type: ItemTypes.STUDENTTILE,
      item: student, // This is what will be 'given' to the project this is dropped on
      collect: (monitor) => ({
        isDragging: monitor.isDragging(), // TODO add isDragging styling
      }),
    }),
    []
  );

  return (
    // TODO add a chevron dropdown to show possible roles, student coach, ...
    // <tr key={student.id} className="">
    //   <td className="">
        <div
          ref={drag}
          key={student.id}
          className="my-4 mx-1 flex flex-row justify-between p-2 shadow-sm shadow-gray-500"
        >
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

          {/* TODO add some sort of counter to show total amount of suggestions for this student */}
          {/* holds the suggestions circle image thing + checkmark */}
          <div className="relative w-[10%]">
            <PieChart
              data={[
                {
                  title: 'Yes',
                  value: suggestionCounts[StatusSuggestionStatus.Yes] || 0,
                  color: '#22c55e', // I can't get tailwind config colors to work here
                },
                {
                  title: 'No',
                  value: suggestionCounts[StatusSuggestionStatus.No] || 0,
                  color: '#ef4444',
                },
                {
                  title: 'Maybe',
                  value: suggestionCounts[StatusSuggestionStatus.Maybe] || 0,
                  color: '#f97316',
                },
              ]}
              lineWidth={25}
            />
            <i
              className={`chart-label absolute left-1/2 top-1/2 text-[16px] sm:text-[22px] md:text-[12px] lg:text-[20px] xl:text-[20px] xl1920:text-[22px] ${
                chartHelper[student.status]
                  ? chartHelper[student.status][1]
                  : chartHelper['Default'][1]
              }`}
            >
              {chartHelper[student.status]
                ? chartHelper[student.status][0]
                : chartHelper['Default'][0]}
            </i>
          </div>
        </div>
     //   </td>
     // </tr>*
  );
};

export default StudentTile;
