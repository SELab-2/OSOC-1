import { Fragment, PropsWithChildren, useEffect, useState } from 'react';
import StudentTiles from './students/StudentTiles';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { Menu, Transition } from '@headlessui/react';
import { ChevronDownIcon } from '@heroicons/react/solid';
import { StatusSuggestionStatus, Student } from '../lib/types';
import useAxiosAuth from '../hooks/useAxiosAuth';
import { axiosAuthenticated } from '../lib/axios';
import Endpoints from '../lib/endpoints';
import { func } from 'prop-types';
import Select from 'react-select';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;

type StudentsSidebarProps = PropsWithChildren<unknown>;

// TODO show/handle errors
// TODO this is full code duplication of projects search atm
/**
 * function that allows searching students by name
 *
 * @param StudentNameSearch = (part of) the name of a student
 * @param setStudents       = callback to set the results
 */
function searchStudentName(
  StudentNameSearch: string,
  setStudents: (students: Student[]) => void
) {
  axiosAuthenticated
    .get(Endpoints.STUDENTS, {
      params: { name: StudentNameSearch },
    })
    .then((response) => {
      setStudents(response.data as Student[]);
    })
    .catch((ex) => {
      console.log(ex);
    });
}

// TODO no actual functionality implemented yet
const StudentSidebar: React.FC<StudentsSidebarProps> = () => {
  const [showFilter, setShowFilter] = useState(true);
  const [StudentNameSearch, setStudentNameSearch] = useState('' as string);
  //TODO all these toggles and button states should be one object
  const [StatusYes, setStatusYes] = useState(false);
  const [StatusNo, setStatusNo] = useState(false);
  const [StatusMaybe, setStatusMaybe] = useState(false);
  const [StatusUndecided, setStatusUndecided] = useState(false);
  const [OnlyAlumni, setOnlyAlumni] = useState(false);
  const [OnlyStudentCoach, setOnlyStudentCoach] = useState(false);
  const [IncludeSuggested, setIncludeSuggested] = useState(false);
  const [Roles, setRoles] = useState([] as string[]);
  const [students, setStudents]: [Student[], (students: Student[]) => void] =
    useState([] as Student[]);
  const [loading, setLoading]: [boolean, (loading: boolean) => void] =
    useState<boolean>(true); // TODO use this for styling
  const [error, setError]: [string, (error: string) => void] = useState(''); // TODO use this for actual error handling

  useAxiosAuth();
  useEffect(() => {
    axiosAuthenticated
      .get<Student[]>(Endpoints.STUDENTS)
      .then((response) => {
        setStudents(response.data as Student[]);
        setLoading(false);
      })
      .catch((ex) => {
        const error =
          ex.response.status === 404
            ? 'Resource Not found'
            : 'An unexpected error has occurred';
        setError(error);
        setLoading(false);
      });
  }, []);

  return (
    // TODO test with a long list for autoscroll etc this should be separate from projects scroll but no longer
    // holds searchbar + hide filter button
    <div className="mt-[50px] sm:mt-0">
      <div className="mb-3 flex w-full flex-col items-center justify-between lg:flex-row">
        {/* TODO add an easy reset/undo search button */}
        {/* The students searchbar */}
        <div className="justify-left md:w-[calc(100% - 200px)] mb-3 flex w-[80%] md:ml-0 lg:mb-0 ">
          <div className="relative w-full">
            <input
              type="text"
              className="form-control m-0 block w-full rounded border border-solid border-gray-300 bg-white bg-clip-padding px-3 py-1.5 text-base font-normal text-gray-700 transition ease-in-out focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none"
              id="StudentsSearch"
              placeholder="Search students by name"
              onChange={(e) => setStudentNameSearch(e.target.value)}
              onKeyPress={(e) => {
                if (e.key == 'Enter') {
                  searchStudentName(StudentNameSearch, setStudents);
                }
              }}
            />
            <i
              className="absolute bottom-1.5 right-2 z-10 h-[24px] w-[16px] opacity-20"
              onClick={() => searchStudentName(StudentNameSearch, setStudents)}
            >
              {magnifying_glass}
            </i>
          </div>
        </div>

        {/* Show/hide filter button */}
        <button
          className="justify-right ml-2 min-w-[120px] rounded-sm bg-check-orange px-2 py-1 text-sm font-medium text-white shadow-sm shadow-gray-300"
          type="submit"
          onClick={() => setShowFilter(!showFilter)}
        >
          {showFilter ? 'Hide Filters' : 'Show Filters'}
        </button>
      </div>

      {/* Holds the filter controls */}
      <div
        className={`flex w-full flex-col rounded-sm border-2 border-check-orange ${
          showFilter ? 'visible my-2 h-auto p-2' : 'my-0 hidden h-0 p-0'
        }`}
      >
        {/* holds drowndown, deselect all, clear all filters */}
        <div className="flex w-full flex-row justify-between">
          {/* This is the dropdown menu to select skills to filter on */}
          <div
            onClick={(e) => e.stopPropagation()}
            onMouseDown={(e) => e.stopPropagation()}
            // className={`max-w-[60%]`}
          >
            <Fragment>
              {/* TODO fix this becoming wider when something is selected */}
              {/* TODO fix this looking horrible when a lot is selected */}
              <Select
                className="basic-single"
                classNamePrefix="select"
                isDisabled={false}
                isLoading={false}
                isClearable={true}
                isRtl={false}
                isSearchable={true}
                isMulti={true}
                name="Skills"
                options={[
                  { value: 'chocolate', label: 'Chocolate' },
                  { value: 'strawberry', label: 'Strawberry' },
                  { value: 'vanilla', label: 'Vanilla' },
                ]}
                placeholder="Select Skills"
                onChange={(e) => console.log(e.values)}
              />
            </Fragment>
          </div>
          <button className="ml-4 bg-gray-300 px-2 text-sm text-black">
            Clear all filters
          </button>
        </div>

        {/* holds the toggles */}
        <div className="my-2 flex w-full flex-col space-y-2">
          <div className="flex w-full flex-row flex-wrap">
            <div className="relative mr-2 inline-block w-10 select-none transition duration-200 ease-in">
              <input
                type="checkbox"
                name="toggleAlumni"
                id="toggleAlumni"
                className="toggle-checkbox absolute m-1 h-3 w-3 cursor-pointer appearance-none rounded-full bg-gray-300"
                checked={OnlyAlumni}
                onChange={() => setOnlyAlumni(!OnlyAlumni)}
              />
              <label
                htmlFor="toggleAlumni"
                className="toggle-label block h-5 cursor-pointer overflow-hidden rounded-full border-2 border-gray-300 bg-white"
              />
            </div>
            <label htmlFor="toggleAlumni" className="text-sm">
              Only Alumni
            </label>
          </div>

          <div className="flex w-full flex-row flex-wrap">
            <div className="relative mr-2 inline-block w-10 select-none transition duration-200 ease-in">
              <input
                type="checkbox"
                name="toggleStudentCoach"
                id="toggleStudentCoach"
                className="toggle-checkbox absolute m-1 h-3 w-3 cursor-pointer appearance-none rounded-full bg-gray-300"
                checked={OnlyStudentCoach}
                onChange={() => setOnlyStudentCoach(!OnlyStudentCoach)}
              />
              <label
                htmlFor="toggleStudentCoach"
                className="toggle-label block h-5 cursor-pointer overflow-hidden rounded-full border-2 border-gray-300 bg-white"
              />
            </div>
            <label htmlFor="toggleStudentCoach" className="text-sm">
              Only Student Coach Volunteers
            </label>
          </div>

          <div className="flex w-full flex-row flex-wrap">
            <div className="relative mr-2 inline-block w-10 select-none transition duration-200 ease-in">
              <input
                type="checkbox"
                name="toggleSuggested"
                id="toggleSuggested"
                className="toggle-checkbox absolute m-1 h-3 w-3 cursor-pointer appearance-none rounded-full bg-gray-300"
                checked={IncludeSuggested}
                onChange={() => setIncludeSuggested(!IncludeSuggested)}
              />
              <label
                htmlFor="toggleSuggested"
                className="toggle-label block h-5 cursor-pointer overflow-hidden rounded-full border-2 border-gray-300 bg-white"
              />
            </div>
            <label htmlFor="toggleSuggested" className="text-sm">
              Include students you&rsquo;ve made a suggestion for
            </label>
          </div>
        </div>

        <p className="justify-self-start">Filter on status</p>

        {/* holds the filter buttons */}
        <div className="flex flex-col justify-center border-y-2 py-1.5 lg:flex-row">
          <div className="flex grow flex-row justify-between lg:mr-[2.75%] lg:w-1">
            <button
              className={`${
                StatusYes ? 'bg-osoc-btn-primary' : 'bg-gray-300'
              } w-[44%] text-sm text-black`}
              onClick={() => setStatusYes(!StatusYes)}
            >
              Yes
            </button>
            <button
              className={`${
                StatusNo ? 'bg-osoc-btn-primary' : 'bg-gray-300'
              } w-[44%] text-sm text-black`}
              onClick={() => setStatusNo(!StatusNo)}
            >
              No
            </button>
          </div>
          <div className="mt-2.5 flex grow flex-row justify-between lg:ml-[2.75%] lg:mt-0 lg:w-1">
            <button
              className={`${
                StatusMaybe ? 'bg-osoc-btn-primary' : 'bg-gray-300'
              } w-[44%] text-sm text-black`}
              onClick={() => setStatusMaybe(!StatusMaybe)}
            >
              Maybe
            </button>
            <button
              className={`${
                StatusUndecided ? 'bg-osoc-btn-primary' : 'bg-gray-300'
              } w-[44%] text-sm text-black`}
              onClick={() => setStatusUndecided(!StatusUndecided)}
            >
              Undecided
            </button>
          </div>
        </div>
      </div>

      {/* These are the student tiles */}
      <StudentTiles students={students} />
    </div>
  );
};

