import {
  Assignment,
  ItemTypes,
  Position,
  Project,
  ProjectBase,
  Student,
  User,
  UUID,
  Url,
  UserRole,
} from '../../lib/types';
import { Icon } from '@iconify/react';
import Popup from 'reactjs-popup';
import Select from 'react-select';
import { useDrop } from 'react-dnd';
import { Fragment, useEffect, useState } from 'react';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useUser from '../../hooks/useUser';
import ProjectPopup, {
  defaultprojectForm,
  projectFormFromProject,
} from './ProjectPopup';
import { getUrlList, getUrlMap, parseError } from '../../lib/requestUtils';
import Error from '../Error';
import { SpinnerCircular } from 'spinners-react';
import { useRouter } from 'next/router';
import { convertProjectBase } from '../../lib/conversionUtils';
import { NextRouter } from 'next/dist/client/router';
const speech_bubble = <Icon icon="simple-line-icons:speech" />;
const xmark_circle = <Icon icon="akar-icons:circle-x" />;
const edit_icon = <Icon icon="akar-icons:edit" />;

// Using projectInput and not just project to avoid confusion
type ProjectProp = {
  projectInput: ProjectBase;
  refreshProjects: () => void;
  conflictStudents: UUID[];
};

type UserProp = {
  user: User;
};

type PositionProp = {
  position: Position;
};

type AssignmentProp = {
  assignment: Assignment;
  setOpenUnassignment: (openUnAssignment: boolean) => void;
  setAssignmentId: (assignmentId: UUID) => void;
  setRemoveStudentName: (removeStudentName: string) => void;
  conflictStudents: UUID[];
};

/**
 * This function sends an authenticated POST request to add a student to a project via an assignment
 *
 * @param projectId   - the UUID of the project to add a student to
 * @param studentId   - the UUID of the student to add to a project
 * @param positionId  - the UUID of the position to assign the student
 *                      this position is already part of the needed project positions
 * @param suggesterId - the UUID of the currently authenticated User
 * @param reason      - the reason for assigning this student to this project
 * @param setMyProjectBase  - callback for reloadProject that is called after this POST completes
 * @param signal - IMPORTANT signal only works on following get request to reload
 * @param setError - Callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 418 response
 */
