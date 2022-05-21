import { PieChart } from 'react-minimal-pie-chart';
import { Icon } from '@iconify/react';
import {
  ItemTypes,
  StatusSuggestionStatus,
  StudentBaseList,
} from '../../lib/types';
import { useDrag } from 'react-dnd';
import { useEffect, useState } from 'react';
const check_mark = <Icon icon="bi:check-lg" />;
const question_mark = <Icon icon="bi:question-lg" />;
const x_mark = <Icon icon="bx:x" />;
const tilde_mark = <Icon icon="mdi:tilde" />;

/**
 * This is what StudentTile expects as its argument
 * @See StudentTile for more information
 */
type StudentProp = {
  studentInput: StudentBaseList;
  setStudentBase: (studentBase: StudentBaseList) => void;
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
 * @param setStudentBase - callback to set studentBase object,
 *                         this is needed to be able to click on a student
 *                         and then show it in the select students main screen
 */
const StudentTile: React.FC<StudentProp> = ({
  studentInput,
  setStudentBase,
}: StudentProp) => {
  // Need to set a project with all keys present to avoid the render code throwing undefined errors
  const [myStudent, setMyStudent]: [
    StudentBaseList,
    (myStudent: StudentBaseList) => void
  ] = useState(studentInput as StudentBaseList); // using different names to avoid confusion

  /**
   * Since polling is done in parent StudentSidebar.tsx, we only watch if
   * we get passed a different object than we were already showing.
   */
  useEffect(() => {
    if (JSON.stringify(studentInput) != JSON.stringify(myStudent)) {
      setMyStudent(studentInput as StudentBaseList);
    }
  }, [studentInput]);

  /**
   * This hook allows dragging the StudentTile
   * It can be dropped onto a ProjectTile and will then open assignment functionality
   */
  const [, drag] = useDrag(
    () => ({
      type: ItemTypes.STUDENTTILE,
      item: myStudent, // This is what will be 'given' to the project this is dropped on
      collect: (monitor) => ({
        isDragging: monitor.isDragging(), // TODO add isDragging styling
      }),
    }),
    [myStudent]
  );

  return (
    // TODO add a chevron dropdown to show possible roles, student coach, ...
    <div
      ref={drag}
      key={myStudent.id}
      onClick={() => setStudentBase(myStudent)}
    >
      <div
        className={`my-4 mx-1 flex cursor-pointer flex-row justify-between p-2 opacity-100 shadow-sm shadow-gray-500 hover:bg-osoc-neutral-bg hover:brightness-75`}
      >
        {/* basic student info */}
        <div className="flex w-3/4 flex-col justify-center">
          <div
            className={`flex flex-row ${
              myStudent.alumn || myStudent.possibleStudentCoach
                ? 'visible'
                : 'hidden h-0 w-0'
            }`}
          >
            <p
              className={`m-0 rounded-xl bg-osoc-bg text-xs ${
                myStudent.alumn ? 'visible mr-2 px-1' : 'hidden h-0 px-0'
              }`}
            >
              Alumn
            </p>
            <p
              className={`m-0 rounded-xl bg-osoc-btn-primary text-xs ${
                myStudent.possibleStudentCoach
                  ? 'visible px-1'
                  : 'hidden h-0 px-0'
              }`}
            >
              Coach
            </p>
          </div>
          <p className="pl-2">
            {myStudent.firstName + ' ' + myStudent.lastName}
          </p>
        </div>

        {/* TODO add some sort of counter to show total amount of suggestions for this student */}
        {/* holds the suggestions circle image thing + checkmark */}
        <div className="relative w-[10%]">
          <PieChart
            data={[
              {
                title: 'Yes',
                value:
                  myStudent.statusSuggestionCount[StatusSuggestionStatus.Yes] ||
                  0,
                color: '#22c55e', // I can't get tailwind config colors to work here
              },
              {
                title: 'No',
                value:
                  myStudent.statusSuggestionCount[StatusSuggestionStatus.No] ||
                  0,
                color: '#ef4444',
              },
              {
                title: 'Maybe',
                value:
                  myStudent.statusSuggestionCount[
                    StatusSuggestionStatus.Maybe
                  ] || 0,
                color: '#f97316',
              },
            ]}
            lineWidth={25}
          />
          <i
            className={`chart-label absolute left-1/2 top-1/2 text-[16px] sm:text-[28px] md:text-[12px] lg:text-[20px] xl:text-[20px] xl1920:text-[22px] ${
              chartHelper[myStudent.status]
                ? chartHelper[myStudent.status][1]
                : chartHelper['Default'][1]
            }`}
          >
            {chartHelper[myStudent.status]
              ? chartHelper[myStudent.status][0]
              : chartHelper['Default'][0]}
          </i>
        </div>
      </div>
    </div>
  );
};

export default StudentTile;