export default StudentSidebar;

// Temporary fake data to test with
const students_fake = [
  {
    id: '1',
    firstName: 'FNaam1',
    lastName: 'LNaam1',
    status: 'Undecided',
    statusSuggestions: [
      {
        coachId: '100',
        status: StatusSuggestionStatus.Yes,
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '101',
        status: StatusSuggestionStatus.Yes,
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: StatusSuggestionStatus.No,
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: true,
  },
  {
    id: '2',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: 'Undecided',
    statusSuggestions: [
      {
        coachId: '100',
        status: StatusSuggestionStatus.Maybe,
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: StatusSuggestionStatus.Yes,
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: StatusSuggestionStatus.No,
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
  {
    id: '3',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: StatusSuggestionStatus.Yes,
    statusSuggestions: [
      {
        coachId: '100',
        status: StatusSuggestionStatus.Maybe,
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: StatusSuggestionStatus.Yes,
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: StatusSuggestionStatus.No,
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
  {
    id: '4',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: StatusSuggestionStatus.No,
    statusSuggestions: [
      {
        coachId: '100',
        status: StatusSuggestionStatus.Maybe,
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: StatusSuggestionStatus.No,
        motivation: 'Dit is een motivatie voor no',
      },
      {
        coachId: '102',
        status: StatusSuggestionStatus.No,
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
  {
    id: '5',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: StatusSuggestionStatus.Maybe,
    statusSuggestions: [
      {
        coachId: '100',
        status: StatusSuggestionStatus.Maybe,
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: StatusSuggestionStatus.Yes,
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: StatusSuggestionStatus.No,
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
];