// TODO when post is finished, should update the student filter
function postStudentToProject(
  projectId: UUID,
  studentId: UUID,
  positionId: UUID,
  suggesterId: UUID,
  reason: string,
  setMyProjectBase: (myProjectBase: ProjectBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .post(
      '/' + edition + Endpoints.PROJECTS + '/' + projectId + '/assignments', // TODO import this url somehow
      {
        student: studentId,
        position: positionId,
        suggester: suggesterId,
        reason: reason,
      }
    )
    .then(() => {
      reloadProject(projectId, setMyProjectBase, signal, setError, router);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

// TODO when delete is finished, should update the student filter
/**
 * This function sends an authenticated DELETE request to remove an assignment from a project
 *
 * @param projectId     - the UUID of the project to remove the assignment from
 * @param assignmentId  - the UUID of the assignment to remove
 * @param setMyProjectBase    - callback for reloadProject that is called after this DELETE completes
 * @param signal - IMPORTANT signal only works on following get request to reload
 * @param setError - Callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 418 response
 */
function deleteStudentFromProject(
  projectId: UUID,
  assignmentId: UUID,
  setMyProjectBase: (myProjectBase: ProjectBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .delete(
      '/' +
        edition +
        Endpoints.PROJECTS +
        '/' +
        projectId +
        '/assignments/' +
        assignmentId // TODO import this url somehow
    )
    .then(() => {
      reloadProject(projectId, setMyProjectBase, signal, setError, router);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function to delete the current project, will refresh projects list when completed
 *
 * @param projectId - the UUID of the project to remove
 * @param refreshProjects - callback to update main projects list
 * @param setError - Callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 418 response
 */
function deleteProject(
  projectId: UUID,
  refreshProjects: () => void,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .delete('/' + edition + Endpoints.PROJECTS + '/' + projectId)
    .then(() => {
      refreshProjects();
    })
    .catch((err) => {
      parseError(err, setError, router);
    });
}

/**
 * This function reloads this project
 * Use whenever a post, patch or delete is done
 *
 * @param projectId - the UUID of the project to reload
 * @param setMyProjectBase - a hook to set the reloaded project information
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 418 response
 */
function reloadProject(
  projectId: UUID,
  setMyProjectBase: (myProjectBase: ProjectBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .get<ProjectBase>('/' + edition + Endpoints.PROJECTS + '/' + projectId)
    .then((response) => {
      setMyProjectBase(response.data as ProjectBase);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function to dereference needed project fields
 * @param projectBase - base object with fields to dereference
 * @param setLoading - callback to set when loading is finished
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 418 response
 */
async function getEntireProject(
  projectBase: ProjectBase,
  setLoading: (loading: boolean) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
): Promise<Project> {
  const newProject: Project = convertProjectBase(projectBase);
  const positionMap = new Map<Url, Position>();
  await Promise.all([
    getUrlList<User>(
      projectBase.coaches,
      newProject.coaches,
      signal,
      setError,
      router
    ),
    getUrlList<Assignment>(
      projectBase.assignments,
      newProject.assignments,
      signal,
      setError,
      router
    ),
    getUrlMap<Position>(
      projectBase.positions,
      positionMap,
      signal,
      setError,
      router
    ),
  ]);

  if (signal.aborted) {
    return newProject;
  }

  newProject.positions = Array.from(positionMap.values());

  const studentMap = new Map<Url, Student>();
  await getUrlMap<Student>(
    newProject.assignments.map(
      (assignment) => assignment.student
    ) as unknown as string[],
    studentMap,
    signal,
    setError,
    router
  );

  const suggesterMap = new Map<Url, User>();
  await getUrlMap<User>(
    newProject.assignments.map(
      (assignment) => assignment.suggester
    ) as unknown as string[],
    suggesterMap,
    signal,
    setError,
    router
  );

  if (signal.aborted) {
    return newProject;
  }

  for (const assignment of newProject.assignments) {
    assignment.position = positionMap.get(
      assignment.position as unknown as string
    ) as Position;
    assignment.student = studentMap.get(
      assignment.student as unknown as string
    ) as Student;
    assignment.suggester = suggesterMap.get(
      assignment.suggester as unknown as string
    ) as User;
  }
  setLoading(false);
  return newProject;
}

const ProjectTile: React.FC<ProjectProp> = ({
  projectInput,
  refreshProjects,
  conflictStudents,
}: ProjectProp) => {
  const router = useRouter();
  const [user] = useUser();
  // Need to set a project with all keys present to avoid the render code throwing undefined errors
  const [myProject, setMyProject]: [Project, (myProject: Project) => void] =
    useState(convertProjectBase(projectInput) as Project); // using different names to avoid confusion
  const [myProjectBase, setMyProjectBase]: [
    ProjectBase,
    (myProjectBase: ProjectBase) => void
  ] = useState(projectInput as ProjectBase);
  const [projectForm, setProjectForm] = useState(
    JSON.parse(JSON.stringify({ ...defaultprojectForm }))
  );
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [openAssignment, setOpenAssignment] = useState(false);
  const [openUnassignment, setOpenUnassignment] = useState(false);
  const [assignmentId, setAssignmentId] = useState('' as UUID);
  const [removeStudentName, setRemoveStudentName] = useState('' as string);
  const [student, setStudent] = useState({} as Student);
  const [positionId, setPositionId] = useState('' as UUID);
  const [reason, setReason] = useState('' as string);
  const [currentUser] = useUser();
  const [showEditProject, setShowEditProject] = useState(false);
  const [deletePopup, setDeletePopup] = useState(false);

  let controller = new AbortController();
  let controller2 = new AbortController();

  /**
   * Since polling is done in parent page projects.tsx, we only watch if
   * we get passed a different object than we were already showing.
   * The only thing we can't check based on changed URLs are the positions
   * so we always get the positions unless we need to reload the entire object.
   */
  useEffect(() => {
    if (JSON.stringify(projectInput) != JSON.stringify(myProjectBase)) {
      setMyProjectBase(projectInput as ProjectBase);
    } else {
      controller2.abort();
      controller2 = new AbortController();
      const signal = controller.signal;
      const newPositions = [] as Position[];

      (async () => {
        await getUrlList<Position>(
          projectInput.positions,
          newPositions,
          signal,
          setError,
          router
        );
        // This check does two things, it checks if the positions have changed
        // But it also checks if the project itself has actually been loaded fully via the second check
        // If we don't do this second check, it could change myProject before everything is loaded
        // And rendering will then fail due to undefined errors
        if (
          JSON.stringify(newPositions) != JSON.stringify(myProject.positions) &&
          JSON.stringify(myProject.positions) !=
            JSON.stringify(myProjectBase.positions)
        ) {
          myProject.positions = newPositions;
        }
      })();
      return () => {
        controller2.abort();
      };
    }
  }, [projectInput]);

  /**
   * called when myProjectBase changes, this includes right after
   * myProjectBase gets set to projectInput at the start.
   */
  useEffect(() => {
    setLoading(true);
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    (async () => {
      await getEntireProject(
        myProjectBase,
        setLoading,
        signal,
        setError,
        router
      ).then((response) => {
        setMyProject(response);
      });
    })();
    return () => {
      controller.abort();
    };
  }, [myProjectBase]);

  /**
   * This catches the dropped studentTile
   * The studentTile passes its student as the DragObject to this function on drop
   * Then we allow the suggester to choose a position & reason, then assign student to project
   * Dropping is only allowed if the student is not yet assigned to this project
   */
  const [{ isOver, canDrop }, drop] = useDrop(
    () => ({
      accept: ItemTypes.STUDENTTILE,
      canDrop: (item) => {
        return !myProject.assignments
          .map((assignment) => assignment.student.id)
          .includes((item as Student).id);
      },
      drop: (item) => {
        setStudent(item as Student);
        setOpenAssignment(true); // This opens the popup window to select position & type reason
      },
      collect: (monitor) => ({
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop(),
      }),
    }),
    [myProject]
  );

  /**
   * react-select refuses to work unless you use this weird structure
   * label is what is shown in the dropdown, value is used to pass to assign function
   */
  const myOptions = [] as Array<{ value: string; label: string }>;
  myProject.positions.forEach((position) => {
    myOptions.push({ value: position.id, label: position.skill.skillName });
  });

  return (
    <div
      ref={drop}
      className={`${
        isOver && !canDrop ? 'bg-check-red' : 'bg-osoc-neutral-bg'
      } ${
        isOver && canDrop ? 'bg-check-green' : 'bg-osoc-neutral-bg'
      } m-4 flex w-full flex-col rounded-xl bg-osoc-neutral-bg p-2 shadow-sm shadow-gray-500 xl:w-[calc(50%-48px)] xl1920:w-[calc(33.5%-48px)]`}
    >
      {error && <Error error={error} className="mb-4" setError={setError} />}
      {/* project info top */}
      <div className="flex flex-row justify-between pb-12">
        {/* left part of header */}
        <div className="flex min-w-[40%] flex-col xl:min-w-[50%]">
          <div className="flex flex-row items-center">
            <p className="inline text-lg font-bold">
              {myProject.name}
              <i
                className={`${
                  user.role == UserRole.Admin ? 'visible' : 'hidden'
                } i-inline inline pl-2 text-xl opacity-20 hover:cursor-pointer`}
                onClick={() => setShowEditProject(true)}
              >
                {edit_icon}
              </i>
            </p>
          </div>
          <p>{myProject.clientName}</p>
          <div className="flex flex-row">
            {myProject.coaches.map((user) => (
              <ProjectCoachesList key={user.id} user={user} />
            ))}
          </div>
        </div>

        {/* right part of header */}
        <div className="flex flex-col pt-1">
          {myProject.positions.map((position) => (
            <ProjectPositionsList key={position.id} position={position} />
          ))}
        </div>
      </div>

      {/* assigned students list */}
      <div className="flex flex-col">
        {myProject.assignments
          .sort((one, two) => (one.id > two.id ? -1 : 1))
          .map((assignment) => (
            <ProjectAssignmentsList
              key={assignment.id}
              assignment={assignment}
              setOpenUnassignment={setOpenUnassignment}
              setAssignmentId={setAssignmentId}
              setRemoveStudentName={setRemoveStudentName}
              conflictStudents={conflictStudents}
            />
          ))}
      </div>

      {loading && (
        <div className="w-full text-center">
          <p>Loading</p>
          <SpinnerCircular
            size={60}
            thickness={80}
            color="#FCB70F"
            secondaryColor="rgba(252, 183, 15, 0.4)"
            className="mx-auto"
          />
        </div>
      )}

      {/* This is the popup to assign a student to a project */}
      <Popup
        open={openAssignment}
        onClose={() => setOpenAssignment(false)}
        data-backdrop="static"
        data-keyboard="false"
        closeOnDocumentClick={false}
        lockScroll={true}
      >
        <div className="modal chart-label max-w-screen absolute left-1/2 top-1/2 flex max-h-[85vh] min-w-[600px] flex-col bg-osoc-neutral-bg p-5">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              setOpenAssignment(false);
            }}
          >
            &times;
          </a>

          <h3 className="mb-3 text-lg">
            Assign{' '}
            <i>
              {student.firstName} {student.lastName}
            </i>{' '}
            to <i>{myProject.name}</i>
          </h3>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              controller.abort();
              controller = new AbortController();
              const signal = controller.signal;
              postStudentToProject(
                myProject.id,
                student.id,
                positionId,
                currentUser.id,
                reason,
                setMyProjectBase,
                signal,
                setError,
                router
              );
              setOpenAssignment(false);
              return () => {
                controller.abort();
              };
            }}
          >
            {/* This is a fix to stop clicking on the clearable closing the entire modal */}
            <div
              onClick={(e) => e.stopPropagation()}
              onMouseDown={(e) => e.stopPropagation()}
              className="mb-6"
            >
              <Fragment>
                <label>
                  Position
                  <Select
                    className="basic-single mt-1"
                    classNamePrefix="select"
                    isDisabled={false}
                    isLoading={false}
                    isClearable={true}
                    isRtl={false}
                    isSearchable={true}
                    name="Position"
                    options={myOptions}
                    placeholder="Select position"
                    onChange={(e) => setPositionId(e ? e.value || '' : '')}
                  />
                </label>
              </Fragment>
            </div>

            <label>
              Reason for assignment
              <textarea
                placeholder="Reason for assignment"
                className="mt-1 w-full resize-y border-2 px-1"
                onChange={(e) => setReason(e.target.value || '')}
              />
            </label>
            <div className="mt-6 flex flex-row justify-between">
              <button
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  setOpenAssignment(false);
                }}
                className={`min-w-[120px] border-2 bg-white`}
              >
                Cancel
              </button>

              <button
                className={`min-w-[120px] border-2 bg-check-green py-1`}
                type={`submit`}
              >
                Confirm
              </button>
            </div>
          </form>
        </div>
      </Popup>

      {/* This is the popup to remove a student assignment */}
      <Popup
        open={openUnassignment}
        onClose={() => setOpenUnassignment(false)}
        data-backdrop="static"
        data-keyboard="false"
        closeOnDocumentClick={false}
        lockScroll={true}
      >
        <div className="modal chart-label max-w-screen absolute left-1/2 top-1/2 flex max-h-[85vh] min-w-[450px] flex-col bg-osoc-neutral-bg py-5">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              setOpenUnassignment(false);
            }}
          >
            &times;
          </a>
          <h3 className="px-5 text-lg">
            Are you sure you wish to remove <i>{removeStudentName}</i> from{' '}
            <i>{myProject.name}</i>?
          </h3>
          <div className="mt-3 flex flex-row justify-between px-5">
            <button
              onClick={() => setOpenUnassignment(false)}
              className={`min-w-[120px] border-2 bg-white`}
            >
              Cancel
            </button>

            <button
              className={`min-w-[120px] border-2 bg-check-green py-1`}
              onClick={() => {
                setOpenUnassignment(false);
                controller.abort();
                controller = new AbortController();
                const signal = controller.signal;
                deleteStudentFromProject(
                  myProject.id,
                  assignmentId,
                  setMyProjectBase,
                  signal,
                  setError,
                  router
                );
                return () => {
                  controller.abort();
                };
              }}
            >
              Confirm
            </button>
          </div>
        </div>
      </Popup>

      {/* This is the popup to edit this project */}
      <Popup
        open={showEditProject}
        onClose={() => setShowEditProject(false)}
        // reset the form before opening so users do not accidentally change things
        onOpen={() =>
          setProjectForm(
            projectFormFromProject(myProject, myProjectBase.assignments)
          )
        }
        data-backdrop="static"
        data-keyboard="false"
        closeOnDocumentClick={false}
        lockScroll={true}
      >
        <div className="modal chart-label max-w-screen absolute left-1/2 top-1/2 flex max-h-[85vh] min-w-[600px] flex-col bg-osoc-neutral-bg py-5">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              setShowEditProject(false);
            }}
          >
            &times;
          </a>
          <h3 className="mb-3 px-5 text-xl">Edit Project</h3>
          <div className="mb-4 flex flex-col overflow-y-auto">
            <ProjectPopup
              projectForm={projectForm}
              setShowPopup={setShowEditProject}
              setProjectForm={setProjectForm}
              setError={setError}
              setMyProjectBase={setMyProjectBase}
              setDeletePopup={setDeletePopup}
            />
          </div>
        </div>
      </Popup>

      {/* This is the popup to confirm deleting a project */}
      <Popup
        open={deletePopup}
        onClose={() => setDeletePopup(false)}
        data-backdrop="static"
        data-keyboard="false"
        closeOnDocumentClick={false}
        lockScroll={true}
      >
        <div className="modal chart-label max-w-screen absolute left-1/2 top-1/2 flex max-h-[85vh] min-w-[450px] flex-col bg-osoc-neutral-bg py-5">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              setDeletePopup(false);
            }}
          >
            &times;
          </a>
          <h3 className="px-5 text-lg">
            Are you sure you wish to remove <i>{myProject.name}</i>?
          </h3>
          <div className="mt-3 flex flex-row justify-between px-5">
            <button
              onClick={(e) => {
                e.stopPropagation();
                e.preventDefault();
                setDeletePopup(false);
              }}
              className={`min-w-[120px] border-2 bg-white`}
            >
              Cancel
            </button>

            <button
              className={`min-w-[120px] border-2 bg-check-red py-1`}
              onClick={(e) => {
                e.stopPropagation();
                e.preventDefault();
                setDeletePopup(false);
                setShowEditProject(false);
                deleteProject(myProject.id, refreshProjects, setError, router);
              }}
            >
              Delete
            </button>
          </div>
        </div>
      </Popup>
    </div>
  );
};

