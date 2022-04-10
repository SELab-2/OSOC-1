import { Fragment, PropsWithChildren, useState } from 'react';
import StudentTiles from './students/StudentTiles';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { Menu, Transition } from '@headlessui/react';
import { ChevronDownIcon } from '@heroicons/react/solid';
const magnifying_glass = <FontAwesomeIcon icon={faMagnifyingGlass} />;

type StudentsSidebarProps = PropsWithChildren<unknown>;

// TODO no actual functionality implemented yet
const StudentSidebar: React.FC<StudentsSidebarProps> = () => {
  const [showFilter, setShowFilter] = useState(true);
  return (
    // TODO test with a long list for autoscroll etc this should be separate from projects scroll
    // holds searchbar + hide filter button
    <div className="mt-[50px] sm:mt-0">
      <div className="mb-3 flex w-full flex-col items-center justify-between lg:flex-row">
        {/* The students searchbar */}
        <div className="justify-left mb-3 flex w-[80%] md:ml-0 md:w-[calc(100% - 200px)] lg:mb-0 ">
          <div className="relative w-full">
            <input
              type="search"
              className="form-control m-0 block w-full rounded border border-solid border-gray-300 bg-white bg-clip-padding px-3 py-1.5 text-base font-normal text-gray-700 transition ease-in-out focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none"
              id="StudentsSearch"
              placeholder="Search students by name"
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

        {/* Show/hide filter button */}
        <button
          className="justify-right rounded-sm bg-check-orange min-w-[120px] ml-2 text-sm px-2 py-1 font-medium text-white shadow-sm shadow-gray-300"
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
          <div className="flex flex-row justify-items-center">
            {/* This button controls the dropdown */}
            <Menu as="div" className="relative inline-block text-left">
              <div>
                <Menu.Button className="inline-flex w-full justify-center border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50">
                  Select Roles
                  <ChevronDownIcon
                    className="-mr-1 ml-2 h-5 w-5"
                    aria-hidden="true"
                  />
                </Menu.Button>
              </div>

              <Transition
                as={Fragment}
                enter="transition ease-out duration-100"
                enterFrom="transform opacity-0 scale-95"
                enterTo="transform opacity-100 scale-100"
                leave="transition ease-in duration-75"
                leaveFrom="transform opacity-100 scale-100"
                leaveTo="transform opacity-0 scale-95"
              >
                {/* These are the actual dropdown options */}
                <Menu.Items className="absolute right-0 z-10 w-full origin-top-right bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                  <div className="">
                    <Menu.Item>
                      {({ active }) => (
                        <p
                          className={`${
                            active
                              ? 'bg-gray-100 text-gray-900'
                              : 'text-gray-700'
                          } block px-4 py-2 text-sm`}
                        >
                          opt1
                        </p>
                      )}
                    </Menu.Item>
                    <Menu.Item>
                      {({ active }) => (
                        <p
                          className={`${
                            active
                              ? 'bg-gray-100 text-gray-900'
                              : 'text-gray-700'
                          } block px-4 py-2 text-sm`}
                        >
                          opt2
                        </p>
                      )}
                    </Menu.Item>
                    <Menu.Item>
                      {({ active }) => (
                        <p
                          className={`${
                            active
                              ? 'bg-gray-100 text-gray-900'
                              : 'text-gray-700'
                          } block px-4 py-2 text-sm`}
                        >
                          opt3
                        </p>
                      )}
                    </Menu.Item>
                    {/* Don't know what type this dropdown menu will end up being yet */}
                    {/*<form method="POST" action="#">*/}
                    {/*    <Menu.Item>*/}
                    {/*        {({ active }) => (*/}
                    {/*            <button*/}
                    {/*                type="submit"*/}
                    {/*                className={`${*/}
                    {/*                    active ? 'bg-gray-100 text-gray-900' : 'text-gray-700'}*/}
                    {/*                block px-4 py-2 text-sm`}*/}
                    {/*            >*/}
                    {/*                Sign out*/}
                    {/*            </button>*/}
                    {/*        )}*/}
                    {/*    </Menu.Item>*/}
                    {/*</form>*/}
                  </div>
                </Menu.Items>
              </Transition>
            </Menu>
            <button className="ml-2 bg-gray-300 p-2 text-sm text-black">
              Deselect all
            </button>
          </div>
          <button className="bg-gray-300 px-2 ml-4 text-sm text-black">
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
            <button className="w-[44%] bg-gray-300 text-sm text-black">
              Yes
            </button>
            <button className="w-[44%] bg-gray-300 text-sm text-black">
              No
            </button>
          </div>
          <div className="mt-2.5 flex grow flex-row justify-between lg:ml-[2.75%] lg:mt-0 lg:w-1">
            <button className="w-[44%] bg-gray-300 text-sm text-black">
              Maybe
            </button>
            <button className="w-[44%] bg-gray-300 text-sm text-black">
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
const students = [
  {
    id: '1',
    firstName: 'FNaam1',
    lastName: 'LNaam1',
    status: 'Undecided',
    statusSuggestions: [
      {
        coachId: '100',
        status: 'Yes',
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '101',
        status: 'Yes',
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: 'No',
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
        status: 'Maybe',
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: 'Yes',
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: 'No',
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
  {
    id: '3',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: 'Yes',
    statusSuggestions: [
      {
        coachId: '100',
        status: 'Maybe',
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: 'Yes',
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: 'No',
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
  {
    id: '4',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: 'No',
    statusSuggestions: [
      {
        coachId: '100',
        status: 'Maybe',
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: 'Yes',
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: 'No',
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
  {
    id: '5',
    firstName: 'FNaam2',
    lastName: 'LNaam2',
    status: 'Maybe',
    statusSuggestions: [
      {
        coachId: '100',
        status: 'Maybe',
        motivation: 'Dit is een motivatie voor maybe',
      },
      {
        coachId: '101',
        status: 'Yes',
        motivation: 'Dit is een motivatie voor yes',
      },
      {
        coachId: '102',
        status: 'No',
        motivation: 'Dit is een motivatie voor no',
      },
    ],
    alumn: false,
  },
];
