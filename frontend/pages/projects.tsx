import type { NextPage } from 'next';
import Header from '../components/Header';
import StudentSidebar from '../components/StudentSidebar';
import ProjectTiles from '../components/projects/ProjectTiles';
import { Icon } from '@iconify/react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useState } from 'react';
import { UserRole } from '../lib/types';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;
const arrow_out = <Icon icon="bi:arrow-right-circle" />;
const arrow_in = <Icon icon="bi:arrow-left-circle" />;

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
  const [showSidebar, setShowSidebar] = useState(false);

  return (
    <div className="min-w-screen flex min-h-screen flex-col items-center">
      <Header />

      <main className="flex w-full flex-row">
        {/* Holds the sidebar with search, filter and student results */}
        <section
          className={`${
            showSidebar ? 'visible' : 'hidden'
          } relative mt-[14px] w-full bg-osoc-neutral-bg p-4 md:visible md:block md:w-[400px] md:max-w-[450px] lg:min-w-[450px]`}
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

            {/* This is the projects searchbar */}
            <div className="ml-6 flex w-full justify-center md:mx-6 md:mr-4">
              <div className="relative mx-4 w-full md:mr-0 lg:w-[80%]">
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
