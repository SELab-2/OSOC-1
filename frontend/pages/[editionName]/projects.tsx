import type { NextPage } from 'next';
import Header from '../../components/Header';
import StudentSidebar from '../../components/StudentSidebar';
import { Icon } from '@iconify/react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useEffect, useRef, useState } from 'react';
import {
  Conflict,
  ProjectBase,
  ProjectData,
  StudentBase,
  Url,
  UserRole,
  UUID,
  conflictMapType,
} from '../../lib/types';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import Popup from 'reactjs-popup';
import ProjectTile from '../../components/projects/ProjectTile';
import ProjectPopup, {
  defaultprojectForm,
} from '../../components/projects/ProjectPopup';
import FlatList from 'flatlist-react';
import useUser from '../../hooks/useUser';
import { SpinnerCircular } from 'spinners-react';
import Error from '../../components/Error';
import { getUrlMap, parseError } from '../../lib/requestUtils';
import RouteProtection from '../../components/RouteProtection';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';
import usePoll from 'react-use-poll';
import useOnScreen from '../../hooks/useOnScreen';
import PersistLogin from '../../components/PersistLogin';
import ProjectConflict from '../../components/projects/ProjectConflict';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;
const arrow_out = <Icon icon="bi:arrow-right-circle" />;
const arrow_in = <Icon icon="bi:arrow-left-circle" />;

/**
 * function that allows searching projects by name
 *
 * @param projectSearch - (part of) the name of a project
 * @param setProjects   - callback to set the results
 * @param state - holds page, loading, hasMoreItems, pageSize
 * @param setState - set the state variable
 * @param setLoading - set loading or not, this is not the same as the state loading due to styling bug otherwise
 * @param signal - AbortSignal for the axios request
 * @param setError - callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 400 response
 */
