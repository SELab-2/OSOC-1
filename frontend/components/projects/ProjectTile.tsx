export type Project = {
    id:	string
    name: string
    client: string
    // coaches*	[...]
    // desc*	string
    // studentRoles*	[...]
};

type ProjectProp = {
    project: Project;
}

const ProjectTile: React.FC<ProjectProp> = ({ project }: ProjectProp) => {

    return (
        <div></div>
    )
}

export default ProjectTile;