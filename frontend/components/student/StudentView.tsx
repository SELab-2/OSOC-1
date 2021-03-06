import {
  Answer,
  StatusSuggestion,
  StatusSuggestionBase,
  Student,
  StudentBase,
  StudentBaseList,
  Url,
  User,
  UserRole,
  UUID,
} from '../../lib/types';
import { Fragment, useEffect, useState } from 'react';
import Select from 'react-select';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faCheck,
  faQuestion,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import {
  convertStudentBase,
  convertStudentBaseList,
  convertStudentFullToList,
} from '../../lib/conversionUtils';
import {
  fetchEditionState,
  getUrlList,
  getUrlMap,
  parseError,
} from '../../lib/requestUtils';
import { NextRouter } from 'next/dist/client/router';
import { useRouter } from 'next/router';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useUser from '../../hooks/useUser';
import Error from '../Error';
import StudentFormView from './StudentFormView';
import axios, { AxiosError } from 'axios';
import { Icon } from '@iconify/react';
import Popup from 'reactjs-popup';
import { getAnswerStrings } from '../../lib/tallyForm';

const check_mark = <FontAwesomeIcon icon={faCheck} />;
const question_mark = <FontAwesomeIcon icon={faQuestion} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;
const speech_bubble = <Icon icon="simple-line-icons:speech" />;
const trash_can = <Icon icon="fa-solid:trash-alt" />;

type StudentViewProp = {
  studentInput: StudentBaseList;
  setOriginalStudentBase: (originalStudentBase: StudentBaseList) => void;
};

type StatusSuggestionProp = {
  statusSuggestion: StatusSuggestion;
};

/**
 * Function to set the status of a student, this is an admin only function
 * This function does NOT check if the new status is different from the old one
 * @param status - new status for the student
 * @param studentId - id for student to change status for
 * @param myStudent - original student, used to avoid having to reload page
 * @param setMyStudent - callback to set new student object with changed status
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 400 response
 */
