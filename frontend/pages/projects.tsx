import type { NextPage } from 'next';
import Header from '../components/Header';
import StudentSidebar from '../components/StudentSidebar';
import ProjectTiles from '../components/projects/ProjectTiles';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;

// Header
//
// Sidebar
//      Searchbar + filter control button
//      Filter panel
//      students list
//
//
// Main
//      searchbar
//      Project tiles
//          top information thing
//          assigned people list

const Projects: NextPage = () => {
  return (
    <div className="flex min-h-screen flex-col items-center py-2">
      <Header />

      <main className="flex w-full flex-row">
        {/* Holds the sidebar with search, filter and student results */}
        <StudentSidebar />

        {/* Holds the projects searchbar + project tiles */}
        <section className="mt-[30px] w-2/3 flex-initial">
          {/* This is the projects searchbar */}
          <div className="flex justify-center">
            <div className="relative mb-3 xl:w-96">
              <input
                type="search"
                className="form-control m-0 block w-full rounded border border-solid border-gray-300 bg-white bg-clip-padding px-3 py-1.5 text-base font-normal text-gray-700 transition ease-in-out focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none"
                id="ProjectsSearch"
                placeholder="Search projects by name"
              />
              {/* TODO add actual onclick search */}
              <i
                className="absolute bottom-1.5 right-2 opacity-20"
                // onClick={() => }
              >
                {magnifying_glass}
              </i>
            </div>
          </div>

          {/* This contains the project tiles */}
          <ProjectTiles projects={projects} />
        </section>
      </main>
    </div>
  );
};

export default Projects;

// Test data
// Skill will probably not work when actual data is used
const projects = [
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
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
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
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
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
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
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
      },
      {
        id: '104',
        username: 'coach 104',
        email: 'coach104@smnt.com',
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
        },
        reason: 'Another reason for doing something I hate creating fake data',
      },
    ],
  },
];
