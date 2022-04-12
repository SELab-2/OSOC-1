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
import { useDrop } from 'react-dnd';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import { Component, Fragment, useEffect, useState } from 'react';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useUser from '../../hooks/useUser';
const speech_bubble = <Icon icon="simple-line-icons:speech" />;
const xmark_circle = <Icon icon="akar-icons:circle-x" />;
import Popup from 'reactjs-popup';
import { Menu, Transition } from '@headlessui/react';
import { ChevronDownIcon } from '@heroicons/react/solid';
import Select, { OptionsOrGroups, Options } from 'react-select';

type ProjectProp = {
  project: Project;
};

type UserProp = {
  user: User;
};

type PositionProp = {
  position: Position;
};

type AssignmentProp = {
  assignment: Assignment;
  projectId: UUID;
};

/**
 * This function sends an authenticated POST request to add a student to a project via an assignment
 *
 * @param projectId   = the UUID of the project to add a student to
 * @param studentId   = the UUID of the student to add to a project
 * @param positionId  = the UUID of the position to assign the student
 *                      this position is already part of the needed project positions
 * @param suggesterId = the UUID of the currently authenticated User
 * @param reason      = the reason for assigning this student to this project
 */
// TODO when post is finished, should update the project frontend view & also the student filter
// TODO should show success / error
function postStudentToProject(
  projectId: UUID,
  studentId: UUID,
  positionId: UUID,
  suggesterId: UUID,
  reason: string
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
    })
    .catch((ex) => {
      console.log(ex);
    });
}

function deleteStudentFromProject(projectId: UUID, assignmentId: UUID) {
  axiosAuthenticated
    .delete(
      Endpoints.PROJECTS + '/' + projectId + '/assignments/' + assignmentId // TODO import this url somehow
    )
    .then((response) => {
      console.log(response);
    })
    .catch((ex) => {
      console.log(ex);
    });
}

const Checkbox = ({ children, ...props }: JSX.IntrinsicElements['input']) => (
  <label style={{ marginRight: '1em' }}>
    <input type="checkbox" {...props} />
    {children}
  </label>
);

const ProjectTile: React.FC<ProjectProp> = ({ project }: ProjectProp) => {
  const [open, setOpen]: [boolean, (open: boolean) => void] =
    useState<boolean>(false);
  const [student, setStudent] = useState({} as Student);
  const closeModal = () => setOpen(false);
  // const [Clearable, setClearable] = useState(false);
  const [PositionId, setPositionId] = useState('' as UUID);
  const [Reason, setReason] = useState('' as string);
  const [User] = useUser(); // Needed for the suggester UUID
  useAxiosAuth();
  /**
   * This hook catches the dropped studentTile
   * The studentTile passes its student as the DragObject to this function on drop
   * Then we allow the user to choose a position & reason, then post student to project
   */
  const [{ isOver, canDrop }, drop] = useDrop(
    () => ({
      accept: ItemTypes.STUDENTTILE,
      // accept: Student,
      canDrop: () => true, // TODO add check to see if student is already part of project
      drop: (item) => {
        // console.log(item);
        setStudent(item as Student); // TODO find a way to pass item not as type DragObject but as type Student
        setOpen(!open);
        // const student = item as Student; // TODO find a way to pass item not as type DragObject but as type Student

        // TODO call a function that creates a pop up thing to choose reason & position
        // postStudentToProject(
        //     project.id,
        //     student.id,
        //     project.positions[0].id,
        //     User.id,
        //     'a fake reason'
        // );
        // TODO after that call postStudentToProject with correct information
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
   * label is what is shown in the dropdown
   */
  const myOptions = [] as Array<any>;
  project.positions.forEach((position) => {
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
          <p className="text-lg font-bold">{project.name}</p>
          <p>{project.clientName}</p>
          <div className="flex flex-row">
            {project.coaches.map((user) => (
              <ProjectCoachesList key={user.id} user={user} />
            ))}
          </div>
        </div>

        {/* right part of header */}
        <div className="flex flex-col pt-1">
          {project.positions.map((position) => (
            <ProjectPositionsList key={position.id} position={position} />
          ))}
        </div>
      </div>

      {/* assigned students list */}
      <div className="flex flex-col">
        {project.assignments.map((assignment) => (
          <ProjectAssignmentsList
            key={assignment.id}
            projectId={project.id}
            assignment={assignment}
          />
        ))}
      </div>

      {/* TODO style this entire thing & show what project / student is used */}
      <Popup
        open={open}
        onClose={closeModal}
        data-backdrop="static"
        data-keyboard="false"
      >
        <div className="modal chart-label absolute left-1/2 top-1/2 flex min-w-[450px] flex-col bg-white p-20">
          <a
            className="close"
            onClick={(e) => {
              e.stopPropagation();
              closeModal();
            }}
          >
            &times;
          </a>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              postStudentToProject(
                project.id,
                student.id,
                PositionId,
                User.id,
                Reason
              );
              setOpen(false);
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
                  onChange={(e) => setPositionId(e.value || '')}
                />
              </Fragment>
            </div>
            <button className={`border-2`} type={`submit`}>
              Assign student
            </button>
          </form>
        </div>
      </Popup>
    </div>
  );
};

const PositionsDropdownItem: React.FC<PositionProp> = ({
  position,
}: PositionProp) => {
  return (
    <Menu.Item>
      {({ active }) => (
        <p
          className={`${
            active ? 'bg-gray-100 text-gray-900' : 'text-gray-700'
          } block px-4 py-2 text-sm`}
        >
          {position.skill.skillName}
        </p>
      )}
    </Menu.Item>
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
  projectId,
  assignment,
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
          onClick={() => deleteStudentFromProject(projectId, assignment.id)}
          className="icon-xcircle-red text-2xl"
        >
          {xmark_circle}
        </i>
      </div>
    </div>
  );
};

export default ProjectTile;
