import {
  conflictMapType,
  ProjectBase,
  StudentBase,
  Url,
} from '../../lib/types';
import { useEffect, useState } from 'react';
import { getUrlList } from '../../lib/requestUtils';
import ProjectTile from './ProjectTile';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';
import Error from '../Error';

type ProjectConflictProp = {
  conflictMap: conflictMapType;
};

type ProjectConflictStudentProp = {
  student: StudentBase;
  amount: number;
  setCurrentStudent: (currentStudent: StudentBase) => void;
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

  useEffect(() => {
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
  }, [currentStudent]);

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
        <div className={`mt-4 w-[30%]`}>
          {Array.from(conflictMap).map(([key, value]) => (
            <ProjectConflictStudents
              key={key}
              student={value.student}
              amount={value.amount}
              setCurrentStudent={setCurrentStudent}
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
}: ProjectConflictStudentProp) => {
  return (
    <div
      className={`mb-2 cursor-pointer rounded bg-osoc-neutral-bg p-1 shadow-sm shadow-gray-500`}
      onClick={(e) => {
        e.stopPropagation();
        e.preventDefault();
        setCurrentStudent(student);
      }}
    >
      {student.firstName + ' ' + student.lastName + ' (' + amount + ')'}
    </div>
  );
};

export default ProjectConflict;
