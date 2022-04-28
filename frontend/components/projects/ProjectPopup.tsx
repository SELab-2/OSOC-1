import { Fragment, useEffect, useState } from 'react';
import { Icon } from '@iconify/react';
import { Project, ProjectBase, Url, User, UUID } from '../../lib/types';
import CreatableSelect from 'react-select/creatable';
import { getCoaches, getSkills, parseError } from '../../lib/requestUtils';
import Select from 'react-select';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import Error from '../Error';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';
const xmark_circle = <Icon icon="akar-icons:circle-x" />;

/**
 * This is what ProjectPopup expects as its arguments
 * @See ProjectPopup for more information
 */
type ProjectPopupProp = {
  projectForm: ProjectForm;
  setShowPopup: (showPopup: boolean) => void;
  setProjectForm: (projectForm: ProjectForm) => void;
  setError: (error: string) => void;
  setMyProjectBase: (myProjectBase: ProjectBase) => void;
  setDeletePopup: (deletePopup: boolean) => void;
};

/**
 * This is how a position is saved inside the form
 * The amount is a string since input fields always return a string
 * The skill is a react-select options field, the value holds the UUID of the POSITION if editing a project
 * When creating a new project the skill value will be empty
 * The skill label holds the skill name that is part of the position
 */
type positionForm = {
  amount: string; // This is a string because input field always return a string
  skill: { value: UUID; label: string }; // The weird react-select options type
};

/**
 * This is the type for the ProjectForm used to keep track of what to send on a post or patch project
 * assignments is not a value that is ever changed via this form, we simply need it for patch requests
 */
type ProjectForm = {
  id: Url;
  projectName: string;
  clientName: string;
  description: string;
  coaches: User[];
  positions: positionForm[];
  assignments: Url[];
};

/**
 * Always unpack this value and use JSON.parse JSON.stringify when assigning to avoid strange behaviour
 */
const defaultPosition = {
  amount: '',
  skill: {} as { value: string; label: string },
};

/**
 * Always unpack this value and use JSON.parse JSON.stringify when assigning to avoid strange behaviour
 */
export const defaultprojectForm = {
  id: '',
  projectName: '',
  clientName: '',
  description: '',
  coaches: [] as User[],
  positions: [
    JSON.parse(JSON.stringify({ ...defaultPosition })),
  ] as positionForm[],
  assignments: [] as Url[],
} as ProjectForm;

/**
 * Function to create a ProjectForm as expected by ProjectPopup from a Project
 * Note that the position.id will be saved in the position skill object
 * as the value, since this is a react-select options object
 *
 * @param project - the project to use as a base
 * @param assignmentUrls - a list of the assignment urls that are part of the given project
 */
export function projectFormFromProject(
  project: Project,
  assignmentUrls: Url[]
): ProjectForm {
  const newProjectForm = { ...defaultprojectForm };
  newProjectForm.id = project.id || '';
  newProjectForm.projectName = project.name || '';
  newProjectForm.clientName = project.clientName || '';
  newProjectForm.description = project.description || '';
  newProjectForm.coaches = project.coaches || [];
  newProjectForm.positions = project.positions
    ? project.positions.map((value) => {
        return {
          amount: value.amount.toString(),
          skill: { value: value.id, label: value.skill.skillName },
        };
      })
    : ([JSON.parse(JSON.stringify({ ...defaultPosition }))] as positionForm[]);
  newProjectForm.assignments = assignmentUrls || ([] as Url[]);
  return newProjectForm;
}

/**
 * Function to post or patch a new project
 * Depending on if the value projectForm.id is present, a post or patch will be done
 *
 * @param projectForm - the form containing all needed fields to post or patch a project
 * @param setMyProjectBase - callback function to set result (can be used for reloading)
 * @param signal - AbortSignal for the axios request
 * @param setError - callback to set error message
 * @param router - Router object needed for edition parameter & error handling on 400 response
 */
