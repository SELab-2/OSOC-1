import type { NextPage } from 'next';
import Header from '../components/Header';

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
        <section className="m-4 w-1/3 flex-initial">
          {/* holds searchbar + hide filter button */}
          <div className="flex w-full flex-row items-center">
            {/* The students searchbar */}
            <div className="justify-left flex w-3/4">
              <div className="mb-3 xl:w-96">
                <input
                  type="search"
                  className="
                                form-control
                                m-0
                                block
                                w-full
                                rounded
                                border
                                border-solid
                                border-gray-300
                                bg-white bg-clip-padding
                                px-3 py-1.5 text-base
                                font-normal
                                text-gray-700
                                transition
                                ease-in-out
                                focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none
                            "
                  id="StudentsSearch"
                  placeholder="Search students by name"
                />
              </div>
            </div>

            {/* Show/hide filter button */}
            <button
              className="justify-right w-1/4 rounded-sm bg-amber-400 px-4 py-1 font-medium text-white shadow-sm shadow-gray-300 lg:mb-4"
              type="submit"
            >
              Hide Filters
            </button>
          </div>

          {/* Holds the filter controls */}
          <div className="flex w-full flex-col rounded-sm border-2 border-amber-400 p-4">
            {/* holds drowndown, deselect all, clear all filters */}
            <div className="flex w-full flex-row justify-between">
              <div className="flex flex-row justify-items-center">
                {/* TODO fix this dropdown later */}
                {/* This button controls the dropdown */}
                <button
                  type="button"
                  className="flex flex-row justify-items-center rounded-md border border-gray-300 bg-white px-4 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 focus:ring-offset-gray-100"
                  id="menu-button"
                  aria-expanded="true"
                  aria-haspopup="true"
                >
                  Select Roles
                  <svg
                    className="-mr-1 ml-2 h-5 w-5"
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 20 20"
                    fill="currentColor"
                    aria-hidden="true"
                  >
                    <path
                      fillRule="evenodd"
                      d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                      clipRule="evenodd"
                    />
                  </svg>
                </button>

                <button className="ml-2 bg-gray-300 text-black p-2">
                  Deselect all
                </button>
              </div>
              <button className="mr-2 bg-gray-300 text-black p-2">
                Clear all filters
              </button>
            </div>

            {/* holds the toggles */}
            <div className="flex w-full flex-col my-2 space-y-2">
              <div className="flex w-full flex-row flex-wrap">
              <div className="relative inline-block w-10 mr-2 select-none transition duration-200 ease-in">
                <input type="checkbox" name="toggleAlumni" id="toggleAlumni"
                       className="toggle-checkbox absolute w-3 h-3 rounded-full bg-gray-300 appearance-none cursor-pointer m-1"/>
                <label htmlFor="toggleAlumni" className="toggle-label block overflow-hidden h-5 rounded-full bg-white border-2 border-gray-300 cursor-pointer"/>
              </div>
              <label htmlFor="toggleAlumni" className="">Only Alumni</label>
            </div>

              <div className="flex w-full flex-row flex-wrap">
                <div className="relative inline-block w-10 mr-2 select-none transition duration-200 ease-in">
                  <input type="checkbox" name="toggleStudentCoach" id="toggleStudentCoach"
                         className="toggle-checkbox absolute w-3 h-3 rounded-full bg-gray-300 appearance-none cursor-pointer m-1"/>
                  <label htmlFor="toggleStudentCoach" className="toggle-label block overflow-hidden h-5 rounded-full bg-white border-2 border-gray-300 cursor-pointer"/>
                </div>
                <label htmlFor="toggleStudentCoach" className="">Only Student Coach Volunteers</label>
              </div>

              <div className="flex w-full flex-row flex-wrap">
                <div className="relative inline-block w-10 mr-2 select-none transition duration-200 ease-in">
                  <input type="checkbox" name="toggleSuggested" id="toggleSuggested"
                         className="toggle-checkbox absolute w-3 h-3 rounded-full bg-gray-300 appearance-none cursor-pointer m-1"/>
                  <label htmlFor="toggleSuggested" className="toggle-label block overflow-hidden h-5 rounded-full bg-white border-2 border-gray-300 cursor-pointer"/>
                </div>
                <label htmlFor="toggleSuggested" className="">Include students you've made a suggestion for</label>
              </div>
            </div>

            <p className="justify-self-start">Filter on status</p>

            {/* holds the filter buttons */}
            <div className="flex flex-row justify-center space-x-4 border-y-2 py-1.5 px-8">
              <button className="w-1 grow bg-gray-300 text-black">Yes</button>
              <button className="w-1 grow bg-gray-300 text-black">No</button>
              <button className="w-1 grow bg-gray-300 text-black">Maybe</button>
              <button className="w-1 grow bg-gray-300 text-black">
                Undecided
              </button>
            </div>
          </div>

          {/* TODO add something real for x/y shown */}
          <p>shown 10/10</p>

          <div className="my-4 h-1 w-full border-b-2" />

          {/* TODO here should be the student tiles */}
        </section>

        {/* Holds the projects searchbar + project tiles */}
        <section className="w-2/3 flex-initial">
          {/* This is the projects searchbar */}
          <div className="flex justify-center">
            <div className="mb-3 xl:w-96">
              <input
                type="search"
                className="
                                form-control
                                m-0
                                block
                                w-full
                                rounded
                                border
                                border-solid
                                border-gray-300
                                bg-white bg-clip-padding
                                px-3 py-1.5 text-base
                                font-normal
                                text-gray-700
                                transition
                                ease-in-out
                                focus:border-blue-600 focus:bg-white focus:text-gray-700 focus:outline-none
                            "
                id="ProjectsSearch"
                placeholder="Search projects by name"
              />
            </div>
          </div>

          {/* This contains the project tiles */}
          <section>{/* TODO */}</section>
        </section>
      </main>
    </div>
  );
};

export default Projects;
