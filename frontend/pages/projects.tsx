import type { NextPage } from 'next';
import Header from '../components/Header';
import StudentSidebar from '../components/StudentSidebar';
import ProjectTiles from '../components/projects/ProjectTiles';
import { Icon } from '@iconify/react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { Fragment, useEffect, useState } from 'react';
import {Project, ProjectBase, ProjectData, Skill, Student, UserRole, UUID} from '../lib/types';
import axios, { axiosAuthenticated } from '../lib/axios';
import Endpoints from '../lib/endpoints';
import useAxiosAuth from '../hooks/useAxiosAuth';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import Popup from 'reactjs-popup';
import Select from 'react-select';
import { number } from 'prop-types';
import ProjectTile from '../components/projects/ProjectTile';
// import ProjectPopup, {defaultprojectForm, projectForm, setProjectForm} from "../components/projects/ProjectPopup"
// import ProjectPopup, {defaultprojectForm, useProjectForm} from "../components/projects/ProjectPopup"
import ProjectPopup, {
  defaultprojectForm,
} from '../components/projects/ProjectPopup';
import StudentTile from '../components/students/StudentTile';
import FlatList from 'flatlist-react';
import {strictEqual} from "assert";
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;
const arrow_out = <Icon icon="bi:arrow-right-circle" />;
const arrow_in = <Icon icon="bi:arrow-left-circle" />;
const xmark_circle = <Icon icon="akar-icons:circle-x" />;

/**
 * function that allows searching projects by name
 *
 * @param projectSearch - (part of) the name of a project
 * @param setProjects   - callback to set the results
 * @param state - holds page, loading, hasMoreItems, pageSize
 * @param setState - set the state variable
 */
// TODO show/handle errors
function searchProject(
  projectSearch: string,
  setProjects: (projects: Project[]) => void,
state: {hasMoreItems: boolean,
    page: number,
    pageSize: number,
    loading: boolean},
setState: (state: {hasMoreItems: boolean,
  page: number,
  pageSize: number,
  loading: boolean}) => void,
) {
  state.loading = true;
  axiosAuthenticated
    .get<ProjectData>(Endpoints.PROJECTS, {
      params: {
        name: projectSearch,
        pageNumber: state.page,
        pageSize: state.pageSize,
      },
    })
    .then((response) => {
      setProjects(response.data.collection as Project[]);
      const newState = {...state};
      newState.page = state.page + 1;
      newState.hasMoreItems = response.data.totalLength > (state.page * state.pageSize);
      setState(newState);
    })
    .catch((ex) => {
      console.log(ex);
    });
  state.loading = false;
}

/**
 * Projects page for OSOC application
 * @returns Projects page
 */
const Projects: NextPage = () => {
  // Used to hide / show the students sidebar on screen width below 768px
  const [showSidebar, setShowSidebar] = useState(false);
  const [showCreateProject, setShowCreateProject] = useState(false);
  const [projectSearch, setProjectSearch] = useState('' as string);
  const [projects, setProjects]: [Project[], (projects: Project[]) => void] =
    useState([] as Project[]);
  const [loading, setLoading]: [boolean, (loading: boolean) => void] =
    useState<boolean>(true);
  const [error, setError]: [string, (error: string) => void] = useState('');

  const [projectForm, setProjectForm] = useState({ ...defaultprojectForm });

  const updateProjects: (param: Project[]) => void = (projectsList: Project[]) => {
    const newProjects = projects ? [...projects] : ([] as Project[]) as Project[];
    newProjects.push(...projectsList);
    setProjects(newProjects);
  }

  useAxiosAuth();
  useEffect(() => {
    state.page = 0;
    searchProject(projectSearch, setProjects, state, setState);
    // axiosAuthenticated
    //   .get<ProjectData>(Endpoints.PROJECTS)
    //   .then((response) => {
    //     // console.log(response.data);
    //     setProjects(response.data.collection as Project[]);
    //     setLoading(false);
    //   })
    //   .catch((ex) => {
    //     const error =
    //       ex.response.status === 404
    //         ? 'Resource Not found'
    //         : 'An unexpected error has occurred';
    //     setError(error);
    //     setLoading(false);
    //   });
  }, []);

  const [state, setState] = useState({
    hasMoreItems: true,
    page: 0,
    pageSize: 50,
    loading: false,
  });

  const showBlank = () => {
    if (projects.length === 0 && state.loading) {
      return <div>Loading projects...</div>;
    }
    return <div>No projects found.</div>;
  };

  // TODO make actual request with pagination parameters via searchStudent but without overwriting list
  const fetchData = () => {
    searchProject(projectSearch, updateProjects, state, setState);
  };

  return (
    <div className="min-w-screen flex min-h-screen flex-col items-center">
      <Header />
      <DndProvider backend={HTML5Backend}>
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
            <StudentSidebar />
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
                          searchProject(projectSearch, setProjects, state, setState);
                        }
                      }}
                    />
                    <i
                      className="absolute bottom-1.5 right-2 z-10 h-[24px] w-[16px] opacity-20"
                      onClick={() => {
                        state.page = 0;
                        searchProject(projectSearch, setProjects, state, setState);
                      }}
                    >
                      {magnifying_glass}
                    </i>
                  </div>
                </div>

                <button
                  className="justify-right ml-2 min-w-[120px] rounded-sm bg-check-orange px-2 py-1 text-sm font-medium text-white shadow-sm shadow-gray-300"
                  type="submit"
                  onClick={() => setShowCreateProject(true)}
                >
                  Create new project
                </button>
              </div>
            </div>

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
                paginationLoadingIndicator={<div>Loading Projects</div>} // TODO style this
                paginationLoadingIndicatorPosition="center"
              />
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
            />
          </div>
        </div>
      </Popup>
    </div>
  );
};