function searchProject(
  projectSearch: string,
  setProjects: (projects: ProjectBase[]) => void,
  state: {
    hasMoreItems: boolean;
    page: number;
    pageSize: number;
    loading: boolean;
  },
  setState: (state: {
    hasMoreItems: boolean;
    page: number;
    pageSize: number;
    loading: boolean;
  }) => void,
  setLoading: (loading: boolean) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  setLoading(true);
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .get<ProjectData>('/' + edition + Endpoints.PROJECTS, {
      params: {
        name: projectSearch,
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
      // VERY IMPORTANT TO CHANGE STATE FIRST!!!!
      setProjects(response.data.collection as ProjectBase[]);
      setState(newState);
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

async function searchConflicts(
  setProjects: (projects: ProjectBase[]) => void,
  setConflictStudents: (conflictStudents: UUID[]) => void,
  conflictMap: conflictMapType,
  setConflictMap: (map: conflictMapType) => void,
  setLoading: (loading: boolean) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  setLoading(true);
  const edition = router.query.editionName as string;

  try {
    const conflictsResponse = await axiosAuthenticated.get<Conflict[]>(
      '/' + edition + Endpoints.CONFLICTS,
      {
        signal: signal,
      }
    );
    const conflicts = conflictsResponse.data as Conflict[];

    const newConflictMap = new Map() as conflictMapType;
    conflictMap.forEach((value, key) => {
      const newValue = { ...value };
      newValue.amount = 1;
      newConflictMap.set(key, newValue);
    });

    const newStudents = conflicts
      .map((conflict) => conflict.student as Url)
      .filter(
        (student) => !newConflictMap.has(student.split('/').pop() as UUID)
      );

    const newConflictStudents = new Map<Url, StudentBase>();
    await getUrlMap<StudentBase>(
      newStudents,
      newConflictStudents,
      signal,
      setError,
      router
    );

    conflicts.forEach((conflict) => {
      const studId = conflict.student.split('/').pop() as UUID;
      const value = newConflictMap.get(studId);
      if (value) {
        conflict.projects.forEach((item) =>
          (value.projectUrls as Set<Url>).add(item)
        );
        value.amount = conflict.projects.length;
        newConflictMap.set(studId, value);
      } else {
        const newValue = {
          projectUrls: new Set<Url>(conflict.projects),
          amount: conflict.projects.length,
          student: newConflictStudents.get(conflict.student) as StudentBase,
        };
        newConflictMap.set(studId, newValue);
      }
    });

    setConflictMap(newConflictMap);
  } catch (err) {
    parseError(err, setError, signal, router);
    if (!signal.aborted) {
      setLoading(false);
    }
  }
}

/**
 * Projects page for OSOC application
 * @returns Projects page
 */
const Projects: NextPage = () => {
  const [user] = useUser();
  const router = useRouter();
  // Used to hide / show the students sidebar on screen width below 768px
  const [showSidebar, setShowSidebar] = useState(false);
  const [showCreateProject, setShowCreateProject] = useState(false);
  const [projectSearch, setProjectSearch] = useState('' as string);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showConflicts, setShowConflicts] = useState(false);
  const [conflictStudents, setConflictStudents] = useState([] as UUID[]);
  const [conflictMap, setConflictMap] = useState(new Map() as conflictMapType);
  // const [conflictsLoaded, setConflictsLoaded] = useState(false);
  const [projects, setProjects]: [
    ProjectBase[],
    (projects: ProjectBase[]) => void
  ] = useState([] as ProjectBase[]);
  const [projectForm, setProjectForm] = useState(
    JSON.parse(JSON.stringify({ ...defaultprojectForm }))
  );
  const elementRef1 = useRef<HTMLDivElement>(null);
  const isOnScreen = useOnScreen(elementRef1);

  let controller = new AbortController();
  useAxiosAuth();

  useEffect(() => {
    state.page = 0;
    return search();
  }, []);

  useEffect(() => {
    if (!showConflicts) {
      setConflictMap(new Map() as conflictMapType);
      setConflictStudents([] as UUID[]);
    }
  }, [showConflicts]);

  /**
   * function to add new project results instead of overwriting old results
   * @param projectsList - list of projects to add to all projects
   */
  const updateProjects: (param: ProjectBase[]) => void = (
    projectsList: ProjectBase[]
  ) => {
    const newProjects = projects
      ? [...projects]
      : ([] as ProjectBase[] as ProjectBase[]);
    newProjects.push(...projectsList);
    setProjects(newProjects);
  };

  /**
   * Used as a callback to ProjectPopup, this gets called when a new project is added.
   * Can't cheat this by manually adding since project would then get shown twice on fetching new projects.
   * Keeping this even though we have polling since it is weird having to wait on this otherwise.
   */
  const refreshProjects = () => {
    setProjectForm(JSON.parse(JSON.stringify({ ...defaultprojectForm })));
    return search();
  };

  /**
   * Call to refresh projects list from page 0 with current filters applied
   */
  const search = () => {
    state.page = 0;
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    searchProject(
      projectSearch,
      setProjects,
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
    page: 0,
    pageSize: 50,
    loading: true,
  });

  /**
   * This is the polling hook that will reload the projects list every 3000 ms
   * This does not change state or loading but will show error messages
   */
  usePoll(
    () => {
      if (!state.loading && { isOnScreen }.isOnScreen && !showConflicts) {
        controller.abort();
        controller = new AbortController();
        const signal = controller.signal;
        searchProject(
          projectSearch,
          setProjects,
          {
            hasMoreItems: state.hasMoreItems,
            loading: state.loading,
            page: 0,
            pageSize: Math.max(state.page, 1) * state.pageSize,
          },
          () => null,
          () => null,
          signal,
          setError,
          router
        );
        return () => {
          controller.abort();
        };
      } else if (!state.loading && { isOnScreen }.isOnScreen && showConflicts) {
        controller.abort();
        controller = new AbortController();
        const signal = controller.signal;
        searchConflicts(
          setProjects,
          setConflictStudents,
          conflictMap,
          setConflictMap,
          setLoading,
          signal,
          setError,
          router
        );
        return () => {
          controller.abort();
        };
      }
    },
    [state, projectSearch, { isOnScreen }.isOnScreen, showConflicts],
    {
      interval: 3000,
    }
  );

  /**
   * What to show when the projects list is empty
   */
  const showBlank = () => {
    if (loading) {
      return (
        <div className="w-full text-center">
          <p>Loading Projects</p>
          <SpinnerCircular
            size={100}
            thickness={100}
            color="#FCB70F"
            secondaryColor="rgba(252, 183, 15, 0.4)"
            className="mx-auto"
          />
        </div>
      );
    }
    return <div>No projects found.</div>;
  };

  /**
   * Called when FlatList is scrolled to the bottom
   */
  const fetchData = () => {
    if (!{ isOnScreen }.isOnScreen) {
      return;
    }
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    searchProject(
      projectSearch,
      updateProjects,
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
    <PersistLogin>
      <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
        <div className="min-w-screen flex min-h-screen flex-col items-center">
          <Header />
          <DndProvider backend={HTML5Backend} key={1}>
            <main className="flex w-full flex-row">
              {/* Holds the sidebar with search, filter and student results */}
              <section
                className={`${
                  showSidebar ? 'visible' : 'hidden'
                } relative mt-[14px] w-full bg-osoc-neutral-bg px-4 md:visible md:block md:w-[400px] md:max-w-[450px] lg:min-w-[450px]`}
              >
                {/* button to close sidebar on mobile */}
                <div
                  className={`${
                    showSidebar ? 'visible' : 'hidden'
                  } absolute left-[24px] top-[16px] z-50 flex flex-col justify-center text-[30px] opacity-20 md:hidden`}
                >
                  <i onClick={() => setShowSidebar(!showSidebar)}>{arrow_in}</i>
                </div>
                <StudentSidebar
                  setError={setError}
                  setStudentBase={() => null}
                />
              </section>

              {/* Holds the projects searchbar + project tiles */}
              <section
                className={`${
                  showSidebar ? 'hidden' : 'visible'
                } mt-[30px] w-full md:visible md:block`}
              >
                <div className={`ml-6 mb-3 flex flex-row md:ml-0 md:w-full`}>
                  {/* button to open sidebar on mobile */}
                  <div
                    className={`${
                      showSidebar ? 'hidden' : 'visible w-auto'
                    } flex flex-col justify-center text-[30px] opacity-20 md:hidden`}
                  >
                    <i onClick={() => setShowSidebar(!showSidebar)}>
                      {arrow_out}
                    </i>
                  </div>

                  <div
                    className={`flex w-full flex-row justify-center xl:mr-8 xl1920:mr-10`}
                    ref={elementRef1}
                  >
                    {/* TODO add an easy reset/undo search button */}
                    {/* TODO either move search icon left and add xmark to the right or vice versa */}
                    {/* This is the projects searchbar */}
                    <div
                      className={`${
                        user.role == UserRole.Admin
                          ? 'xl:mr-4 xl:flex-row'
                          : 'md:mr-4 md:flex-row'
                      } ml-6 mr-8 flex w-full flex-col justify-center md:ml-0 lg:ml-6`}
                    >
                      <div className="lg:w-[calc(100% - 200px)] relative mx-4 w-full md:mr-0">
                        <input
                          type="text"
                          className={`${
                            showConflicts
                              ? 'bg-gray cursor-not-allowed'
                              : 'cursor-text bg-white'
                          } form-control m-0 block w-full rounded border border-solid border-gray-300 bg-clip-padding px-3 py-1.5 text-base font-normal text-gray-700 transition ease-in-out focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none`}
                          id="ProjectsSearch"
                          placeholder="Search projects by name"
                          disabled={showConflicts}
                          onChange={(e) => setProjectSearch(e.target.value)}
                          onKeyPress={(e) => {
                            if (showConflicts) {
                              return;
                            }
                            if (e.key == 'Enter') {
                              return search();
                            }
                          }}
                        />
                        <i
                          className="absolute bottom-1.5 right-2 z-10 h-[24px] w-[16px] opacity-20"
                          onClick={() => {
                            if (showConflicts) {
                              return;
                            }
                            return search();
                          }}
                        >
                          {magnifying_glass}
                        </i>
                      </div>
                      <div
                        className={`${
                          user.role == UserRole.Admin
                            ? 'xl:mt-0 xl:h-auto'
                            : 'md:mt-0 md:h-auto'
                        } mt-2 flex h-[36px] flex-row justify-center`}
                      >
                        {/* Button to show conflicts */}
                        <button
                          className={`justify-right ml-2 min-w-[160px] rounded-sm bg-check-orange px-2 py-1 text-sm font-medium text-white shadow-sm shadow-gray-300`}
                          //type="submit"
                          onClick={() => setShowConflicts(!showConflicts)}
                        >
                          {showConflicts
                            ? 'Show All Projects'
                            : 'Show Conflicts'}
                        </button>

                        {/* Button to create new project */}
                        <button
                          className={`${
                            user.role == UserRole.Admin ? 'visible' : 'hidden'
                          } justify-right ml-2 min-w-[160px] rounded-sm bg-check-orange px-2 py-1 text-sm font-medium text-white shadow-sm shadow-gray-300`}
                          //type="submit"
                          onClick={() => setShowCreateProject(true)}
                        >
                          Create new project
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                {error && <Error error={error} className="mb-4" />}

                {/* This contains the project tiles */}
                {!showConflicts && (
                  <div className="ml-0 flex flex-row flex-wrap lg:ml-6">
                    <FlatList
                      list={projects}
                      renderItem={(project: ProjectBase) => (
                        <ProjectTile
                          key={project.id}
                          projectInput={project}
                          conflictStudents={conflictStudents}
                          refreshProjects={refreshProjects}
                        />
                      )}
                      renderWhenEmpty={showBlank} // let user know if initial data is loading or there is no data to show
                      hasMoreItems={state.hasMoreItems}
                      loadMoreItems={fetchData}
                      paginationLoadingIndicator={<div />} // Use an empty div here to avoid showing the default since it has a bug
                      paginationLoadingIndicatorPosition="center"
                    />
                    <div
                      className={`${
                        state.loading && state.page > 0
                          ? 'visible block'
                          : 'hidden'
                      } text-center`}
                    >
                      <p>Loading Projects</p>
                      <SpinnerCircular
                        size={100}
                        thickness={100}
                        color="#FCB70F"
                        secondaryColor="rgba(252, 183, 15, 0.4)"
                        className="mx-auto"
                      />
                    </div>
                  </div>
                )}
                {showConflicts && <ProjectConflict conflictMap={conflictMap} />}
              </section>
            </main>
          </DndProvider>

          {/* This is the popup to create a new project */}
          <Popup
            open={showCreateProject}
            onClose={() => setShowCreateProject(false)}
            data-backdrop="static"
            data-keyboard="false"
            closeOnDocumentClick={false}
            lockScroll={true}
          >
            <div className="modal chart-label max-w-screen absolute left-1/2 top-1/2 flex max-h-[85vh] min-w-[600px] flex-col bg-osoc-neutral-bg py-5">
              <a
                className="close"
                onClick={(e) => {
                  e.stopPropagation();
                  setShowCreateProject(false);
                }}
              >
                &times;
              </a>

              <h3 className="mb-3 px-5 text-xl">Create New Project</h3>
              <div className="mb-4 flex flex-col overflow-y-auto">
                <ProjectPopup
                  projectForm={projectForm}
                  setShowPopup={setShowCreateProject}
                  setProjectForm={setProjectForm}
                  setError={setError}
                  setMyProjectBase={refreshProjects}
                  setDeletePopup={() => null}
                />
              </div>
            </div>
          </Popup>
        </div>
      </RouteProtection>
    </PersistLogin>
  );
};

export default Projects;
