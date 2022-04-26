import type { NextPage } from 'next';
import Header from '../components/Header';
import StudentSidebar from '../components/StudentSidebar';
import { Icon } from '@iconify/react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useEffect, useState } from 'react';
import { ProjectBase, ProjectData, UserRole } from '../lib/types';
import { axiosAuthenticated } from '../lib/axios';
import Endpoints from '../lib/endpoints';
import useAxiosAuth from '../hooks/useAxiosAuth';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import Popup from 'reactjs-popup';
import ProjectTile from '../components/projects/ProjectTile';
import ProjectPopup, {
  defaultprojectForm,
} from '../components/projects/ProjectPopup';
import FlatList from 'flatlist-react';
import useUser from '../hooks/useUser';
import { SpinnerCircular } from 'spinners-react';
import Error from '../components/Error';
import { parseError } from '../lib/requestUtils';
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
 */
// TODO show/handle errors
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
  setError: (error: string) => void
) {
  setLoading(true);
  axiosAuthenticated
    .get<ProjectData>(Endpoints.PROJECTS, {
      params: {
        name: projectSearch,
        pageNumber: state.page,
        pageSize: state.pageSize,
      },
      signal: signal,
    })
    .then((response) => {
      setProjects(response.data.collection as ProjectBase[]);
      const newState = { ...state };
      newState.page = state.page + 1;
      newState.hasMoreItems =
        response.data.totalLength > state.page * state.pageSize;
      newState.loading = false;
      setState(newState);
      setLoading(false);
    })
    .catch((err) => {
      const newState = { ...state };
      newState.loading = false;
      setState(newState);
      parseError(err, setError, signal);
      if (!signal.aborted) {
        setLoading(false);
      }
    });
}

/**
 * Projects page for OSOC application
 * @returns Projects page
 */
const Projects: NextPage = () => {
  const [user] = useUser();
  // Used to hide / show the students sidebar on screen width below 768px
  const [showSidebar, setShowSidebar] = useState(false);
  const [showCreateProject, setShowCreateProject] = useState(false);
  const [projectSearch, setProjectSearch] = useState('' as string);
  const [projects, setProjects]: [
    ProjectBase[],
    (projects: ProjectBase[]) => void
  ] = useState([] as ProjectBase[]);
  const [loading, setLoading]: [boolean, (loading: boolean) => void] =
    useState<boolean>(true);
  const [error, setError]: [string, (error: string) => void] = useState('');

  let controller = new AbortController();

  const [projectForm, setProjectForm] = useState({ ...defaultprojectForm });

  const updateProjects: (param: ProjectBase[]) => void = (
    projectsList: ProjectBase[]
  ) => {
    const newProjects = projects
      ? [...projects]
      : ([] as ProjectBase[] as ProjectBase[]);
    newProjects.push(...projectsList);
    setProjects(newProjects);
  };

  useAxiosAuth();
  useEffect(() => {
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
      setError
    );
    return () => {
      controller.abort();
    };
  }, []);

  const [state, setState] = useState({
    hasMoreItems: true,
    page: 0,
    pageSize: 50,
    loading: true,
  });

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

  const fetchData = () => {
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
      setError
    );
    return () => {
      controller.abort();
    };
  };

  return (
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
              } absolute left-[24px] top-[17px] flex flex-col justify-center text-[29px] opacity-20 md:hidden`}
            >
              <i onClick={() => setShowSidebar(!showSidebar)}>{arrow_in}</i>
            </div>
            <StudentSidebar setError={setError} />
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
                <i onClick={() => setShowSidebar(!showSidebar)}>{arrow_out}</i>
              </div>

              <div className={`flex flex-row`}>
                {/* TODO add an easy reset/undo search button */}
                {/* TODO either move search icon left and add xmark to the right or vice versa */}
                {/* This is the projects searchbar */}
                <div className="ml-6 flex w-full justify-center md:mx-6 md:mr-4">
                  <div className="relative mx-4 w-full md:mr-0 lg:w-[80%]">
                    <input
                      type="text"
                      className="form-control m-0 block w-full rounded border border-solid border-gray-300 bg-white bg-clip-padding px-3 py-1.5 text-base font-normal text-gray-700 transition ease-in-out focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none"
                      id="ProjectsSearch"
                      placeholder="Search projects by name"
                      onChange={(e) => setProjectSearch(e.target.value)}
                      onKeyPress={(e) => {
                        if (e.key == 'Enter') {
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
                            setError
                          );
                          return () => {
                            controller.abort();
                          };
                        }
                      }}
                    />
                    <i
                      className="absolute bottom-1.5 right-2 z-10 h-[24px] w-[16px] opacity-20"
                      onClick={() => {
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
                          setError
                        );
                        return () => {
                          controller.abort();
                        };
                      }}
                    >
                      {magnifying_glass}
                    </i>
                  </div>
                </div>

                {/* Button to create new project */}
                <button
                  className={`${
                    user.role == UserRole.Admin ? 'visible' : 'hidden'
                  } justify - right ml-2 min-w-[120px] rounded-sm bg-check-orange px-2 py-1 text-sm font-medium text-white shadow-sm shadow-gray-300`}
                  type="submit"
                  onClick={() => setShowCreateProject(true)}
                >
                  Create new project
                </button>
              </div>
            </div>

            {error && <Error error={error} className="mb-4" />}

            {/* This contains the project tiles */}
            <div className="ml-0 flex flex-row flex-wrap lg:ml-6">
              <FlatList
                list={projects}
                renderItem={(project: ProjectBase) => (
                  <ProjectTile key={project.id} projectInput={project} />
                )}
                renderWhenEmpty={showBlank} // let user know if initial data is loading or there is no data to show
                hasMoreItems={state.hasMoreItems}
                loadMoreItems={fetchData}
                paginationLoadingIndicator={<div />} // Use an empty div here to avoid showing the default since it has a bug
                paginationLoadingIndicatorPosition="center"
              />
              <div
                className={`${
                  state.loading && state.page > 0 ? 'visible block' : 'hidden'
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
            {/* TODO after this is uploaded, need to get new correct projects list */}
            <ProjectPopup
              projectForm={projectForm}
              setShowPopup={setShowCreateProject}
              setProjectForm={setProjectForm}
              setError={setError}
            />
          </div>
        </div>
      </Popup>
    </div>
  );
};

export default Projects;
