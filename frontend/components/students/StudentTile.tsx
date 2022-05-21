import { PieChart } from 'react-minimal-pie-chart';
import { Icon } from '@iconify/react';
import {
  ItemTypes,
  StatusSuggestionStatus,
  StudentBaseExtra,
  StudentBaseList,
  UUID,
} from '../../lib/types';
import { useDrag } from 'react-dnd';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import { convertStudentBaseListToExtra } from '../../lib/conversionUtils';
import { parseError } from '../../lib/requestUtils';
const check_mark = <Icon icon="bi:check-lg" />;
const question_mark = <Icon icon="bi:question-lg" />;
const x_mark = <Icon icon="bx:x" />;
const tilde_mark = <Icon icon="mdi:tilde" />;
const chevron_down = <Icon icon="akar-icons:circle-chevron-down" />;
const chevron_up = <Icon icon="akar-icons:circle-chevron-up" />;

/**
 * This is what StudentTile expects as its argument
 * @See StudentTile for more information
 */
type StudentProp = {
  studentInput: StudentBaseList;
  setStudentBase: (studentBase: StudentBaseList) => void;
  setShowSidebar: (showSidebar: boolean) => void;
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

function getStudentExtra(
  studentId: UUID,
  setMyStudentExtra: (myStudentExtra: StudentBaseExtra) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .get<StudentBaseExtra>(`/${edition}${Endpoints.STUDENTS}/${studentId}`, {
      params: { view: 'Extra' },
      signal: signal,
    })
    .then((response) => {
      setMyStudentExtra(response.data as StudentBaseExtra);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

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
  setShowSidebar,
}: StudentProp) => {
  const [myStudentList, setMyStudentList]: [
    StudentBaseList,
    (myStudentList: StudentBaseList) => void
  ] = useState(studentInput as StudentBaseList); // using different names to avoid confusion
  const [isOpen, setIsOpen] = useState(false);
  const [myStudentExtra, setMyStudentExtra] = useState(
    convertStudentBaseListToExtra(studentInput) as StudentBaseExtra
  );
  // const [myStudentPulled, setMyStudentPulled] = useState(studentInput);

  const router = useRouter();
  let controller = new AbortController();
  useAxiosAuth();

  /**
   * Since polling is done in parent StudentSidebar.tsx, we only watch if
   * we get passed a different object than we were already showing.
   */
  useEffect(() => {
    if (JSON.stringify(studentInput) != JSON.stringify(myStudentList)) {
      setMyStudentList(studentInput as StudentBaseList);
    }
  }, [studentInput]);

  useEffect(() => {
    if (isOpen && router.isReady) {
      controller.abort();
      controller = new AbortController();
      const signal = controller.signal;
      getStudentExtra(
        myStudentList.id,
        setMyStudentExtra,
        signal,
        () => null,
        router
      );
    }
  }, [isOpen, router.isReady]);

  /**
   * This hook allows dragging the StudentTile
   * It can be dropped onto a ProjectTile and will then open assignment functionality
   */
  const [{ isDragging }, drag] = useDrag(
    () => ({
      type: ItemTypes.STUDENTTILE,
      item: myStudentList, // This is what will be 'given' to the project this is dropped on
      collect: (monitor) => ({
        isDragging: monitor.isDragging(), // WONTFIX add isDragging styling
      }),
    }),
    [myStudentList]
  );

  useEffect(() => {
    if (isDragging) {
      setShowSidebar(false);
    }
  }, [isDragging]);

  return (
    <div
      ref={drag}
      key={myStudentList.id}
      onClick={() => setStudentBase(myStudentList)}
    >
      <div className="my-4 mx-1 flex cursor-pointer flex-col justify-between p-2 opacity-100 shadow-sm shadow-gray-500 hover:bg-osoc-neutral-bg hover:brightness-75">
        <div className="flex flex-row">
          {/* basic student info */}
          <div className="flex w-3/4 flex-col justify-center">
            <div
              className={`flex flex-row ${
                myStudentList.alumn || myStudentList.possibleStudentCoach
                  ? 'visible'
                  : 'hidden h-0 w-0'
              }`}
            >
              <p
                className={`m-0 rounded-xl bg-osoc-bg text-xs ${
                  myStudentList.alumn ? 'visible mr-2 px-1' : 'hidden h-0 px-0'
                }`}
              >
                Alumn
              </p>
              <p
                className={`m-0 rounded-xl bg-osoc-btn-primary text-xs ${
                  myStudentList.possibleStudentCoach
                    ? 'visible px-1'
                    : 'hidden h-0 px-0'
                }`}
              >
                Coach
              </p>
            </div>
            <div className="flex flex-row">
              <div className="flex flex-col justify-center">
                <i
                  className="icon-chevron-gray"
                  onClick={(e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    console.log('clicked');
                    setIsOpen(!isOpen);
                  }}
                >
                  {!isOpen && chevron_down}
                  {isOpen && chevron_up}
                </i>
              </div>
              <p className="pl-1">
                {myStudentList.firstName + ' ' + myStudentList.lastName}
              </p>
            </div>
          </div>

          {/* WONTFIX add some sort of counter to show total amount of suggestions for this student */}
          {/* holds the suggestions circle image thing + checkmark */}
          <div className="relative w-[10%]">
            <PieChart
              data={[
                {
                  title: 'Yes',
                  value:
                    myStudentList.statusSuggestionCount[
                      StatusSuggestionStatus.Yes
                    ] || 0,
                  color: '#22c55e', // I can't get tailwind config colors to work here
                },
                {
                  title: 'No',
                  value:
                    myStudentList.statusSuggestionCount[
                      StatusSuggestionStatus.No
                    ] || 0,
                  color: '#ef4444',
                },
                {
                  title: 'Maybe',
                  value:
                    myStudentList.statusSuggestionCount[
                      StatusSuggestionStatus.Maybe
                    ] || 0,
                  color: '#f97316',
                },
              ]}
              lineWidth={25}
            />
            <i
              className={`chart-label absolute left-1/2 top-1/2 text-[16px] sm:text-[28px] md:text-[12px] lg:text-[20px] xl:text-[20px] xl1920:text-[22px] ${
                chartHelper[myStudentList.status]
                  ? chartHelper[myStudentList.status][1]
                  : chartHelper['Default'][1]
              }`}
            >
              {chartHelper[myStudentList.status]
                ? chartHelper[myStudentList.status][0]
                : chartHelper['Default'][0]}
            </i>
          </div>
        </div>
        {isOpen && (
          <div className="mt-2 flex flex-col">
            <div className="flex flex-row">
              <p className="pr-1">Skills:</p>
              <div className="flex flex-col">
                {myStudentExtra.skills.map((skill) => (
                  <p key={skill.skillName}>{skill.skillName}</p>
                ))}
              </div>
            </div>
            <div>
              <p>Suggestions:</p>
              <p className="ml-4">
                Yes: {myStudentExtra.statusSuggestionCount.Yes || 0}, No:{' '}
                {myStudentExtra.statusSuggestionCount.No || 0}, Maybe:{' '}
                {myStudentExtra.statusSuggestionCount.Maybe || 0}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentTile;