export default Projects;

const projects_test2 = [
  {
    name: 'Universal background frame',
    clientName: 'Goodman and Sons',
    description: 'extend real-time synergies',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Product/process development scientist' },
        amount: 5,
        id: '67cf7e2c-56fc-4935-8e6c-d5fac4cd4921',
      },
      {
        skill: { skillName: 'Television camera operator' },
        amount: 4,
        id: '5af5d61a-d34d-4f73-a6d3-2c0e01bdda79',
      },
      {
        skill: { skillName: 'Health and safety adviser' },
        amount: 1,
        id: '31395096-0f01-4ad2-b5f0-02637677b0af',
      },
      {
        skill: { skillName: 'Building services engineer' },
        amount: 4,
        id: '212253ea-4b49-4a26-80d7-d98dd20d8d6f',
      },
      {
        skill: { skillName: 'Landscape architect' },
        amount: 3,
        id: 'db469655-9c61-47b2-8c63-31e231310039',
      },
    ],
    assignments: [
      {
        student: {
          firstName: 'Jacob',
          lastName: 'Flores',
          skills: [],
          alumn: false,
          id: '00528aeb-9fb8-42fd-aaa9-3f46b1a79d4c',
          status: 'Yes',
          statusSuggestions: [],
          communications: [],
        },
        position: {
          skill: { skillName: 'Product/process development scientist' },
          amount: 5,
          id: '67cf7e2c-56fc-4935-8e6c-d5fac4cd4921',
        },
        suggester: {
          username: 'adminUserName',
          email: 'admin@admin.com',
          role: UserRole.Admin,
          password:
            '$2a$14$z15XjHuxRybpqqBqQsIgTuloSfbeLGo.NdyburrGv3tcvfPKO.bt2',
          id: '4c654763-eab4-43f7-950d-358050a2d101',
        },
        reason: 'a fake reason',
        id: 'ad49312f-96a3-4d24-80bb-83826912c4f6',
      },
    ],
    id: '169e2a6f-2ac2-4e58-b6cb-7de1fb9f8a5a',
  },
  {
    name: 'Advanced real-time structure',
    clientName: 'Graham Inc',
    description: 'incubate 24/365 schemas',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Fashion designer' },
        amount: 6,
        id: 'c53d24ca-b4f2-4df9-939b-844c07e00146',
      },
      {
        skill: { skillName: 'Doctor, general practice' },
        amount: 4,
        id: '26b70c74-8e29-4005-9cb1-6bedfa6c686e',
      },
      {
        skill: { skillName: 'Surveyor, land/geomatics' },
        amount: 5,
        id: '048d77b5-809d-4747-a042-43ba2c727033',
      },
      {
        skill: { skillName: 'Press photographer' },
        amount: 2,
        id: 'cc355aa9-634a-45d6-abb8-38346c48c905',
      },
      {
        skill: { skillName: 'Librarian, public' },
        amount: 6,
        id: '853dbc50-3afe-45ac-86b8-fed3f1f1fa80',
      },
    ],
    assignments: [],
    id: 'f2236adf-b3f3-45ea-b7b7-452158475ac4',
  },
  {
    name: 'Secured systematic adapter',
    clientName: 'Gordon, Murphy and Hernandez',
    description: 'transition rich e-tailers',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Call centre manager' },
        amount: 5,
        id: '3db01f4e-8a59-4a99-a2d1-c14c6afe3990',
      },
      {
        skill: { skillName: 'Insurance underwriter' },
        amount: 4,
        id: 'cd2f02e8-cbc6-466e-8e48-ac01c8a5c238',
      },
      {
        skill: { skillName: 'Communications engineer' },
        amount: 1,
        id: '00f8d5fa-f354-4f32-ada4-7418fd6b8521',
      },
      {
        skill: { skillName: 'Telecommunications researcher' },
        amount: 7,
        id: '0ec9264a-83fb-49ed-9f3c-1f21b12083f8',
      },
      {
        skill: { skillName: 'Film/video editor' },
        amount: 1,
        id: '1a76ce80-6f33-4693-a490-52fc5022307e',
      },
    ],
    assignments: [],
    id: '21bfb785-1a3d-42e3-a6fb-8c631ad7ed96',
  },
  {
    name: 'Team-oriented empowering protocol',
    clientName: 'Jimenez PLC',
    description: 'matrix compelling infrastructures',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Investment banker, corporate' },
        amount: 6,
        id: 'ae6b21bf-7641-4057-b10d-a772a164e4b7',
      },
      {
        skill: { skillName: 'Surveyor, mining' },
        amount: 2,
        id: '4fd154cf-beb3-4f0f-a2f4-ae1a769f551d',
      },
      {
        skill: { skillName: 'Fashion designer' },
        amount: 3,
        id: '38648b2f-6614-4017-bc4b-50c7c6a3c665',
      },
      {
        skill: { skillName: 'Merchant navy officer' },
        amount: 6,
        id: 'cce929ee-c599-4221-97ed-b3a43e194413',
      },
      {
        skill: { skillName: 'Production assistant, radio' },
        amount: 3,
        id: '588cf26f-c6b1-4175-a00e-cd3fffcbb364',
      },
    ],
    assignments: [],
    id: 'ea0f4c3d-15cc-40b0-a24f-88980b909152',
  },
  {
    name: 'Robust encompassing parallelism',
    clientName: 'Griffin, Hood and Dennis',
    description: 'disintermediate dynamic relationships',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Legal executive' },
        amount: 3,
        id: '3c58a40d-ac45-4aac-ada6-6ea1a9a6b58e',
      },
      {
        skill: { skillName: 'Engineer, mining' },
        amount: 7,
        id: 'fb8803e9-92c5-409f-b849-0b60a2fd9aa3',
      },
      {
        skill: { skillName: 'Aid worker' },
        amount: 1,
        id: '9b237aa2-d01b-4640-b8e4-1afcb49be319',
      },
      {
        skill: { skillName: 'Information officer' },
        amount: 3,
        id: '317f9463-27ef-450a-a381-95e3105ae4b6',
      },
      {
        skill: { skillName: 'Designer, furniture' },
        amount: 5,
        id: 'b509d146-0920-46a5-96a5-37efcff2923b',
      },
    ],
    assignments: [],
    id: '441de376-4489-45bc-bd51-bd889015ef4c',
  },
  {
    name: 'Decentralized real-time product',
    clientName: 'Perez, Santos and Stewart',
    description: 'revolutionize impactful architectures',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Clinical research associate' },
        amount: 4,
        id: '99d56d2b-9f28-4e77-b12f-d529b391039b',
      },
      {
        skill: { skillName: 'Financial risk analyst' },
        amount: 7,
        id: '97b34d01-477d-4e15-9ef3-d290c9373f5d',
      },
      {
        skill: { skillName: 'Ship broker' },
        amount: 7,
        id: '39bc40c0-f9b8-46c1-aaac-ee933ebc8048',
      },
      {
        skill: { skillName: 'Surveyor, quantity' },
        amount: 3,
        id: 'f5321603-7a69-48fc-9105-ff2a26823984',
      },
      {
        skill: { skillName: 'Investment banker, operational' },
        amount: 4,
        id: '0b6244e2-ad85-4b35-ac90-c586b88d8ad6',
      },
    ],
    assignments: [],
    id: '5c3e8573-4dcf-493e-b892-595b468c3141',
  },
  {
    name: 'Polarized high-level encryption',
    clientName: 'Ramirez, Welch and Kennedy',
    description: 'monetize intuitive applications',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Biochemist, clinical' },
        amount: 5,
        id: '4b241967-fac3-44d8-9a37-797fd447c561',
      },
      {
        skill: { skillName: 'Psychologist, prison and probation services' },
        amount: 3,
        id: 'd0782627-d133-4644-95cb-e30e8aa444ae',
      },
      {
        skill: { skillName: 'Lobbyist' },
        amount: 6,
        id: '22db2856-48cf-411f-83e2-5607f301d42c',
      },
      {
        skill: { skillName: 'Environmental education officer' },
        amount: 6,
        id: 'a2b2aad2-13bf-4749-84da-d5427fa97cfa',
      },
      {
        skill: { skillName: 'Further education lecturer' },
        amount: 2,
        id: '5aad8598-a034-455e-8fe7-f39f4e37a2c0',
      },
    ],
    assignments: [],
    id: 'a0552f61-b126-493b-9c42-6a767d3b4add',
  },
  {
    name: 'Diverse transitional archive',
    clientName: 'Anderson-Campbell',
    description: 'brand robust vortals',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Lecturer, further education' },
        amount: 6,
        id: 'ed8ca8d7-32d0-44cf-806b-fd33a94f19b8',
      },
      {
        skill: { skillName: 'Adult nurse' },
        amount: 4,
        id: 'f8f7d950-a639-4d7e-93d8-b2a0a74ce08f',
      },
      {
        skill: { skillName: 'Transport planner' },
        amount: 1,
        id: '063af2bf-b757-4e33-b320-dfb9341c6e4b',
      },
      {
        skill: { skillName: 'Statistician' },
        amount: 5,
        id: '6baa6b7b-9bdb-454b-981d-aed633f2c420',
      },
      {
        skill: { skillName: 'Engineering geologist' },
        amount: 6,
        id: '686bca06-069c-4717-8c40-a638811da311',
      },
    ],
    assignments: [],
    id: 'aeefee39-38ef-4e79-b6c9-a778e8addbf6',
  },
  {
    name: 'Triple-buffered content-based algorithm',
    clientName: 'Benitez, Watkins and Clark',
    description: 'orchestrate granular users',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Personal assistant' },
        amount: 7,
        id: '0c3ef2d3-03f9-4f16-9e31-3227fd13f10b',
      },
      {
        skill: { skillName: 'Magazine features editor' },
        amount: 3,
        id: '6333423e-bcac-482f-b1fa-39ee3f3a503b',
      },
      {
        skill: { skillName: 'Programme researcher, broadcasting/film/video' },
        amount: 1,
        id: '3c634248-9a84-4b2c-a65c-523330926f11',
      },
      {
        skill: { skillName: 'Accountant, chartered' },
        amount: 3,
        id: '7fb873e9-122c-4ac0-9ce4-78a053039544',
      },
      {
        skill: { skillName: 'Television production assistant' },
        amount: 4,
        id: '43c800ab-fb08-4f5a-a443-735d7a55564a',
      },
    ],
    assignments: [],
    id: '8deb60c6-fabe-43af-8b57-8039a770c113',
  },
  {
    name: 'Cross-group solution-oriented Internet solution',
    clientName: 'Hunt, Brown and Hall',
    description: 'transition efficient niches',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Paediatric nurse' },
        amount: 1,
        id: '7fc8688d-0150-445d-b20a-f48a146b1895',
      },
      {
        skill: { skillName: 'Dispensing optician' },
        amount: 3,
        id: '41b289dc-b6e5-478e-8f24-47cbd7716f60',
      },
      {
        skill: { skillName: 'Mechanical engineer' },
        amount: 5,
        id: '1fe71ab2-792b-470f-bc04-a818d44e8c49',
      },
      {
        skill: { skillName: 'Charity fundraiser' },
        amount: 3,
        id: '93355538-cc90-4d03-a333-58f5b39b8ea8',
      },
      {
        skill: { skillName: 'Higher education lecturer' },
        amount: 2,
        id: '517b6282-f879-478a-bd01-e8674b779617',
      },
    ],
    assignments: [],
    id: '21b7a223-fc69-4dae-a035-68b97dfb83c4',
  },
];

