import { Fragment, PropsWithChildren, useEffect, useState } from 'react';
import StudentTiles from './students/StudentTiles';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { StatusSuggestionStatus, Student } from '../lib/types';
import useAxiosAuth from '../hooks/useAxiosAuth';
import { axiosAuthenticated } from '../lib/axios';
import Endpoints from '../lib/endpoints';
import Select from 'react-select';
import FlatList from "flatlist-react";
import StudentTile from './students/StudentTile';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;

type StudentsSidebarProps = PropsWithChildren<unknown>;

/**
 * function that allows searching students by name
 *
 * @param studentNameSearch       - (part of) the name of a student
 * @param skills                  - list of skills to include, student has to have one of these
 * @param studentSearchParameters - record containing the possible filters boolean
 * @param setStudents             - callback to set the results
 */
// TODO show/handle errors
// TODO add Skills, alumn & studentcoach filter when implemented in backend
// TODO add pagination once fully implemented in backed
function searchStudent(
  studentNameSearch: string,
  skills: Array<{ value: string; label: string }>,
  studentSearchParameters: Record<string, boolean>,
  setStudents: (students: Student[]) => void
) {
  // console.log(skills);
  axiosAuthenticated
    .get(Endpoints.STUDENTS, {
      params: {
        name: studentNameSearch,
        includeSuggested: !studentSearchParameters.ExcludeSuggested,
        status: getStatusFilterList(studentSearchParameters),
      },
    })
    .then((response) => {
      setStudents(response.data as Student[]);
      console.log(response);
    })
    .catch((ex) => {
      console.log(ex);
    });
}

/**
 * function to map the boolean status states onto a string
 *
 * @param studentSearchParameters - Record that contains the four possible statuses: StatusYes, StatusNo, StatusMaybe, StatusUndecided
 */
function getStatusFilterList(
  studentSearchParameters: Record<string, boolean>
): string {
  let stringList = '';
  stringList += studentSearchParameters.StatusYes ? 'Yes,' : '';
  stringList += studentSearchParameters.StatusNo ? 'No,' : '';
  stringList += studentSearchParameters.StatusMaybe ? 'Maybe,' : '';
  stringList += studentSearchParameters.StatusUndecided ? 'Undecided,' : '';
  return stringList;
}

