import {
  conflictMapType,
  ProjectBase,
  StudentBase,
  Url,
  UUID,
} from '../../lib/types';
import { useState } from 'react';
import { getUrlList } from '../../lib/requestUtils';
import ProjectTile from './ProjectTile';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';
import Error from '../Error';
import usePoll from 'react-use-poll';
import { Icon } from '@iconify/react';
const xmark_circle = <Icon icon="akar-icons:circle-x" />;

type ProjectConflictProp = {
  conflictMap: conflictMapType;
  pollConflicts: () => void;
  conflictsKeep: conflictMapType;
  setConflictsKeep: (conflictsKeep: conflictMapType) => void;
};

type ProjectConflictStudentProp = {
  student: StudentBase;
  amount: number;
  setCurrentStudent: (currentStudent: StudentBase) => void;
  currentStudent: StudentBase;
  removeCurrentStudent: (id: UUID) => void;
};

/**
 * Function to get all projects from a list of project urls
 *
 * @param projectUrls - Urls of projects to get
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 400 response
 */
async function getProjects(
  projectUrls: Url[],
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const Projects = [] as ProjectBase[];
  await getUrlList<ProjectBase>(
    projectUrls,
    Projects,
    signal,
    setError,
    router
  );
  return Projects;
}

/**
 * Component to show Conflicts, has a sidebar to the right to allow selecting a student with conflicts
 * After selecting a student, will show the conflicts for that student
 */
const ProjectConflict: React.FC<ProjectConflictProp> = ({
  conflictMap,
  pollConflicts,
  conflictsKeep,
  setConflictsKeep,
}: ProjectConflictProp) => {
  const [currentStudent, setCurrentStudent] = useState({} as StudentBase);
  const [projects, setProjects] = useState([] as ProjectBase[]);
  const [error, setError] = useState('');
  const router = useRouter();
  let controller = new AbortController();
  let busy = false;

  const removeCurrentStudent = (id: UUID) => {
    busy = true;
    const newConflictsKeep = new Map(conflictsKeep) as conflictMapType;
    newConflictsKeep.delete(id);
    setConflictsKeep(newConflictsKeep);
    busy = false;
  };

  /**
   * Poll only the currently showing projects
   */
  usePoll(
    () => {
      if (currentStudent) {
        controller.abort();
        controller = new AbortController();
        const signal = controller.signal;
        (async () => {
          setProjects(
            await getProjects(
              Array.from(
                conflictMap.get(currentStudent.id)?.projectUrls || []
              ) as Url[],
              signal,
              setError,
              router
            )
          );
        })();
        return () => {
          controller.abort();
        };
      }
    },
    [currentStudent, conflictMap],
    {
      interval: 3000,
    }
  );

  /**
   * Poll the actual conflicts
   */
  usePoll(
    () => {
      if (!busy) {
        pollConflicts();
      }
    },
    [busy, conflictsKeep],
    {
      interval: 3000,
    }
  );

  return (
    <div className={`mx-4`}>
      {error && <Error error={error} className="mb-4" />}
      {conflictMap.size == 0 && (
        <h4 className="mt-4 text-center text-lg">No conflicts found!</h4>
      )}
      <div className={`flex w-full flex-row content-between justify-between`}>
        {/* Conflicts projects list */}
        {projects.length > 0 && (
          <div className={`tile-reset w-[65%]`}>
            {projects.map((project) => (
              <ProjectTile
                key={project.id}
                projectInput={project}
                refreshProjects={() => null}
                conflictStudents={Array.from(conflictMap.keys()).filter(
                  (conflict) => (conflictMap.get(conflict)?.amount || 0) > 1
                )}
              />
            ))}
          </div>
        )}
        {projects.length == 0 && (
          <p className={`mt-5 ml-6`}>
            Click a student name to show conflicting projects.
          </p>
        )}

        {/* Conflicts students list */}
        <div
          className={`p-auto m-auto mr-4 mt-4 h-auto w-[30%] bg-osoc-neutral-bg p-4`}
        >
          <h4 className={`mb-4 text-xl`}>Conflict Students</h4>
          {Array.from(conflictMap)
            .sort(([, a], [, b]) =>
              a.student.lastName + a.student.firstName >
              b.student.lastName + b.student.firstName
                ? 1
                : -1
            )
            .map(([key, value]) => (
              <ProjectConflictStudents
                key={key}
                student={value.student}
                amount={value.amount}
                setCurrentStudent={setCurrentStudent}
                currentStudent={currentStudent}
                removeCurrentStudent={removeCurrentStudent}
              />
            ))}
        </div>
      </div>
    </div>
  );
};

/**
 * These are the entries from the student conflict sidebar to the right
 */
const ProjectConflictStudents: React.FC<ProjectConflictStudentProp> = ({
  student,
  amount,
  setCurrentStudent,
  currentStudent,
  removeCurrentStudent,
}: ProjectConflictStudentProp) => {
  return (
    <div
      className={`${
        currentStudent && currentStudent.id == student.id
          ? 'bg-osoc-yellow'
          : 'bg-osoc-neutral-bg'
      } 
      mb-2 flex cursor-pointer flex-row justify-between rounded p-1 shadow-sm shadow-gray-500`}
      onClick={(e) => {
        e.stopPropagation();
        e.preventDefault();
        setCurrentStudent(student);
      }}
    >
      <div>
        {student.firstName + ' ' + student.lastName + ' (' + amount + ')'}
      </div>

      {/* If only 1 or 0 assignments are left, allow removing the student from the list */}
      {amount <= 1 && (
        <div className="flex flex-col justify-center">
          <i
            onClick={(e) => {
              e.stopPropagation();
              e.preventDefault();
              removeCurrentStudent(student.id);
            }}
            className="icon-xcircle-gray text-2xl"
          >
            {xmark_circle}
          </i>
        </div>
      )}
    </div>
  );
};

export default ProjectConflict;
