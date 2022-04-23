import {Fragment} from "react";
import Select from "react-select";
import {Icon} from "@iconify/react";
import {Project, User, UUID} from "../../lib/types";
const xmark_circle = <Icon icon="akar-icons:circle-x" />;


type ProjectPopupProp = {
    projectForm: ProjectForm;
    setShowPopup: (showPopup: boolean) => void;
    setProjectForm: (projectForm: ProjectForm) => void;
};

type positionForm = {
    amount: string; // This is a string because input field always return a string
    skill: { value: UUID; label: string }; // The weird react-select options type
}

type ProjectForm = {
    projectName: string;
    clientName: string;
    description: string;
    coachIds: User[];
    positions: positionForm[];
}

/**
 * Always unpack this value when assigning to avoid strange behaviour
 */
const defaultPosition = {
    amount: '',
    skill: {} as { value: string; label: string },
}

/**
 * Always unpack this value when assigning to avoid strange behaviour
 */
export const defaultprojectForm = {
    projectName: '',
    clientName: '',
    description: '',
    coachIds: [],
    positions: [{...defaultPosition}] as positionForm[],
} as ProjectForm;

export function projectFormFromProject(project: Project){
    const newProjectForm = { ...defaultprojectForm };
    newProjectForm.projectName = project.name || '';
    newProjectForm.clientName = project.clientName || '';
    newProjectForm.description = project.description || '';
    newProjectForm.coachIds = project.coaches || [];
    newProjectForm.positions = project.positions.map(value => (
        {
            amount: value.amount.toString(),
            skill: { value: value.id, label: value.skill.skillName},
        } as positionForm))
    return newProjectForm;
}

const ProjectPopup: React.FC<ProjectPopupProp> = ({projectForm, setShowPopup, setProjectForm}) => {

    const setPositionDropdownValue = (key: number, value: { value: string; label: string }) => {
        const newProjectForm = { ...projectForm };
        (newProjectForm['positions'] as positionForm[])[key].skill = value;
        setProjectForm(newProjectForm);
    }

    const setPositionDropdownAmount = (key: number, amount: string) => {
        const newProjectForm = { ...projectForm };
        (newProjectForm['positions'] as positionForm[])[key].amount = amount;
        setProjectForm(newProjectForm);
    }

    const addPositionDropdown = () => {
        const newProjectForm = { ...projectForm };
        (newProjectForm['positions'] as positionForm[]).push({...defaultPosition} as positionForm);
        setProjectForm(newProjectForm);
    }

    const removePositionDropdown = (key: number) => {
        const newProjectForm = { ...projectForm };
        (newProjectForm['positions'] as positionForm[]).splice(key, 1);
        setProjectForm(newProjectForm);
    }

    const handleProjectFormChange = (parameter: keyof typeof projectForm, value: string|User[]|positionForm[]) => {
        const newProjectForm = { ...projectForm };
        newProjectForm[parameter] = value as string&User[]&positionForm[]; // not sure why this is needed but it errors without
        setProjectForm(newProjectForm);
    };

    // TODO add select coaches field
    return (
        <form
            onSubmit={(e) => {
                e.preventDefault();
                // TODO add submit function
                // TODO don't forget to reset projectForm to default afterwards
                setShowPopup(false);
            }}
        >

            <label className="block">
                Project Name
                <input
                    className="border-2 block"
                    name="projectName"
                    type="text"
                    value={projectForm.projectName as string}
                    onChange={(e) => handleProjectFormChange('projectName', e.target.value)}
                />
            </label>

            <label className="block">
                Client Name
                <input
                    className="border-2 block"
                    name="clientName"
                    type="text"
                    value={projectForm.clientName as string}
                    onChange={(e) => handleProjectFormChange('clientName', e.target.value)}
                />
            </label>
            {/* This is a fix to stop clicking on the clearable closing the entire modal */}
            <div
                onClick={(e) => e.stopPropagation()}
                onMouseDown={(e) => e.stopPropagation()}
            >
                <Fragment>

                    {(projectForm.positions as positionForm[]).map((position, index) => {
                        return (
                            <div className="flex flex-row" key={index}>
                                <label className="block">
                                    Position
                                    <Select
                                        className="basic-single"
                                        classNamePrefix="select"
                                        isDisabled={false}
                                        isLoading={false}
                                        isClearable={true}
                                        isRtl={false}
                                        isSearchable={true}
                                        name={`"Position-" +${index}`}
                                        value={position.skill}
                                        options={[
                                            { value: 'chocolate', label: 'Chocolate' },
                                            { value: 'strawberry', label: 'Strawberry' },
                                            { value: 'vanilla', label: 'Vanilla' },
                                        ]} // TODO fix this once backend has getAllSKills endpoint implemented
                                        placeholder="Position"
                                        onChange={(e) => setPositionDropdownValue(index, e ? e : {} as { value: string; label: string })}
                                    />
                                </label>
                                <label className="mx-auto mb-4 block text-left lg:mb-8 lg:max-w-sm">
                                    Amount
                                    <input
                                        className="mt-1 box-border block h-8 w-full border-2 border-[#C4C4C4] p-1 text-sm"
                                        name={`"Amount-" +${index}`}
                                        type="number"
                                        value={position.amount}
                                        onChange={(e) => setPositionDropdownAmount(index, e.target.value)}
                                    />
                                </label>
                                <div className="flex flex-col justify-center">
                                    <i
                                        onClick={() => {
                                            console.log(index);
                                            removePositionDropdown(index);
                                        }}
                                        className="icon-xcircle-red text-2xl"
                                    >
                                        {xmark_circle}
                                    </i>
                                </div>
                            </div>
                        );
                    })}
                </Fragment>
                {/* TODO make this button more obvious */}
                <button onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    addPositionDropdown();
                }} className="btn btn-primary">
                    + Add Position
                </button>
            </div>

            <textarea
                placeholder="Project description"
                className="mt-3 w-full resize-y border-2 border-check-gray"
                value={projectForm.description as string}
                onChange={(e) => handleProjectFormChange('description',e.target.value || '')}
            />

            <button className={`border-2`} type={`submit`}>
                Create Project
            </button>
        </form>
    );
};


export default ProjectPopup;