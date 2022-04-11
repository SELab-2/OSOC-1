import { Assignment, Position, Project, User } from '../../lib/types';
import { Icon } from '@iconify/react';
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

const ProjectTile: React.FC<ProjectProp> = ({ project }: ProjectProp) => {
  return (
    <div className="m-4 flex w-full flex-col rounded-xl bg-osoc-neutral-bg p-2 shadow-sm shadow-gray-500 xl:w-[calc(50%-48px)] xl1920:w-[calc(33.5%-48px)]">
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