const ProjectCoachesList: React.FC<UserProp> = ({ user }: UserProp) => {
  return <p className="mr-2 bg-osoc-bg px-1 text-xs">{user.username}</p>;
};

const ProjectPositionsList: React.FC<PositionProp> = ({
  position,
}: PositionProp) => {
  return (
    <div className="text-right">
      <p className="my-1 inline bg-gray-300 px-1">
        <span className="font-semibold">{position.amount + 'x '}</span>
        {position.skill.skillName}
      </p>
    </div>
  );
};

const ProjectAssignmentsList: React.FC<AssignmentProp> = ({
  assignment,
  setAssignmentId,
  setOpenUnassignment,
  setRemoveStudentName,
  conflictStudents,
}: AssignmentProp) => {
  return (
    <div className="flex flex-row justify-between pb-4">
      <div>
        <div className="flex flex-row">
          <p
            className={`${
              conflictStudents.includes(assignment.student.id)
                ? 'bg-red-400'
                : 'bg-inherit'
            }`}
          >
            {assignment.student.firstName + ' ' + assignment.student.lastName}
          </p>
          <div className="tooltip pl-2 pt-1">
            <i className="icon-speech-blue text-xs">{speech_bubble}</i>
            {/* TODO Make this tooltip look nicer */}
            {/* TODO this tooltip should have a max width since it can bug the layout atm */}
            <span className="tooltiptext w-fit bg-gray-200 px-2">
              {assignment.reason}
            </span>
          </div>
        </div>
        <p className="my-1 inline bg-gray-300 px-1 text-sm">
          {assignment.position.skill.skillName}
        </p>
        <p className="text-xs opacity-40">
          Suggested by {assignment.suggester.username}
        </p>
      </div>
      <div className="flex flex-col justify-center">
        <i
          onClick={(e) => {
            e.stopPropagation();
            e.preventDefault();
            setAssignmentId(assignment.id);
            setRemoveStudentName(
              assignment.student.firstName + ' ' + assignment.student.lastName
            );
            setOpenUnassignment(true);
          }}
          className="icon-xcircle-red text-2xl hover:cursor-pointer"
        >
          {xmark_circle}
        </i>
      </div>
    </div>
  );
};

export default ProjectTile;
