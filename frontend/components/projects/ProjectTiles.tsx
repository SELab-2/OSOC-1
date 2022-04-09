import ProjectTile, { Project } from './ProjectTile';

type ProjectTableProps = {
    projects: Project[]
}

const ProjectTiles: React.FC<ProjectTableProps> = ({ projects }: ProjectTableProps) => {

    return (
        <table className="w-full table-auto">
            <thead className="top-0 bg-white">
            <tr>
                <th className="col-span-full text-xs font-normal text-right border-b-2 border-gray-400 pb-1">
                    {/* TODO this should be something else once endpoint is dont */}
                    {/*{ students.length + "/" + students.length + " shown" }*/}
                </th>
            </tr>
            </thead>
            <tbody>
            {
                projects.map((project) => <ProjectTile key={project.id} project={project}/>)
            }
            </tbody>
        </table>
    )
}

export default ProjectTiles;