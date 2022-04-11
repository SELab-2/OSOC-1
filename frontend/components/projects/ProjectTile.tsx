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
import { useEffect } from 'react';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
const speech_bubble = <Icon icon="simple-line-icons:speech" />;
const xmark_circle = <Icon icon="akar-icons:circle-x" />;

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
  useAxiosAuth();
  useEffect(() => {
    axiosAuthenticated
      .post(
        Endpoints.BASEURL + '/' + projectId + '/assignments', // TODO import this url somehow
        {
          studentId,
          positionId,
          suggesterId,
          reason,
        }
      )
      .then((response) => {
        console.log(response);
      })
      .catch((ex) => {
        console.log(ex);
      });
  }, []);
}

const ProjectTile: React.FC<ProjectProp> = ({ project }: ProjectProp) => {
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
        console.log(item);
        const student = item as Student; // TODO find a way to pass item not as type DragObject but as type Student
        // TODO call a function that creates a pop up thing to choose reason & position
        // TODO after that call postStudentToProject with correct information
      },
      collect: (monitor) => ({
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop(),
      }), // TODO isOver & canDrop styling
    }),
    []
  );

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
          <ProjectAssignmentsList key={assignment.id} assignment={assignment} />
        ))}
      </div>
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
          {assignment.position.skill}
        </p>
        <p className="text-xs opacity-40">
          Suggested by {assignment.suggester.username}
        </p>
      </div>
      <div className="flex flex-col justify-center">
        <i className="icon-xcircle-red text-2xl">{xmark_circle}</i>
      </div>
    </div>
  );
};

export default ProjectTile;
