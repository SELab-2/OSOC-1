import type { NextPage } from 'next';
import Header from '../components/Header';
import {useState} from "react";
import StudentSidebar from '../components/StudentSidebar';

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass } from "@fortawesome/free-solid-svg-icons";
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
        <StudentSidebar/>

        {/* Holds the projects searchbar + project tiles */}
        <section className="w-2/3 flex-initial">
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
              >{magnifying_glass}</i>
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
