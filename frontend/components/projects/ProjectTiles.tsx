import ProjectTile, { Project } from './ProjectTile';

type ProjectTableProps = {
  projects: Project[];
};

const ProjectTiles: React.FC<ProjectTableProps> = ({
  projects,
}: ProjectTableProps) => {
  return (
    <div className="ml-0 flex flex-row flex-wrap lg:ml-6">
      {projects.map((project) => (
        <ProjectTile key={project.id} project={project} />
      ))}
    </div>
  );
};

export default ProjectTiles;