const projects_test3 = [
  {
    name: 'Universal background frame',
    clientName: 'Goodman and Sons',
    description: 'extend real-time synergies',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Product/process development scientist' },
        amount: 5,
        id: '67cf7e2c-56fc-4935-8e6c-d5fac4cd4921',
      },
      {
        skill: { skillName: 'Television camera operator' },
        amount: 4,
        id: '5af5d61a-d34d-4f73-a6d3-2c0e01bdda79',
      },
      {
        skill: { skillName: 'Health and safety adviser' },
        amount: 1,
        id: '31395096-0f01-4ad2-b5f0-02637677b0af',
      },
      {
        skill: { skillName: 'Building services engineer' },
        amount: 4,
        id: '212253ea-4b49-4a26-80d7-d98dd20d8d6f',
      },
      {
        skill: { skillName: 'Landscape architect' },
        amount: 3,
        id: 'db469655-9c61-47b2-8c63-31e231310039',
      },
    ],
    assignments: [],
    id: '169e2a6f-2ac2-4e58-b6cb-7de1fb9f8a5a',
  },
  {
    name: 'Advanced real-time structure',
    clientName: 'Graham Inc',
    description: 'incubate 24/365 schemas',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Fashion designer' },
        amount: 6,
        id: 'c53d24ca-b4f2-4df9-939b-844c07e00146',
      },
      {
        skill: { skillName: 'Doctor, general practice' },
        amount: 4,
        id: '26b70c74-8e29-4005-9cb1-6bedfa6c686e',
      },
      {
        skill: { skillName: 'Surveyor, land/geomatics' },
        amount: 5,
        id: '048d77b5-809d-4747-a042-43ba2c727033',
      },
      {
        skill: { skillName: 'Press photographer' },
        amount: 2,
        id: 'cc355aa9-634a-45d6-abb8-38346c48c905',
      },
      {
        skill: { skillName: 'Librarian, public' },
        amount: 6,
        id: '853dbc50-3afe-45ac-86b8-fed3f1f1fa80',
      },
    ],
    assignments: [],
    id: 'f2236adf-b3f3-45ea-b7b7-452158475ac4',
  },
  {
    name: 'Secured systematic adapter',
    clientName: 'Gordon, Murphy and Hernandez',
    description: 'transition rich e-tailers',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Call centre manager' },
        amount: 5,
        id: '3db01f4e-8a59-4a99-a2d1-c14c6afe3990',
      },
      {
        skill: { skillName: 'Insurance underwriter' },
        amount: 4,
        id: 'cd2f02e8-cbc6-466e-8e48-ac01c8a5c238',
      },
      {
        skill: { skillName: 'Communications engineer' },
        amount: 1,
        id: '00f8d5fa-f354-4f32-ada4-7418fd6b8521',
      },
      {
        skill: { skillName: 'Telecommunications researcher' },
        amount: 7,
        id: '0ec9264a-83fb-49ed-9f3c-1f21b12083f8',
      },
      {
        skill: { skillName: 'Film/video editor' },
        amount: 1,
        id: '1a76ce80-6f33-4693-a490-52fc5022307e',
      },
    ],
    assignments: [],
    id: '21bfb785-1a3d-42e3-a6fb-8c631ad7ed96',
  },
  {
    name: 'Team-oriented empowering protocol',
    clientName: 'Jimenez PLC',
    description: 'matrix compelling infrastructures',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Investment banker, corporate' },
        amount: 6,
        id: 'ae6b21bf-7641-4057-b10d-a772a164e4b7',
      },
      {
        skill: { skillName: 'Surveyor, mining' },
        amount: 2,
        id: '4fd154cf-beb3-4f0f-a2f4-ae1a769f551d',
      },
      {
        skill: { skillName: 'Fashion designer' },
        amount: 3,
        id: '38648b2f-6614-4017-bc4b-50c7c6a3c665',
      },
      {
        skill: { skillName: 'Merchant navy officer' },
        amount: 6,
        id: 'cce929ee-c599-4221-97ed-b3a43e194413',
      },
      {
        skill: { skillName: 'Production assistant, radio' },
        amount: 3,
        id: '588cf26f-c6b1-4175-a00e-cd3fffcbb364',
      },
    ],
    assignments: [],
    id: 'ea0f4c3d-15cc-40b0-a24f-88980b909152',
  },
  {
    name: 'Robust encompassing parallelism',
    clientName: 'Griffin, Hood and Dennis',
    description: 'disintermediate dynamic relationships',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Legal executive' },
        amount: 3,
        id: '3c58a40d-ac45-4aac-ada6-6ea1a9a6b58e',
      },
      {
        skill: { skillName: 'Engineer, mining' },
        amount: 7,
        id: 'fb8803e9-92c5-409f-b849-0b60a2fd9aa3',
      },
      {
        skill: { skillName: 'Aid worker' },
        amount: 1,
        id: '9b237aa2-d01b-4640-b8e4-1afcb49be319',
      },
      {
        skill: { skillName: 'Information officer' },
        amount: 3,
        id: '317f9463-27ef-450a-a381-95e3105ae4b6',
      },
      {
        skill: { skillName: 'Designer, furniture' },
        amount: 5,
        id: 'b509d146-0920-46a5-96a5-37efcff2923b',
      },
    ],
    assignments: [],
    id: '441de376-4489-45bc-bd51-bd889015ef4c',
  },
  {
    name: 'Decentralized real-time product',
    clientName: 'Perez, Santos and Stewart',
    description: 'revolutionize impactful architectures',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Clinical research associate' },
        amount: 4,
        id: '99d56d2b-9f28-4e77-b12f-d529b391039b',
      },
      {
        skill: { skillName: 'Financial risk analyst' },
        amount: 7,
        id: '97b34d01-477d-4e15-9ef3-d290c9373f5d',
      },
      {
        skill: { skillName: 'Ship broker' },
        amount: 7,
        id: '39bc40c0-f9b8-46c1-aaac-ee933ebc8048',
      },
      {
        skill: { skillName: 'Surveyor, quantity' },
        amount: 3,
        id: 'f5321603-7a69-48fc-9105-ff2a26823984',
      },
      {
        skill: { skillName: 'Investment banker, operational' },
        amount: 4,
        id: '0b6244e2-ad85-4b35-ac90-c586b88d8ad6',
      },
    ],
    assignments: [],
    id: '5c3e8573-4dcf-493e-b892-595b468c3141',
  },
  {
    name: 'Polarized high-level encryption',
    clientName: 'Ramirez, Welch and Kennedy',
    description: 'monetize intuitive applications',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Biochemist, clinical' },
        amount: 5,
        id: '4b241967-fac3-44d8-9a37-797fd447c561',
      },
      {
        skill: { skillName: 'Psychologist, prison and probation services' },
        amount: 3,
        id: 'd0782627-d133-4644-95cb-e30e8aa444ae',
      },
      {
        skill: { skillName: 'Lobbyist' },
        amount: 6,
        id: '22db2856-48cf-411f-83e2-5607f301d42c',
      },
      {
        skill: { skillName: 'Environmental education officer' },
        amount: 6,
        id: 'a2b2aad2-13bf-4749-84da-d5427fa97cfa',
      },
      {
        skill: { skillName: 'Further education lecturer' },
        amount: 2,
        id: '5aad8598-a034-455e-8fe7-f39f4e37a2c0',
      },
    ],
    assignments: [],
    id: 'a0552f61-b126-493b-9c42-6a767d3b4add',
  },
  {
    name: 'Diverse transitional archive',
    clientName: 'Anderson-Campbell',
    description: 'brand robust vortals',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Lecturer, further education' },
        amount: 6,
        id: 'ed8ca8d7-32d0-44cf-806b-fd33a94f19b8',
      },
      {
        skill: { skillName: 'Adult nurse' },
        amount: 4,
        id: 'f8f7d950-a639-4d7e-93d8-b2a0a74ce08f',
      },
      {
        skill: { skillName: 'Transport planner' },
        amount: 1,
        id: '063af2bf-b757-4e33-b320-dfb9341c6e4b',
      },
      {
        skill: { skillName: 'Statistician' },
        amount: 5,
        id: '6baa6b7b-9bdb-454b-981d-aed633f2c420',
      },
      {
        skill: { skillName: 'Engineering geologist' },
        amount: 6,
        id: '686bca06-069c-4717-8c40-a638811da311',
      },
    ],
    assignments: [],
    id: 'aeefee39-38ef-4e79-b6c9-a778e8addbf6',
  },
  {
    name: 'Triple-buffered content-based algorithm',
    clientName: 'Benitez, Watkins and Clark',
    description: 'orchestrate granular users',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Personal assistant' },
        amount: 7,
        id: '0c3ef2d3-03f9-4f16-9e31-3227fd13f10b',
      },
      {
        skill: { skillName: 'Magazine features editor' },
        amount: 3,
        id: '6333423e-bcac-482f-b1fa-39ee3f3a503b',
      },
      {
        skill: { skillName: 'Programme researcher, broadcasting/film/video' },
        amount: 1,
        id: '3c634248-9a84-4b2c-a65c-523330926f11',
      },
      {
        skill: { skillName: 'Accountant, chartered' },
        amount: 3,
        id: '7fb873e9-122c-4ac0-9ce4-78a053039544',
      },
      {
        skill: { skillName: 'Television production assistant' },
        amount: 4,
        id: '43c800ab-fb08-4f5a-a443-735d7a55564a',
      },
    ],
    assignments: [],
    id: '8deb60c6-fabe-43af-8b57-8039a770c113',
  },
  {
    name: 'Cross-group solution-oriented Internet solution',
    clientName: 'Hunt, Brown and Hall',
    description: 'transition efficient niches',
    coaches: [],
    positions: [
      {
        skill: { skillName: 'Paediatric nurse' },
        amount: 1,
        id: '7fc8688d-0150-445d-b20a-f48a146b1895',
      },
      {
        skill: { skillName: 'Dispensing optician' },
        amount: 3,
        id: '41b289dc-b6e5-478e-8f24-47cbd7716f60',
      },
      {
        skill: { skillName: 'Mechanical engineer' },
        amount: 5,
        id: '1fe71ab2-792b-470f-bc04-a818d44e8c49',
      },
      {
        skill: { skillName: 'Charity fundraiser' },
        amount: 3,
        id: '93355538-cc90-4d03-a333-58f5b39b8ea8',
      },
      {
        skill: { skillName: 'Higher education lecturer' },
        amount: 2,
        id: '517b6282-f879-478a-bd01-e8674b779617',
      },
    ],
    assignments: [],
    id: '21b7a223-fc69-4dae-a035-68b97dfb83c4',
  },
];

