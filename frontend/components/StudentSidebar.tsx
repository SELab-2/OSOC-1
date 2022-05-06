import { Fragment, useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { StudentBase, StudentData } from '../lib/types';
import useAxiosAuth from '../hooks/useAxiosAuth';
// import { axiosAuthenticated } from '../lib/axios';
import Endpoints from '../lib/endpoints';
import Select from 'react-select';
import FlatList from 'flatlist-react';
import { getSkills, parseError } from '../lib/requestUtils';
import StudentTile from './students/StudentTile';
import { SpinnerCircular } from 'spinners-react';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';
import { AxiosInstance } from 'axios';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;

/**
 * This is what StudentsSidebar expects as its argument
 */
type StudentsSidebarProps = {
  axiosAuthenticated: AxiosInstance;
  setError: (error: string) => void;
  refresh: [boolean, boolean];
  setRefresh: (refresh: [boolean, boolean]) => void;
  setStudentBase: (studentBase: StudentBase) => void;
  studentBase: StudentBase;
};

/**
 * Function that allows searching students by name
 * Depending on what function you pass for setStudents, the list will get replaced or appended to
 *
 * @param studentNameSearch       - (part of) the name of a student
 * @param skills                  - list of skills to include, student has to have one of these
 * @param studentSearchParameters - record containing the possible filters boolean
 * @param setStudents             - callback to set the results
 * @param setFilterAmount         - callback to set total amount of filtered results
 * @param state                   - holds page, loading, hasMoreItems, pageSize
 * @param setState                - set the state variable
 * @param setLoading              - set loading or not, this is not the same as the state loading due to styling bug otherwise
 * @param signal                  - AbortSignal for the axios request
 * @param setError                - callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 400 response
 */
async function searchStudent(
  axiosAuthenticated: AxiosInstance,
  studentNameSearch: string,
  skills: Array<{ value: string; label: string }>,
  studentSearchParameters: Record<string, boolean>,
  setStudents: (students: StudentBase[]) => void,
  setFilterAmount: (filterAmount: number) => void,
  state: {
    hasMoreItems: boolean;
    loading: boolean;
    page: number;
    pageSize: number;
  },
  setState: (state: {
    hasMoreItems: boolean;
    loading: boolean;
    page: number;
    pageSize: number;
  }) => void,
  setLoading: (loading: boolean) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  setLoading(true);

  // Fallback for no status selected
  if (!getStatusFilterList(studentSearchParameters)) {
    const newState = { ...state };
    newState.page = 0;
    newState.hasMoreItems = false;
    newState.loading = false;
    setState(newState);
    setStudents([] as StudentBase[]);
    setFilterAmount(0 as number);
    setLoading(false);
    return;
  }

  const edition = router.query.editionName as string;
  axiosAuthenticated
    .get<StudentData>('/' + edition + Endpoints.STUDENTS, {
      params: {
        name: studentNameSearch,
        includeSuggested: !studentSearchParameters.ExcludeSuggested,
        status: getStatusFilterList(studentSearchParameters),
        skills: skills.map((skill) => skill.label).join(','),
        alumnOnly: studentSearchParameters.OnlyAlumni,
        studentCoachOnly: studentSearchParameters.OnlyStudentCoach,
        unassignedOnly: studentSearchParameters.ExcludeAssigned,
        pageNumber: state.page,
        pageSize: state.pageSize,
      },
      signal: signal,
    })
    .then((response) => {
      const newState = { ...state };
      newState.page = state.page + 1;
      newState.hasMoreItems =
        response.data.totalLength > state.page * state.pageSize;
      newState.loading = false;
      setState(newState);
      // VERY IMPORTANT TO CHANGE STATE FIRST!!!!
      setStudents(response.data.collection as StudentBase[]);
      setFilterAmount(response.data.totalLength as number);
      setLoading(false);
    })
    .catch((err) => {
      const newState = { ...state };
      newState.loading = false;
      setState(newState);
      parseError(err, setError, signal, router);
      if (!signal.aborted) {
        setLoading(false);
      }
    });
}

/**
 * function to map the boolean status states onto a string
 * if no status states are set, will return a string containing a space to avoid calling backend default
 *
 * @param studentSearchParameters - Record that contains the four possible statuses: StatusYes, StatusNo, StatusMaybe, StatusUndecided
 */
function getStatusFilterList(
  studentSearchParameters: Record<string, boolean>
): string {
  const stringList = [];
  studentSearchParameters.StatusYes ? stringList.push('Yes') : null;
  studentSearchParameters.StatusNo ? stringList.push('No') : null;
  studentSearchParameters.StatusMaybe ? stringList.push('Maybe') : null;
  studentSearchParameters.StatusUndecided ? stringList.push('Undecided') : null;
  return stringList.join(',');
}

/**
 * This returns the StudentSidebar
 * Any page using this should add a DndProvider backend=\{HTML5Backend\} element
 * because student tiles can be dragged and must be in a DndProvider element to avoid errors
 * The DndProvider is needed even when the drag function is not needed or used on that page
 */
const StudentSidebar: React.FC<StudentsSidebarProps> = ({
  axiosAuthenticated,
  setError,
  refresh,
  setRefresh,
  setStudentBase,
  studentBase,
}: StudentsSidebarProps) => {
  const router = useRouter();
  const [showFilter, setShowFilter] = useState(true);

  const [skills, setSkills] = useState(
    [] as Array<{ value: string; label: string }>
  );

  const [skillOptions, setSkillOptions] = useState(
    [] as Array<{ value: string; label: string }>
  );

  // Split this to control when to call searchStudent
  const [studentNameSearch, setStudentNameSearch] = useState('' as string);

  const [filterAmount, setFilterAmount]: [
    number,
    (filterAmount: number) => void
  ] = useState(0);

  const [students, setStudents]: [
    StudentBase[],
    (students: StudentBase[]) => void
  ] = useState([] as StudentBase[]);

  const [loading, setLoading]: [boolean, (loading: boolean) => void] =
    useState<boolean>(true);

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
    setStudentNameSearch('');
  };

  /**
   * function to add new student results instead of overwriting old results
   * @param studentsList - list of students to add to all students
   */
  const updateStudents: (param: StudentBase[]) => void = (
    studentsList: StudentBase[]
  ) => {
    const newStudents = students
      ? [...students]
      : ([] as StudentBase[] as StudentBase[]);
    newStudents.push(...studentsList);
    setStudents(newStudents);
  };

  // useAxiosAuth();
  let controller = new AbortController();

  /**
   * when a search parameter changes, call the function to reload results
   * This will also get called on first render
   */
  useEffect(() => {
    return search(true);
  }, [studentSearchParameters, skills]);

  /**
   * This call refreshes the students without changing the current scroll position
   * The downside here is that if the students length is 500 elements, all 500 elements
   * need to be reloaded. This can be quite slow and does a lot of useless work.
   */
  useEffect(() => {
    if (refresh[0] && (refresh[1] || studentSearchParameters.ExcludeAssigned)) {
      const prevPageSize = state.pageSize;
      const prevPage = state.page;
      state.pageSize = state.page * state.pageSize;
      search();
      state.page = prevPage;
      state.pageSize = prevPageSize;
      setRefresh([false, refresh[1]]);
    }
  }, [refresh]);

  /**
   * Call to refresh students list from page 0 with current filters applied
   */
  const search = (refreshSkills = false) => {
    state.page = 0;
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    refreshSkills
      ? getSkills(axiosAuthenticated, setSkillOptions, signal, setError, router)
      : null;
    searchStudent(
      axiosAuthenticated,
      studentNameSearch,
      skills,
      studentSearchParameters,
      setStudents,
      setFilterAmount,
      state,
      setState,
      setLoading,
      signal,
      setError,
      router
    );
    return () => {
      controller.abort();
    };
  };

  /**
   * State for the infinite scroll FlatList
   * FlatList has a few bugs so there are two different loading parameters
   */
  const [state, setState] = useState({
    hasMoreItems: true,
    loading: true,
    page: 0,
    pageSize: 50,
  });

  /**
   * What to show when the students list is empty
   */
  const showBlank = () => {
    if (loading) {
      return (
        <div className="text-center">
          <p>Loading Students</p>
          <SpinnerCircular
            size={30}
            thickness={100}
            color="#FCB70F"
            secondaryColor="rgba(252, 183, 15, 0.4)"
            className="mx-auto"
          />
        </div>
      );
    }
    return <div className="text-center">No students found.</div>;
  };

  /**
   * Called when FlatList is scrolled to the bottom
   */
  const fetchData = () => {
    if (state.loading) {
      return;
    }
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    state.loading = true;
    searchStudent(
      axiosAuthenticated,
      studentNameSearch,
      skills,
      studentSearchParameters,
      updateStudents,
      setFilterAmount,
      state,
      setState,
      setLoading,
      signal,
      setError,
      router
    );
    return () => {
      controller.abort();
    };
  };

  return (
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
                value={studentNameSearch}
                onChange={(e) => setStudentNameSearch(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key == 'Enter') {
                    return search();
                  }
                }}
              />
              <i
                className="absolute bottom-1.5 right-2 z-10 h-[24px] w-[16px] opacity-20"
                onClick={() => {
                  return search();
                }}
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
                  options={skillOptions}
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
          <div className="col-span-full border-b-2 border-gray-400 pb-1 pr-2 text-right text-xs font-normal">
            {filterAmount + ' total results'}
          </div>
          <FlatList
            list={students}
            renderItem={(student: StudentBase) => (
              <StudentTile
                axiosAuthenticated={axiosAuthenticated}
                key={student.id}
                student={student}
                setStudentBase={setStudentBase}
                studentBase={studentBase}
              />
            )}
            renderWhenEmpty={showBlank} // let user know if initial data is loading or there is no data to show
            hasMoreItems={state.hasMoreItems}
            loadMoreItems={fetchData}
            state={state}
            paginationLoadingIndicator={<div />} // Use an empty div here to avoid showing the default since it has a bug
            paginationLoadingIndicatorPosition="center"
          />
          <div
            className={`${
              state.loading && state.page > 0 ? 'visible block' : 'hidden'
            } text-center`}
          >
            <p>Loading Students</p>
            <SpinnerCircular
              size={30}
              thickness={100}
              color="#FCB70F"
              secondaryColor="rgba(252, 183, 15, 0.4)"
              className="mx-auto"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentSidebar;
