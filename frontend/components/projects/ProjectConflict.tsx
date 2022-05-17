import {
  conflictMapType,
  ProjectBase,
  StudentBase,
  Url,
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
};

type ProjectConflictStudentProp = {
  student: StudentBase;
  amount: number;
  setCurrentStudent: (currentStudent: StudentBase) => void;
  currentStudent: StudentBase;
  removeCurrentStudent: () => void;
};

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

const ProjectConflict: React.FC<ProjectConflictProp> = ({
  conflictMap,
}: ProjectConflictProp) => {
  const [currentStudent, setCurrentStudent] = useState({} as StudentBase);
  const [projects, setProjects] = useState([] as ProjectBase[]);
  const [error, setError] = useState('');
  const router = useRouter();
  let controller = new AbortController();

  const removeCurrentStudent = () => {
    conflictMap.delete(currentStudent.id);
  };

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
    [currentStudent],
    {
      interval: 3000,
    }
  );

  return (
    <div className={`mx-4`}>
      {error && <Error error={error} className="mb-4" />}
      <div className={`flex w-full flex-row content-between justify-between`}>
        {/* Conflicts projects list */}
        {projects.length > 0 && (
          <div className={`tile-reset w-[65%]`}>
            {projects.map((project) => (
              <ProjectTile
                key={project.id}
                projectInput={project}
                refreshProjects={() => null}
                conflictStudents={Array.from(conflictMap.keys())}
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
          className={`max-h- p-auto m-auto mt-4 h-auto w-[30%] bg-osoc-neutral-bg p-4`}
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

      {amount <= 1 && (
        <div className="flex flex-col justify-center">
          <i
            onClick={() => {
              removeCurrentStudent();
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