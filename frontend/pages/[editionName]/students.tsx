import type { NextPage } from 'next';
import Header from '../../components/Header';
import StudentSidebar from '../../components/StudentSidebar';
import { Icon } from '@iconify/react';
import { useState } from 'react';
import { StudentBase, UserRole } from '../../lib/types';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { DndProvider } from 'react-dnd';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import StudentHolder from '../../components/student/StudentHolder';
import RouteProtection from '../../components/RouteProtection';
import Error from '../../components/Error';
import PersistLogin from '../../components/PersistLogin';
const arrow_out = <Icon icon="bi:arrow-right-circle" />;
const arrow_in = <Icon icon="bi:arrow-left-circle" />;

/**
 * Select Students page for OSOC application
 * @returns Select Students page
 */
const Students: NextPage = () => {
  // Used to hide / show the students sidebar on screen width below 768px
  const [showSidebar, setShowSidebar] = useState(false);
  // Needed to allow for click select from the sidebar to the main screen
  const [studentBase, setStudentBase] = useState({} as StudentBase);
  const [error, setError]: [string, (error: string) => void] = useState('');
  useAxiosAuth();

  return (
    <PersistLogin>
      <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
        <div className="min-w-screen flex min-h-screen flex-col items-center">
          <Header />
          <DndProvider backend={HTML5Backend} key={2}>
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
                  {/* button to close sidebar on mobile */}
                  <i onClick={() => setShowSidebar(!showSidebar)}>{arrow_in}</i>
                </div>
                {/* actual sidebar */}
                <StudentSidebar
                  setError={setError}
                  setStudentBase={setStudentBase}
                />
              </section>

              {/* Holds main student content */}
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
                </div>

                {error && <Error error={error} className="mb-4" />}

                {/* This contains the actual student info */}
                <div>
                  <StudentHolder
                    studentBase={studentBase}
                    setStudentBase={setStudentBase}
                  />
                </div>
              </section>
            </main>
          </DndProvider>
        </div>
      </RouteProtection>
    </PersistLogin>
  );
};

export default Students;