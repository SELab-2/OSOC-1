import {
  Assignment,
  ItemTypes,
  Position,
  Project,
  Student,
  User,
  UUID,
} from '../../lib/types';
import { Icon } from '@iconify/react';
import Popup from 'reactjs-popup';
import Select from 'react-select';
import { useDrop } from 'react-dnd';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import { Fragment, useEffect, useState } from 'react';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useUser from '../../hooks/useUser';
import ProjectPopup, {defaultprojectForm, projectFormFromProject} from "./ProjectPopup";
const speech_bubble = <Icon icon="simple-line-icons:speech" />;
const xmark_circle = <Icon icon="akar-icons:circle-x" />;
const edit_icon = <Icon icon="akar-icons:edit" />;

// Using projectInput and not just project to avoid confusion
type ProjectProp = {
  projectInput: Project;
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
 * @param setMyProject  - callback for reloadProject that is called after this POST completes
 */
// TODO when post is finished, should update the project frontend view & also the student filter
// TODO should show success / error
function postStudentToProject(
  projectId: UUID,
  studentId: UUID,
  positionId: UUID,
  suggesterId: UUID,
  reason: string,
  setMyProject: (myProject: Project) => void
) {
  axiosAuthenticated
    .post(
      Endpoints.PROJECTS + '/' + projectId + '/assignments', // TODO import this url somehow
      {
        student: studentId,
        position: positionId,
        suggester: suggesterId,
        reason: reason,
      }
    )
    .then((response) => {
      console.log(response);
      reloadProject(projectId, setMyProject);
    })
    .catch((ex) => {
      console.log(ex);
    });
}

// TODO when delete is finished, should update the project frontend view & also the student filter
// TODO should show success / error
/**
 * This function sends an authenticated DELETE request to remove an assignment from a project
 *
 * @param projectId     - the UUID of the project to remove the assignment from
 * @param assignmentId  - the UUID of the assignment to remove
 * @param setMyProject    - callback for reloadProject that is called after this DELETE completes
 */
function deleteStudentFromProject(
  projectId: UUID,
  assignmentId: UUID,
  setMyProject: (myProject: Project) => void
) {
  axiosAuthenticated
    .delete(
      Endpoints.PROJECTS + '/' + projectId + '/assignments/' + assignmentId // TODO import this url somehow
    )
    .then((response) => {
      console.log(response);
      reloadProject(projectId, setMyProject);
    })
    .catch((ex) => {
      console.log(ex);
    });
}

function reloadProject(
  projectId: UUID,
  setMyProject: (myProject: Project) => void
) {
  axiosAuthenticated
    .get<Project>(Endpoints.PROJECTS + '/' + projectId)
    .then((response) => {
      setMyProject(response.data as Project);
    })
    .catch((ex) => {
      console.log(ex);
    });
}

const ProjectTile: React.FC<ProjectProp> = ({ projectInput }: ProjectProp) => {
  const [myProject, setMyProject]: [Project, (myProject: Project) => void] =
    useState(projectInput as Project); // using different names to avoid confusion
  const [openAssignment, setOpenAssignment]: [
    boolean,
    (openAssignment: boolean) => void
  ] = useState<boolean>(false);
  const closeAssignmentModal = () => setOpenAssignment(false);
  const [openUnassignment, setOpenUnassignment]: [
    boolean,
    (openUnassignment: boolean) => void
  ] = useState<boolean>(false);
  const closeUnassignmentModal = () => setOpenUnassignment(false);
  const [assignmentId, setAssignmentId] = useState('' as UUID);
  const [student, setStudent] = useState({} as Student);
  const [positionId, setPositionId] = useState('' as UUID);
  const [reason, setReason] = useState('' as string);
  const [currentUser] = useUser();
  const [showEditProject, setShowEditProject] = useState(false);
  useAxiosAuth();

  const [projectForm, setProjectForm] = useState(projectFormFromProject(myProject));

  /**
   * This catches the dropped studentTile
   * The studentTile passes its student as the DragObject to this function on drop
   * Then we allow the suggester to choose a position & reason, then assign student to project
   */
  const [{ isOver, canDrop }, drop] = useDrop(
    () => ({
      accept: ItemTypes.STUDENTTILE,
      canDrop: () => true, // TODO add check to see if student is already part of project & if any positions are left
      drop: (item) => {
        setStudent(item as Student); // TODO find a way to pass item not as type DragObject but as type Student
        setOpenAssignment(true); // This opens the popup window to select position & type reason
      },
      collect: (monitor) => ({
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop(),
      }), // TODO isOver & canDrop styling
    }),
    []
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
      className="m-4 flex w-full flex-col rounded-xl bg-osoc-neutral-bg p-2 shadow-sm shadow-gray-500 xl:w-[calc(50%-48px)] xl1920:w-[calc(33.5%-48px)]"
    >
      {/* project info top */}
      <div className="flex flex-row justify-between pb-12">
        {/* left part of header */}
        <div className="flex min-w-[40%] flex-col xl:min-w-[50%]">
          <div className="flex flex-row items-center">
            <p className="text-lg font-bold">{myProject.name}</p>
            <i className={`pl-2 text-xl opacity-20`} onClick={() => setShowEditProject(true)}>{edit_icon}</i>
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
        {myProject.assignments.map((assignment) => (
          <ProjectAssignmentsList
            key={assignment.id}
            assignment={assignment}
            setOpenUnassignment={setOpenUnassignment}
            setAssignmentId={setAssignmentId}
          />
        ))}
      </div>

      {/* TODO style this entire thing & show what project / student is used */}
      {/* This is the popup to assign a student to a project */}
      <Popup
        open={openAssignment}
        onClose={closeAssignmentModal}
        data-backdrop="static"
        data-keyboard="false"
      >
        <div className="modal chart-label absolute left-1/2 top-1/2 flex min-w-[450px] flex-col bg-white p-20">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              closeAssignmentModal();
            }}
          >
            &times;
          </a>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              postStudentToProject(
                myProject.id,
                student.id,
                positionId,
                currentUser.id,
                reason,
                setMyProject
              );
              closeAssignmentModal();
            }}
          >
            <textarea
              placeholder="Reason for assignment"
              className="mt-3 w-full resize-y border-2 border-check-gray"
              onChange={(e) => setReason(e.target.value || '')}
            />
            {/* This is a fix to stop clicking on the clearable closing the entire modal */}
            <div
              onClick={(e) => e.stopPropagation()}
              onMouseDown={(e) => e.stopPropagation()}
            >
              <Fragment>
                <Select
                  className="basic-single"
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
              </Fragment>
            </div>
            <button className={`border-2`} type={`submit`}>
              Assign student
            </button>
          </form>
        </div>
      </Popup>

      {/* TODO style this entire thing & show what project / student is used */}
      {/* This is the popup to remove a student assignment */}
      <Popup
        open={openUnassignment}
        onClose={closeUnassignmentModal}
        data-backdrop="static"
        data-keyboard="false"
      >
        <div className="modal chart-label absolute left-1/2 top-1/2 flex min-w-[450px] flex-col bg-white p-20">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              closeUnassignmentModal();
            }}
          >
            &times;
          </a>
          <p>Are you sure you wish to remove this student from this project?</p>
          <button
            onClick={() => closeUnassignmentModal()}
            className={`border-2`}
          >
            Cancel
          </button>
          <button
            onClick={() => {
              closeUnassignmentModal();
              deleteStudentFromProject(
                myProject.id,
                assignmentId,
                setMyProject
              );
            }}
            className={`border-2`}
          >
            Confirm
          </button>
        </div>
      </Popup>
      {/* TODO style this popup */}
      {/* This is the popup to create a new project */}
      <Popup
          open={showEditProject}
          onClose={() => setShowEditProject(false)}
          // reset the form before opening so users do not accidentally change things
          onOpen={() => setProjectForm(projectFormFromProject(myProject))}
          data-backdrop="static"
          data-keyboard="false"
      >
        <div className="modal chart-label absolute left-1/2 top-1/2 flex min-w-[450px] flex-col bg-white p-20">
          <a
              className="close"
              onClick={(e) => {
                e.stopPropagation();
                setShowEditProject(false);
              }}
          >
            &times;
          </a>
          <h3>Edit Project</h3>

          <ProjectPopup
              projectForm={projectForm}
              setShowPopup={setShowEditProject}
              setProjectForm={setProjectForm}
          />

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
        {position.amount + 'x ' + position.skill}
      </p>
    </div>
  );
};

const ProjectAssignmentsList: React.FC<AssignmentProp> = ({
  assignment,
  setAssignmentId,
  setOpenUnassignment,
}: AssignmentProp) => {
  return (
    <div className="flex flex-row justify-between pb-4">
      <div>
        <div className="flex flex-row">
          <p className="">
            {assignment.student.firstName + ' ' + assignment.student.lastName}
          </p>
          <div className="tooltip pl-2 pt-1">
            <i className="icon-speech-blue text-xs">{speech_bubble}</i>
            {/* TODO Make this tooltip look nicer */}
            <span className="tooltiptext bg-osoc-neutral-bg">
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
          onClick={() => {
            setAssignmentId(assignment.id);
            setOpenUnassignment(true);
          }}
          className="icon-xcircle-red text-2xl"
        >
          {xmark_circle}
        </i>
      </div>
    </div>
  );
};

export default ProjectTile;
