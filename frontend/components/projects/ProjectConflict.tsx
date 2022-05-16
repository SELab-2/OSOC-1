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
    <div>
      {error && <Error error={error} className="mb-4" />}
      <div className={`flex w-full flex-row content-between justify-between`}>
        {/* Conflicts projects list */}
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

        {/* Conflicts students list */}
        <div className={`w-[33%]`}>
          {Array.from(conflictMap).map(([key, value]) => (
            <ProjectConflictStudents
              key={key}
              student={value.student}
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
  setCurrentStudent,
}: ProjectConflictStudentProp) => {
  return (
    <div
      onClick={(e) => {
        e.stopPropagation();
        e.preventDefault();
        setCurrentStudent(student);
      }}
    >
      {student.firstName + ' ' + student.lastName}
    </div>
  );
};

export default ProjectConflict;