// TODO add documentation
const StudentSidebar: React.FC<StudentsSidebarProps> = () => {
  const [showFilter, setShowFilter] = useState(true);

  // Split this from studentSearchParameters to avoid typing hacks
  const [skills, setSkills] = useState(
    [] as Array<{ value: string; label: string }>
  );

  // Split this to avoid making new object every type action & control when to call filter
  const [studentNameSearch, setStudentNameSearch] = useState('' as string);

  const [students, setStudents]: [Student[], (students: Student[]) => void] =
    useState([] as Student[]);
  const [loading, setLoading]: [boolean, (loading: boolean) => void] =
    useState<boolean>(true); // TODO use this for styling
  const [error, setError]: [string, (error: string) => void] = useState(''); // TODO use this for actual error handling

  const defaultStudentSearchParameters = {
    StatusYes: true,
    StatusNo: true,
    StatusMaybe: true,
    StatusUndecided: true,
    OnlyAlumni: false,
    OnlyStudentCoach: false,
    ExcludeSuggested: false,
    ExcludeAssigned: false,
  } as Record<string, boolean>;

  const [studentSearchParameters, setStudentSearchParameters] = useState(
    defaultStudentSearchParameters
  );

  const handleSearchChange = (parameter: string, value: boolean) => {
    const newStudentSearchParameters = { ...studentSearchParameters };
    newStudentSearchParameters[parameter] = value;
    setStudentSearchParameters(newStudentSearchParameters);
  };

  const clearFilters = () => {
    setStudentSearchParameters(defaultStudentSearchParameters);
    setSkills([] as Array<{ value: string; label: string }>);
  };

  useAxiosAuth();
  // TODO this should just call searchStudent since this ignores current filters on reload
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

  /**
   * when a search parameter changes, call the function to reload results
   */
  useEffect(() => {
    searchStudent(
      studentNameSearch,
      skills,
      studentSearchParameters,
      setStudents
    );
  }, [studentSearchParameters, skills]);

  const state = {
    hasMoreItems: true,
    offset: 0,
    loading: false // important so the right blank message is shown from the start
  }

  const showBlank = () => {
    if (students.length === 0 && state.loading) {
      return <div>Loading list...</div>
    }
    return <div>No students found.</div>
  }


  // TODO make actual request with pagination parameters via searchStudent but without overwriting list
  const fetchData = () => {
    // TODO set state to correct values
    console.log("loadmore");
  }

  return (
    // TODO test with a long list for autoscroll etc this should be separate from projects scroll but no longer
    // holds searchbar + hide filter button
    <div className="sidebar mt-[50px] max-h-screen py-4 sm:mt-0">
      <div className="flex max-h-[calc(100vh-32px)] flex-col">
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
                    searchStudent(
                      studentNameSearch,
                      skills,
                      studentSearchParameters,
                      setStudents
                    );
                  }
                }}
              />
              <i
                className="absolute bottom-1.5 right-2 z-10 h-[24px] w-[16px] opacity-20"
                onClick={() =>
                  searchStudent(
                    studentNameSearch,
                    skills,
                    studentSearchParameters,
                    setStudents
                  )
                }
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
                  value={skills}
                  options={[
                    { value: 'chocolate', label: 'Chocolate' },
                    { value: 'strawberry', label: 'Strawberry' },
                    { value: 'vanilla', label: 'Vanilla' },
                  ]} // TODO fix this once backend has getAllSKills endpoint implemented
                  placeholder="Select skills"
                  onChange={(e) =>
                    setSkills(
                      e.map((x) => {
                        return { value: x.value, label: x.label };
                      })
                    )
                  }
                />
              </Fragment>
            </div>
            <button
              className="ml-4 bg-gray-300 px-2 text-sm text-black"
              onClick={() => clearFilters()}
            >
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
                  checked={studentSearchParameters.OnlyAlumni}
                  onChange={() =>
                    handleSearchChange(
                      'OnlyAlumni',
                      !studentSearchParameters.OnlyAlumni
                    )
                  }
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
                  checked={studentSearchParameters.OnlyStudentCoach}
                  onChange={() =>
                    handleSearchChange(
                      'OnlyStudentCoach',
                      !studentSearchParameters.OnlyStudentCoach
                    )
                  }
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
                  checked={studentSearchParameters.ExcludeSuggested}
                  onChange={() =>
                    handleSearchChange(
                      'ExcludeSuggested',
                      !studentSearchParameters.ExcludeSuggested
                    )
                  }
                />
                <label
                  htmlFor="toggleSuggested"
                  className="toggle-label block h-5 cursor-pointer overflow-hidden rounded-full border-2 border-gray-300 bg-white"
                />
              </div>
              <label htmlFor="toggleSuggested" className="text-sm">
                Exclude students you&rsquo;ve made a suggestion for
              </label>
            </div>

            <div className="flex w-full flex-row flex-wrap">
              <div className="relative mr-2 inline-block w-10 select-none transition duration-200 ease-in">
                <input
                  type="checkbox"
                  name="toggleAssigned"
                  id="toggleAssigned"
                  className="toggle-checkbox absolute m-1 h-3 w-3 cursor-pointer appearance-none rounded-full bg-gray-300"
                  checked={studentSearchParameters.ExcludeAssigned}
                  onChange={() =>
                    handleSearchChange(
                      'ExcludeAssigned',
                      !studentSearchParameters.ExcludeAssigned
                    )
                  }
                />
                <label
                  htmlFor="toggleAssigned"
                  className="toggle-label block h-5 cursor-pointer overflow-hidden rounded-full border-2 border-gray-300 bg-white"
                />
              </div>
              <label htmlFor="toggleAssigned" className="text-sm">
                Exclude students already assigned
              </label>
            </div>
          </div>

          <p className="justify-self-start">Filter on status</p>

          {/* holds the filter buttons */}
          <div className="flex flex-col justify-center border-y-2 py-1.5 lg:flex-row">
            <div className="flex grow flex-row justify-between lg:mr-[2.75%] lg:w-1">
              <button
                className={`${
                  studentSearchParameters.StatusYes
                    ? 'bg-osoc-btn-primary'
                    : 'bg-gray-300'
                } w-[44%] text-sm text-black`}
                onClick={() =>
                  handleSearchChange(
                    'StatusYes',
                    !studentSearchParameters.StatusYes
                  )
                }
              >
                Yes
              </button>
              <button
                className={`${
                  studentSearchParameters.StatusNo
                    ? 'bg-osoc-btn-primary'
                    : 'bg-gray-300'
                } w-[44%] text-sm text-black`}
                onClick={() =>
                  handleSearchChange(
                    'StatusNo',
                    !studentSearchParameters.StatusNo
                  )
                }
              >
                No
              </button>
            </div>
            <div className="mt-2.5 flex grow flex-row justify-between lg:ml-[2.75%] lg:mt-0 lg:w-1">
              <button
                className={`${
                  studentSearchParameters.StatusMaybe
                    ? 'bg-osoc-btn-primary'
                    : 'bg-gray-300'
                } w-[44%] text-sm text-black`}
                onClick={() =>
                  handleSearchChange(
                    'StatusMaybe',
                    !studentSearchParameters.StatusMaybe
                  )
                }
              >
                Maybe
              </button>
              <button
                className={`${
                  studentSearchParameters.StatusUndecided
                    ? 'bg-osoc-btn-primary'
                    : 'bg-gray-300'
                } w-[44%] text-sm text-black`}
                onClick={() =>
                  handleSearchChange(
                    'StatusUndecided',
                    !studentSearchParameters.StatusUndecided
                  )
                }
              >
                Undecided
              </button>
            </div>
          </div>
        </div>

        {/* These are the student tiles */}
        <div className="max-h-[100%] grow overflow-y-auto">

          <FlatList
              list={students}
              renderItem={(student: Student) => <StudentTile key={student.id} student={student} />}
              renderWhenEmpty={showBlank} // let user know if initial data is loading or there is no data to show
              hasMoreItems={state.hasMoreItems}
              loadMoreItems={fetchData}
              paginationLoadingIndicator={<div>Loading Students</div>} // TODO style this
              paginationLoadingIndicatorPosition="center"
          />
        </div>
      </div>
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