async function setStudentStatus(
  status: { value: string; label: string },
  studentId: UUID,
  myStudent: Student,
  setMyStudent: (myStudent: Student) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  await axiosAuthenticated
    .post(
      `/${edition}${Endpoints.STUDENTS}/${studentId}${Endpoints.STATUS}`,
      status.label
    )
    .then(() => {
      const newStudent = { ...myStudent };
      newStudent.status = status.label;
      setMyStudent(newStudent);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function to remove a suggestion from a user, the suggestion is defined by the id of the suggester
 *
 * @param studentId - id of student to remove a suggestion from
 * @param coachId - id of suggester for suggestion to remove
 * @param setStudentBase - callback to set reloaded student
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 400 response
 */
async function removeStudentSuggestion(
  studentId: UUID,
  coachId: UUID,
  setStudentBase: (studentBase: StudentBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  await axiosAuthenticated
    .delete(
      `/${edition}${Endpoints.STUDENTS}/${studentId}${Endpoints.SUGGESTIONS}/${coachId}`
    )
    .then(() => {
      reloadStudent(studentId, setStudentBase, signal, setError, router);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function to remove a student
 * This function does not perform any authority checks before deleting!
 *
 * @param studentId - id of student to remove
 * @param setOriginalStudentBase - Callback to set student object to null after deletion
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 400 response
 */
async function removeStudent(
  studentId: UUID,
  setOriginalStudentBase: (studentBaseList: StudentBaseList) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  await axiosAuthenticated
    .delete(`/${edition}${Endpoints.STUDENTS}/${studentId}`)
    .then(() => {
      setOriginalStudentBase({} as StudentBaseList);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function to set a new suggestion for a student, student will be reloaded afterwards
 * Note: This function will perform a delete first since suggestions can't be changed
 *
 * @param status - status to suggest
 * @param studentId - id of student to set suggestion for
 * @param coachId - id of suggester, should be current user
 * @param motivation - motivation for the suggestion
 * @param setStudentBase - callback to set new student after reload
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 400 response
 */
async function setStudentSuggestion(
  status: string,
  studentId: UUID,
  coachId: UUID,
  motivation: string,
  setStudentBase: (studentBase: StudentBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  await axiosAuthenticated
    .delete(
      `/${edition}${Endpoints.STUDENTS}/${studentId}${Endpoints.SUGGESTIONS}/${coachId}`
    )
    .catch((err) => {
      // Ignore 400 bad request error since we're deleting without knowing if it exists
      // A 404 student not found or 401 user is not coachId are not an errors we want to ignore
      if (axios.isAxiosError(err)) {
        const _error = err as AxiosError;
        if (_error.response?.status !== 400) {
          parseError(err, setError, router, signal);
        }
      } else {
        parseError(err, setError, router, signal);
      }
    });

  await axiosAuthenticated
    .post(
      `/${edition}${Endpoints.STUDENTS}/${studentId}${Endpoints.SUGGESTIONS}`,
      {
        suggester: '/' + edition + Endpoints.USERS + '/' + coachId,
        status: status,
        motivation: motivation,
      }
    )
    .then(() => {
      reloadStudent(studentId, setStudentBase, signal, setError, router);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Reload student with given id, the reload URL is hardcoded
 *
 * @param studentId - id of student to reload
 * @param setStudentBase - callback to set the response from the reload
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 400 response
 */
function reloadStudent(
  studentId: UUID,
  setStudentBase: (studentBase: StudentBase) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  const edition = router.query.editionName as string;
  axiosAuthenticated
    .get<StudentBase>('/' + edition + Endpoints.STUDENTS + '/' + studentId)
    .then((response) => {
      setStudentBase(response.data as StudentBase);
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

// WONTFIX Communication not included
/**
 * Function to dereference needed student fields
 *
 * @param studentBase - base object with fields to dereference
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 400 response
 */
async function getEntireStudent(
  studentBase: StudentBase,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
): Promise<Student> {
  const newStudent = convertStudentBase(studentBase);
  const statusSuggestionBaseList: StatusSuggestionBase[] = [];
  await getUrlList<StatusSuggestionBase>(
    studentBase.statusSuggestions,
    statusSuggestionBaseList,
    signal,
    setError,
    router
  );
  await getUrlList<Answer>(
    studentBase.answers,
    newStudent.answers,
    signal,
    setError,
    router
  );

  const suggesterMap = new Map<Url, User>();
  await getUrlMap<User>(
    statusSuggestionBaseList.map(
      (suggestionBase) => suggestionBase.suggester
    ) as Url[],
    suggesterMap,
    signal,
    setError,
    router
  );

  for (const suggestion of statusSuggestionBaseList) {
    const statusSuggestion = {} as StatusSuggestion;
    statusSuggestion.suggester = suggesterMap.get(suggestion.suggester) as User;
    statusSuggestion.status = suggestion.status;
    statusSuggestion.motivation = suggestion.motivation;
    newStudent.statusSuggestions.push(statusSuggestion);
  }

  return newStudent;
}

const StudentView: React.FC<StudentViewProp> = ({
  studentInput,
  setOriginalStudentBase,
}: StudentViewProp) => {
  const [user] = useUser();
  // Needed to reload student when a suggestion is done or status is changed
  const [studentBaseList, setStudentBaseList] = useState(
    studentInput as StudentBaseList
  );
  // WONTFIX don't reload everything when only status or suggestions are changed, save the rest somewhere
  const [studentBase, setStudentBase] = useState({} as StudentBase);
  const [myStudent, setMyStudent]: [Student, (myStudent: Student) => void] =
    useState(convertStudentBaseList(studentInput) as Student);

  const [status, setStatus] = useState({
    value: '',
    label: studentBase.status,
  } as { value: string; label: string });
  const [suggestion, setSuggestion] = useState('');
  const [motivation, setMotivation] = useState('');
  const [deletePopup, setDeletePopup] = useState(false);
  const [editionActive, setEditionActive] = useState(true);
  const [error, setError] = useState('');
  const router = useRouter();
  let controller = new AbortController();

  fetchEditionState(setEditionActive, setError, router);

  useEffect(() => {
    setStudentBaseList(studentInput);
  }, [studentInput]);

  useEffect(() => {
    setMotivation('');
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    reloadStudent(studentBaseList.id, setStudentBase, signal, setError, router);
    return () => {
      controller.abort();
    };
  }, [studentBaseList]);

  useEffect(() => {
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    if (studentBase.id !== undefined && studentBase.id !== studentInput.id) {
      setOriginalStudentBase(convertStudentFullToList(studentBase));
    }
    // This is a safety check, not really needed right now but it avoids accidents
    if (studentBase.id !== undefined) {
      setStatus({ value: '', label: studentBase.status } as {
        value: string;
        label: string;
      });
      getEntireStudent(studentBase, signal, setError, router).then(
        (response) => {
          setMyStudent(response);
        }
      );
    }
    return () => {
      controller.abort();
    };
  }, [studentBase]);

  useEffect(() => {
    setMotivation('');
    myStudent.statusSuggestions.forEach((suggestion) => {
      if (suggestion.suggester.id === user.id) {
        setMotivation(suggestion.motivation);
      }
    });
  }, [myStudent]);

  const answers = getAnswerStrings(myStudent.answers);
  const pronouns = answers.commonPronouns || answers.otherPronouns;
  return (
    <div className={`flex flex-col-reverse justify-between xl:flex-row`}>
      {error && <Error error={error} setError={setError} className="mb-4" />}
      {/* hold the student information */}
      <div className="mx-8 flex flex-col bg-osoc-neutral-bg px-4 py-3">
        <div className="flex flex-row pt-2">
          <h1 className="text-4xl font-semibold">
            {answers.preferredName ||
              myStudent.firstName + ' ' + myStudent.lastName}
          </h1>
          {pronouns != undefined && <p className="pt-2 pl-2">{pronouns}</p>}
          {user.role == UserRole.Admin && (
            <div className="ml-2 flex flex-col justify-center">
              <i
                onClick={() => {
                  setDeletePopup(true);
                }}
                className="icon-trashcan-red cursor-pointer"
              >
                {trash_can}
              </i>
            </div>
          )}
        </div>
        <div className="flex flex-col">
          <h3 className="pt-12 text-2xl">Suggestions</h3>
          {myStudent.statusSuggestions.length === 0 && (
            <p>No suggestions yet.</p>
          )}
          {myStudent.statusSuggestions.map((statusSuggestion) => (
            <StudentStatusSuggestion
              key={statusSuggestion.suggester.id}
              statusSuggestion={statusSuggestion}
            />
          ))}
        </div>
        <StudentFormView answers={answers} />
      </div>

      {/* holds suggestion controls */}
      <div
        className={
          `mr-8 ml-8 mb-6 flex flex-col xl:mb-0 xl:ml-0 ` +
          (editionActive ? 'visible' : 'hidden')
        }
      >
        {/* regular coach status suggestion form */}
        <form
          className={`border-2 p-2`}
          onSubmit={(e) => {
            e.preventDefault();
            e.stopPropagation();
            controller.abort();
            controller = new AbortController();
            const signal = controller.signal;
            if (suggestion == 'Remove') {
              removeStudentSuggestion(
                studentBase.id,
                user.id,
                setStudentBase,
                signal,
                setError,
                router
              );
            } else {
              setStudentSuggestion(
                suggestion,
                studentBase.id,
                user.id,
                motivation,
                setStudentBase,
                signal,
                setError,
                router
              );
            }
            return () => {
              controller.abort();
            };
          }}
        >
          <div className={`flex w-[400px] flex-row justify-between`}>
            <button
              className={`w-[30%] rounded-sm bg-check-green py-[2px] px-1 py-1 text-sm font-medium text-white hover:brightness-95`}
              onClick={() => setSuggestion('Yes')}
              type={`submit`}
            >
              Suggest Yes
            </button>
            <button
              className={`w-[30%] rounded-sm bg-check-orange py-[2px] px-1 py-1 text-sm font-medium text-black hover:brightness-95`}
              onClick={() => setSuggestion('Maybe')}
              type={`submit`}
            >
              Suggest Maybe
            </button>
            <button
              className={`w-[30%] rounded-sm bg-check-red py-[2px] px-1 py-1 text-sm font-medium text-white hover:brightness-95`}
              onClick={() => setSuggestion('No')}
              type={`submit`}
            >
              Suggest No
            </button>
          </div>
          <textarea
            placeholder="Motivation"
            className="mt-3 min-h-[100px] w-full resize-y rounded border"
            value={motivation}
            onChange={(e) => setMotivation(e.target.value || '')}
          />
          {/* button to remove suggestion, only shows when user has a suggestion */}
          {myStudent.statusSuggestions.filter(
            (sugg) => sugg.suggester.id === user.id
          ).length > 0 && (
            <button
              className={`w-[100%] rounded-sm bg-check-gray py-[2px] text-sm text-white hover:brightness-95`}
              onClick={() => setSuggestion('Remove')}
              type={`submit`}
            >
              Remove Suggestion
            </button>
          )}
        </form>

        {/* admin status selection form */}
        <form
          className={`${
            user.role == UserRole.Admin ? 'visible' : 'hidden'
          } mt-5 flex flex-row justify-between border-2 p-2`}
          onSubmit={(e) => {
            e.preventDefault();
            if (status.label != myStudent.status) {
              controller.abort();
              controller = new AbortController();
              const signal = controller.signal;
              setStudentStatus(
                status,
                myStudent.id,
                myStudent,
                setMyStudent,
                signal,
                setError,
                router
              );
              return () => {
                controller.abort();
              };
            }
          }}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <Fragment>
              {/* TODO fix this becoming wider when something is selected */}
              <Select
                className="basic-single"
                classNamePrefix="select"
                isDisabled={false}
                isLoading={false}
                isClearable={false}
                isRtl={false}
                isSearchable={false}
                isMulti={false}
                name="Status"
                placeholder="Select Status"
                // WONTFIX don't hardcode this
                options={[
                  { value: 'Yes', label: 'Yes' },
                  { value: 'No', label: 'No' },
                  { value: 'Maybe', label: 'Maybe' },
                  { value: 'Undecided', label: 'Undecided' },
                ]}
                value={status}
                onChange={(e) => {
                  e
                    ? setStatus(e)
                    : setStatus({} as { value: string; label: string });
                }}
              />
            </Fragment>
          </div>

          {/* button to submit the admin status choice */}
          <button
            className={`rounded-sm bg-check-gray px-2 py-[2px] text-sm hover:brightness-95`}
            type={`submit`}
          >
            Submit
          </button>
        </form>
      </div>
      {/* This is the popup to confirm deleting a student */}
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
            Are you sure you wish to permanently delete{' '}
            <i>{studentBase.firstName + ' ' + studentBase.lastName}</i>?
          </h3>
          <div className="mt-3 flex flex-row justify-between px-5">
            <button
              onClick={() => setDeletePopup(false)}
              className={`min-w-[120px] border-2 bg-white`}
            >
              Cancel
            </button>

            <button
              className={`min-w-[120px] border-2 bg-check-red py-1`}
              onClick={() => {
                setDeletePopup(false);
                if (user.role == UserRole.Admin) {
                  controller.abort();
                  controller = new AbortController();
                  const signal = controller.signal;
                  removeStudent(
                    myStudent.id,
                    setOriginalStudentBase,
                    signal,
                    setError,
                    router
                  );
                  return () => {
                    controller.abort();
                  };
                }
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

const StudentStatusSuggestion: React.FC<StatusSuggestionProp> = ({
  statusSuggestion,
}: StatusSuggestionProp) => {
  let myLabel = question_mark;
  let myColor = 'text-check-orange';
  if (statusSuggestion.status == 'Yes') {
    myLabel = check_mark;
    myColor = 'text-check-green';
  } else if (statusSuggestion.status == 'No') {
    myLabel = x_mark;
    myColor = 'text-check-red';
  }

  return (
    <div className="flex flex-row items-center">
      <i className={`${myColor} w-[30px] px-2`}>{myLabel}</i>
      <p className="">{statusSuggestion.suggester.username}</p>
      <div className="tooltip pl-2 pt-1">
        <i className="icon-speech-blue text-xs">{speech_bubble}</i>
        {/* TODO Make this tooltip look nicer */}
        <span className="tooltiptext w-fit bg-gray-200 px-2">
          {statusSuggestion.motivation}
        </span>
      </div>
    </div>
  );
};

export default StudentView;