// Test data
// Skill will probably not work when actual data is used
const projects_test = [
  {
    id: '1',
    name: 'some project',
    clientName: 'some client name',
    description: 'some description',
    coaches: [
      {
        id: '101',
        username: 'coach 101',
        email: 'coach101@smnt.com',
        role: UserRole.Coach,
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
        role: UserRole.Coach,
      },
    ],
    positions: [
      {
        id: '1001',
        skill: 'Full Stack dev',
        amount: 2,
      },
      {
        id: '1002',
        skill: 'A VERY VERY VERY VERY VERY LONG NAME',
        amount: 3,
      },
      {
        id: '1003',
        skill: 'UI UX Guy',
        amount: 1,
      },
      {
        id: '1004',
        skill: 'Skill4',
        amount: 1,
      },
      {
        id: '1005',
        skill: 'Skill5',
        amount: 1,
      },
      {
        id: '1006',
        skill: 'Skill6',
        amount: 1,
      },
    ],
    assignments: [
      {
        id: '10001',
        student: {
          id: '1',
          firstName: 'FNaam1',
          lastName: 'LNaam1',
          status: 'Yes',
          statusSuggestions: [],
          alumn: true,
        },
        position: {
          id: '1002',
          skill: 'Skill2',
          amount: 3,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'A reason for doing something I hate creating fake data',
      },
      {
        id: '10002',
        student: {
          id: '2',
          firstName: 'FNaam2',
          lastName: 'LNaam2',
          status: 'Maybe',
          statusSuggestions: [],
          alumn: false,
        },
        position: {
          id: '1003',
          skill: 'UI UX Guy',
          amount: 1,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'Another reason for doing something I hate creating fake data',
      },
    ],
  },

  {
    id: '2',
    name: 'some project',
    clientName: 'some client name',
    description: 'some description',
    coaches: [
      {
        id: '101',
        username: 'coach 101',
        email: 'coach101@smnt.com',
        role: UserRole.Coach,
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
        role: UserRole.Coach,
      },
    ],
    positions: [
      {
        id: '1001',
        skill: 'Full Stack dev',
        amount: 2,
      },
      {
        id: '1002',
        skill: 'Skill2',
        amount: 3,
      },
      {
        id: '1003',
        skill: 'UI UX Guy',
        amount: 1,
      },
      {
        id: '1004',
        skill: 'Skill4',
        amount: 1,
      },
      {
        id: '1005',
        skill: 'Skill5',
        amount: 1,
      },
      {
        id: '1006',
        skill: 'Skill6',
        amount: 1,
      },
    ],
    assignments: [
      {
        id: '10001',
        student: {
          id: '1',
          firstName: 'FNaam1',
          lastName: 'LNaam1',
          status: 'Yes',
          statusSuggestions: [],
          alumn: true,
        },
        position: {
          id: '1002',
          skill: 'Skill2',
          amount: 3,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'A reason for doing something I hate creating fake data',
      },
      {
        id: '10002',
        student: {
          id: '2',
          firstName: 'FNaam2',
          lastName: 'LNaam2',
          status: 'Maybe',
          statusSuggestions: [],
          alumn: false,
        },
        position: {
          id: '1003',
          skill: 'UI UX Guy',
          amount: 1,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'Another reason for doing something I hate creating fake data',
      },
    ],
  },

  {
    id: '3',
    name: 'some project',
    clientName: 'some client name',
    description: 'some description',
    coaches: [
      {
        id: '101',
        username: 'coach 101',
        email: 'coach101@smnt.com',
        role: UserRole.Coach,
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
        role: UserRole.Coach,
      },
    ],
    positions: [
      {
        id: '1001',
        skill: 'Full Stack dev',
        amount: 2,
      },
      {
        id: '1002',
        skill: 'Skill2',
        amount: 3,
      },
      {
        id: '1003',
        skill: 'UI UX Guy',
        amount: 1,
      },
      {
        id: '1004',
        skill: 'Skill4',
        amount: 1,
      },
      {
        id: '1005',
        skill: 'Skill5',
        amount: 1,
      },
      {
        id: '1006',
        skill: 'Skill6',
        amount: 1,
      },
    ],
    assignments: [
      {
        id: '10001',
        student: {
          id: '1',
          firstName: 'FNaam1',
          lastName: 'LNaam1',
          status: 'Yes',
          statusSuggestions: [],
          alumn: true,
        },
        position: {
          id: '1002',
          skill: 'Skill2',
          amount: 3,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'A reason for doing something I hate creating fake data',
      },
      {
        id: '10002',
        student: {
          id: '2',
          firstName: 'FNaam2',
          lastName: 'LNaam2',
          status: 'Maybe',
          statusSuggestions: [],
          alumn: false,
        },
        position: {
          id: '1003',
          skill: 'UI UX Guy',
          amount: 1,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'Another reason for doing something I hate creating fake data',
      },
    ],
  },

  {
    id: '4',
    name: 'some project',
    clientName: 'some client name',
    description: 'some description',
    coaches: [
      {
        id: '101',
        username: 'coach 101',
        email: 'coach101@smnt.com',
        role: UserRole.Coach,
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
        role: UserRole.Coach,
      },
    ],
    positions: [
      {
        id: '1001',
        skill: 'Full Stack dev',
        amount: 2,
      },
      {
        id: '1002',
        skill: 'Skill2',
        amount: 3,
      },
      {
        id: '1003',
        skill: 'UI UX Guy',
        amount: 1,
      },
      {
        id: '1004',
        skill: 'Skill4',
        amount: 1,
      },
      {
        id: '1005',
        skill: 'Skill5',
        amount: 1,
      },
      {
        id: '1006',
        skill: 'Skill6',
        amount: 1,
      },
    ],
    assignments: [
      {
        id: '10001',
        student: {
          id: '1',
          firstName: 'FNaam1',
          lastName: 'LNaam1',
          status: 'Yes',
          statusSuggestions: [],
          alumn: true,
        },
        position: {
          id: '1002',
          skill: 'Skill2',
          amount: 3,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'A reason for doing something I hate creating fake data',
      },
      {
        id: '10002',
        student: {
          id: '2',
          firstName: 'FNaam2',
          lastName: 'LNaam2',
          status: 'Maybe',
          statusSuggestions: [],
          alumn: false,
        },
        position: {
          id: '1003',
          skill: 'UI UX Guy',
          amount: 1,
        },
        suggester: {
          id: '102',
          username: 'coach 102',
          email: 'coach102@smnt.com',
          role: UserRole.Coach,
        },
        reason: 'Another reason for doing something I hate creating fake data',
      },
    ],
  },
];
