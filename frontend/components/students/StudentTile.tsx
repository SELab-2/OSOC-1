import { PieChart } from 'react-minimal-pie-chart';
import { Icon } from '@iconify/react';
import { ItemTypes, StatusSuggestionStatus, Student } from '../../lib/types';
import { useDrag } from 'react-dnd';
const check_mark = <Icon icon="bi:check-lg" />;
const question_mark = <Icon icon="bi:question-lg" />;
const x_mark = <Icon icon="bx:x" />;
const tilde_mark = <Icon icon="mdi:tilde" />;

/**
 * This is what StudentTile expects as its argument
 * @See StudentTile for more information
 */
type StudentProp = {
  student: Student;
};

/**
 * Helper type to count the suggestions for the pie char
 */
type statusSuggestionStatusToNumberDict = {
  [key in StatusSuggestionStatus]: number;
};

/**
 * Helper type to use the correct icon and color in the pie chart
 */
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

/**
 * This creates the tiles show in the StudentSidebar
 * @param student - The student whose information should be shown
 */
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
    <div ref={drag} key={student.id}>
      <div
        className={`my-4 mx-1 flex flex-row justify-between p-2 opacity-100 shadow-sm shadow-gray-500`}
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
            className={`chart-label absolute left-1/2 top-1/2 text-[16px] sm:text-[28px] md:text-[12px] lg:text-[20px] xl:text-[20px] xl1920:text-[22px] ${
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
    </div>
  );
};

export default StudentTile;