function postOrPatchProject(
  projectForm: ProjectForm,
  setMyProjectBase: (myProjectBase: ProjectBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  const data = {
    name: projectForm.projectName,
    clientName: projectForm.clientName,
    description: projectForm.description,
    coaches: projectForm.coaches,
    positions: projectForm.positions.map((position) => {
      return {
        skill: {
          skillName: position.skill.label,
        },
        amount: position.amount,
      };
    }),
  } as { [key: string]: unknown };

  if (projectForm.id) {
    data.id = projectForm.id;
    data.assignments = projectForm.assignments;
    data.edition = edition;
    data.positions = projectForm.positions.map((position) => {
      const newPos = {
        skill: {
          skillName: position.skill.label,
        },
        amount: position.amount,
      } as { [key: string]: unknown };
      position.skill.value ? (newPos.id = position.skill.value) : null;
      return newPos;
    });
  }

  (projectForm.id
    ? axiosAuthenticated.patch
    : axiosAuthenticated.post)<ProjectBase>(
    '/' +
      edition +
      Endpoints.PROJECTS +
      (projectForm.id ? '/' + projectForm.id : ''),
    data
  )
    .then((response) => {
      setMyProjectBase(response.data as ProjectBase);
    })
    .catch((err) => {
      parseError(err, setError, new AbortController().signal, router);
    });
}

/**
 * Function to check if all position dropdown values were filled correctly
 * @param projectForm - projectForm containing positions
 * @param setFormError - callback to set error message
 */
function checkPositions(
  projectForm: ProjectForm,
  setFormError: (formError: string) => void
) {
  if (
    projectForm.positions.filter(
      (position) => !position.skill || !position.skill.label
    ).length > 0
  ) {
    setFormError('Each position must have a skill.');
    return false;
  }
  if (
    new Set(projectForm.positions.map((position) => position.skill.label))
      .size != projectForm.positions.length
  ) {
    setFormError('Each position must be unique.');
    return false;
  }
  setFormError('');
  return true;
}

/**
 * This will return a form element to be placed inside a Popup element
 * This form does not include a title and should be placed inside an element with
 * overflow-y-auto and either the element or a parent should have a max height set
 *
 * @param projectForm - use either defaultprojectForm or projectFormFromProject(Project)
 * @param setShowPopup - callback to set to false when popup should close (on from submission or cancel)
 * @param setProjectForm - callback to use to change the passed projectForm, needed to save user changes
 * @param setError - callback to set error message
 * @param setMyProjectBase - callback for the updated ProjectBase object
 * @param setDeletePopup - callback to open a confirm project deletion popup
 */
const ProjectPopup: React.FC<ProjectPopupProp> = ({
  projectForm,
  setShowPopup,
  setProjectForm,
  setError,
  setMyProjectBase,
  setDeletePopup,
}: ProjectPopupProp) => {
  const router = useRouter();
  /**
   * Update the position dropdown value for the position at index key in projectForm
   * @param key - the index in projectForm['positions']
   * @param value - the selected option value
   */
  const setPositionDropdownValue = (
    key: number,
    value: { value: string; label: string }
  ) => {
    const newProjectForm = { ...projectForm };
    (newProjectForm['positions'] as positionForm[])[key].skill = value;
    setProjectForm(newProjectForm);
  };

  /**
   * Update the amount for the position at index key in projectForm
   * @param key - the index in projectForm['positions']
   * @param amount - the given amount as a string
   */
  const setPositionDropdownAmount = (key: number, amount: string) => {
    const newProjectForm = { ...projectForm };
    (newProjectForm['positions'] as positionForm[])[key].amount = amount;
    setProjectForm(newProjectForm);
  };

  /**
   * Create a new empty position
   */
  const addPositionDropdown = () => {
    const newProjectForm = { ...projectForm };
    (newProjectForm['positions'] as positionForm[]).push(
      JSON.parse(JSON.stringify({ ...defaultPosition })) as positionForm
    );
    setProjectForm(newProjectForm);
  };

  /**
   * Remove the position at index key in projectForm, this will also remove that dropdown selector
   * @param key - the index in projectForm['positions'] to remove
   */
  const removePositionDropdown = (key: number) => {
    const newProjectForm = { ...projectForm };
    (newProjectForm['positions'] as positionForm[]).splice(key, 1);
    setProjectForm(newProjectForm);
  };

  /**
   * Updates the value in projectForm referenced by given parameter
   * If value is an array type, please use one of the function for that use case
   *
   * @param parameter - what projectForm[parameter] to update
   * @param value - what the new value for projectForm[parameter] should be
   */
  const handleProjectFormChange = (
    parameter: keyof typeof projectForm,
    value: string | User[] | positionForm[] | Url[]
  ) => {
    const newProjectForm = { ...projectForm };
    newProjectForm[parameter] = value as string &
      User[] &
      positionForm[] &
      Url[]; // not sure why this is needed but it errors without
    setProjectForm(newProjectForm);
  };

  let controller = new AbortController();

  const [skillOptions, setSkillOptions] = useState(
    [] as Array<{ value: string; label: string }>
  );

  const [coachOptions, setCoachOptions] = useState(
    [] as Array<{ value: User; label: string }>
  );

  const [formError, setFormError] = useState('');

  /**
   * When a new skill is created by the user, add it to the options
   * @param option - the skillname to be added
   */
  const addSkillOption = (option: string) => {
    const newSkillOptions = [...skillOptions];
    newSkillOptions.push({ value: '', label: option });
    setSkillOptions(newSkillOptions);
  };

  useEffect(() => {
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    getSkills(setSkillOptions, signal, setError, router);
    getCoaches(setCoachOptions, signal, setError, router);
    return () => {
      controller.abort();
    };
  }, []);

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        if (checkPositions(projectForm, setFormError)) {
          controller.abort();
          controller = new AbortController();
          const signal = controller.signal;
          postOrPatchProject(
            projectForm,
            setMyProjectBase,
            signal,
            setError,
            router
          );
          setShowPopup(false);
          return () => {
            controller.abort();
          };
        }
      }}
    >
      {formError && <Error error={formError} className="mb-4" />}
      <label className="mb-2 block px-5">
        Project Name
        <input
          className="block w-[60%] border-2 py-1"
          name="projectName"
          type="text"
          value={projectForm.projectName as string}
          onChange={(e) =>
            handleProjectFormChange('projectName', e.target.value)
          }
          required
        />
      </label>

      <label className="block px-5">
        Client Name
        <input
          className="block w-[60%] border-2 py-1"
          name="clientName"
          type="text"
          value={projectForm.clientName as string}
          onChange={(e) =>
            handleProjectFormChange('clientName', e.target.value)
          }
          required
        />
      </label>

      <label className="block px-5">
        Coaches
        <Select
          className="basic-single"
          classNamePrefix="select"
          isDisabled={false}
          isLoading={false}
          isClearable={true}
          isRtl={false}
          isSearchable={true}
          isMulti={true}
          name="Coaches"
          value={projectForm.coaches.map((coach) => {
            return {
              value: coach,
              label: coach.username,
            };
          })}
          options={coachOptions}
          placeholder="Select coaches"
          isOptionDisabled={(option) =>
            (projectForm.coaches as User[])
              .map((coach) => coach.username)
              .includes(option.label)
          }
          onChange={(e) =>
            handleProjectFormChange(
              'coaches',
              e.map((x) => x.value)
            )
          }
        />
      </label>

      {/* This is a fix to stop clicking on the clearable closing the entire modal */}
      <div
        className="my-6"
        onClick={(e) => e.stopPropagation()}
        onMouseDown={(e) => e.stopPropagation()}
      >
        <Fragment>
          {(projectForm.positions as positionForm[]).map((position, index) => {
            return (
              <div
                className={`flex flex-row justify-between px-5 ${
                  index % 2 == 0 ? 'bg-neutral-100' : 'bg-neutral-50'
                }`}
                key={index}
              >
                <label className="mb-4 mr-20 grow">
                  Position
                  <CreatableSelect
                    className="basic-single"
                    classNamePrefix="select"
                    isDisabled={false}
                    isLoading={false}
                    isClearable={true}
                    isRtl={false}
                    isSearchable={true}
                    name={`"Position-" +${index}`}
                    value={position.skill}
                    options={skillOptions}
                    isOptionDisabled={(option) =>
                      (projectForm['positions'] as positionForm[])
                        .map((v) => v.skill.label)
                        .includes(option.label)
                    }
                    placeholder="Position"
                    onChange={(e) =>
                      setPositionDropdownValue(
                        index,
                        e ? e : ({} as { value: string; label: string })
                      )
                    }
                    onCreateOption={(e) => {
                      addSkillOption(e);
                      setPositionDropdownValue(
                        index,
                        e
                          ? { value: '', label: e }
                          : ({} as { value: string; label: string })
                      );
                    }}
                  />
                </label>
                <div className="flex flex-row">
                  <label className="mb-4">
                    Amount
                    <input
                      className="mt-1 box-border block h-8 max-w-[120px] border-2 p-1 text-sm"
                      name={`"Amount-" +${index}`}
                      type="number"
                      min="1"
                      value={position.amount}
                      onChange={(e) =>
                        setPositionDropdownAmount(index, e.target.value)
                      }
                      required
                    />
                  </label>
                  <div className="ml-4 flex flex-col justify-center">
                    <i
                      onClick={() => {
                        console.log(index);
                        removePositionDropdown(index);
                      }}
                      className="icon-xcircle-red text-[36px]"
                    >
                      {xmark_circle}
                    </i>
                  </div>
                </div>
              </div>
            );
          })}
        </Fragment>
        {/* TODO make this button more obvious */}
        <button
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            addPositionDropdown();
          }}
          className="btn btn-primary mx-5 mt-1 border-2 bg-white p-2"
        >
          + Add Position
        </button>
      </div>

      <label className="pl-5">
        Description
        <textarea
          placeholder="Project description"
          className="mx-5 mt-1 w-[calc(100%-40px)] resize-y border-2 px-1"
          value={projectForm.description as string}
          onChange={(e) =>
            handleProjectFormChange('description', e.target.value || '')
          }
        />
      </label>

      <div className="mt-3 flex flex-row justify-between px-5">
        <button
          onClick={() => setShowPopup(false)}
          className={`min-w-[120px] border-2 bg-white`}
        >
          Cancel
        </button>

        <button
          onClick={(e) => {
            e.stopPropagation();
            e.preventDefault();
            setDeletePopup(true);
          }}
          className={`${
            projectForm.id ? 'visible' : 'hidden'
          } min-w-[120px] border-2 bg-check-red py-1`}
        >
          Delete
        </button>

        <button
          className={`min-w-[120px] border-2 bg-check-green py-1`}
          type={`submit`}
        >
          Confirm
        </button>
      </div>
    </form>
  );
};

export default ProjectPopup;
