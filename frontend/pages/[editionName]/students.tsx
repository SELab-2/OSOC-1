import type { NextPage } from 'next';
import Header from '../../components/Header';
import StudentSidebar from '../../components/StudentSidebar';
import { Icon } from '@iconify/react';
import { useEffect, useState } from 'react';
import { StudentBaseList, UserRole } from '../../lib/types';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { DndProvider } from 'react-dnd';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import StudentHolder from '../../components/student/StudentHolder';
import RouteProtection from '../../components/RouteProtection';
import Error from '../../components/Error';
import PersistLogin from '../../components/PersistLogin';
import Head from 'next/head';
import { useRouter } from 'next/router';
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
  const [studentBase, setStudentBase] = useState({} as StudentBaseList);
  const [error, setError] = useState('');
  const router = useRouter();
  const [edition, setEdition] = useState(router.query.editionName as string);
  useAxiosAuth();

  useEffect(() => {
    if (router.isReady) {
      setEdition(router.query.editionName as string);
    }
  }, [router.isReady]);

  return (
    <PersistLogin>
      <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
        <Head>
          <title>{edition}: students</title>
        </Head>
        <div className="min-w-screen flex min-h-screen flex-col items-center">
          <Header setError={setError} />
          <DndProvider backend={HTML5Backend} key={2}>
            <main className="mt-[180px] flex w-full flex-row sm:mt-12">
              {/* Holds the sidebar with search, filter and student results */}
              <section
                className={`${
                  showSidebar ? 'visible' : 'hidden'
                } relative mt-[14px] w-full md:visible md:block md:w-[400px] md:max-w-[450px] lg:min-w-[450px]`}
              >
                {/* button to close sidebar on mobile */}
                <div
                  className={`${
                    showSidebar ? 'visible' : 'hidden'
                  } absolute left-[24px] top-[16px] z-50 flex flex-col text-[30px] opacity-20 md:hidden`}
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
                    } flex flex-col text-[30px] opacity-20 md:hidden`}
                  >
                    <i onClick={() => setShowSidebar(!showSidebar)}>
                      {arrow_out}
                    </i>
                  </div>
                </div>

                {error && (
                  <Error error={error} className="mb-4" setError={setError} />
                )}

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
