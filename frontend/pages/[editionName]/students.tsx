import type { NextPage } from 'next';
import Header from '../../components/Header';
import StudentSidebar from '../../components/StudentSidebar';
import { Icon } from '@iconify/react';
import { useState } from 'react';
import { UserRole } from '../../lib/types';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { DndProvider } from 'react-dnd';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import StudentHolder from '../../components/student/StudentHolder';
import RouteProtection from '../../components/RouteProtection';
import Error from '../../components/Error';
const arrow_out = <Icon icon="bi:arrow-right-circle" />;
const arrow_in = <Icon icon="bi:arrow-left-circle" />;

/**
 * Select Students page for OSOC application
 * @returns Select Students page
 */
const Students: NextPage = () => {
  // Used to hide / show the students sidebar on screen width below 768px
  const [showSidebar, setShowSidebar] = useState(false);
  const [refreshStudents, setRefreshStudents] = useState(false);
  const [error, setError]: [string, (error: string) => void] = useState('');
  useAxiosAuth();

  return (
    <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
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
              {/* actual sidebar */}
              <StudentSidebar
                setError={setError}
                refresh={refreshStudents}
                setRefresh={setRefreshStudents}
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
                <StudentHolder />
              </div>
            </section>
          </main>
        </DndProvider>
      </div>
    </RouteProtection>
  );
};

export default Students;

// Temporary fake data to test with
/*
const student = {
  id: '1',
  firstName: 'FNaam1',
  lastName: 'LNaam1',
  status: 'Undecided',
  statusSuggestions: [
    {
      coachId: '100',
      status: StatusSuggestionStatus.Yes,
      motivation: 'Dit is een motivatie voor yes',
    },
    {
      coachId: '101',
      status: StatusSuggestionStatus.Yes,
      motivation: 'Dit is een motivatie voor yes',
    },
    {
      coachId: '102',
      status: StatusSuggestionStatus.No,
      motivation: 'Dit is een motivatie voor no',
    },
  ],
  skills: [],
  alumn: true,
  tallyForm: {
    livingBelgium: true,
    workTime: 1, // 1-4 cba this
    workJuly: true,
    responsibilities: 'ik moet shit doen of niet of wel idk',
    birthName: 'naam',
    lastName: 'andere naam',
    // different name whatever
    gender: 'male',
    // pronouns stuff
    language: 'typescript of niet dan',
    englishLevel: 5, // 1-5
    phoneNumber: '0800999999',
    email: 'paardenlover123@hotmail.be',
    // Skipping cv, portfolio, motivation
    studies: ['Photography', 'Videography'],
    diploma: 'No diploma, I am self taught',
    diplomaYears: '0',
    diplomaYear: '0',
    school: 'self taught',
    role: ['Video editor', 'Photographer'],
    bestSkill: 'Marketer',
    participated: true,
    studentCoach: true,
  },
};
 */
