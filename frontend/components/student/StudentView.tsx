import {
  Answer,
  StatusSuggestion,
  Student,
  StudentBase,
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
import { convertStudentBase } from '../../lib/conversionUtils';
import { getUrlList, parseError } from '../../lib/requestUtils';
import { NextRouter } from 'next/dist/client/router';
import { useRouter } from 'next/router';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useUser from '../../hooks/useUser';
const check_mark = <FontAwesomeIcon icon={faCheck} />;
const question_mark = <FontAwesomeIcon icon={faQuestion} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;

type StudentViewProp = {
  studentInput: StudentBase;
};

type StatusSuggestionProp = {
  statusSuggestion: StatusSuggestion;
  coachMap: Map<UUID, string>;
};

// TODO Communication not yet included!!
// TODO currently a suggestion has a coachId and not a User object, needs to be fixed in backend first
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
): Promise<[Student, Map<UUID, string>]> {
  const newStudent = convertStudentBase(studentBase);
  await getUrlList<StatusSuggestion>(
    studentBase.statusSuggestions,
    newStudent.statusSuggestions,
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

  // TODO temp solution until this gets fixed
  const coaches = new Map<UUID, string>();
  await axiosAuthenticated
    .get<User[]>(Endpoints.USERS, { signal: signal })
    .then((response) => {
      (response.data as User[]).forEach((coach) => {
        coaches.set(coach.id, coach.username);
      });
    })
    .catch((err) => {
      parseError(err, setError, signal, router);
    });

  return [newStudent, coaches];
}

const StudentView: React.FC<StudentViewProp> = ({
  studentInput,
}: StudentViewProp) => {
  const [user] = useUser();
  // Needed to reload student when a suggestion is done or status is changed
  // TODO don't reload everything when only status or suggestions or changed, save the rest somewhere
  const [studentBase, setStudentBase] = useState(studentInput as StudentBase);
  const [myStudent, setMyStudent]: [Student, (myStudent: Student) => void] =
    useState(convertStudentBase(studentBase) as Student);

  const [coachMap, setCoachMap] = useState(new Map<UUID, string>());

  const [, setError]: [string, (error: string) => void] = useState('');
  const router = useRouter();
  let controller = new AbortController();

  useEffect(() => {
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    // This is a safety check, not really needed right now but it avoids accidents
    if (studentInput.id !== undefined) {
      getEntireStudent(studentInput, signal, setError, router).then(
        (response) => {
          setMyStudent(response[0]);
          setCoachMap(response[1]);
        }
      );
    }
    return () => {
      controller.abort();
    };
  }, [studentInput, studentBase]);

  return (
    <div className={`flex flex-col-reverse justify-between xl:flex-row`}>
      {/* hold the student information */}
      <div className="mx-8 flex flex-col bg-osoc-neutral-bg">
        <div>
          <h4 className="font-bold">
            {myStudent.firstName + ' ' + myStudent.lastName}
          </h4>
        </div>
        <div className="flex flex-col">
          <h5 className="font-bold">Suggestions</h5>
          {myStudent.statusSuggestions.map((statusSuggestion) => (
            <StudentStatusSuggestion
              key={statusSuggestion.coachId}
              statusSuggestion={statusSuggestion}
              coachMap={coachMap}
            />
          ))}
        </div>
        <div className="mt-4 flex flex-col">
          <h5 className="font-bold">Answers:</h5>
          {myStudent.answers.map((answer) => (
            <div key={answer.id}>
              <p>{answer.question}</p>
              <p>{answer.answer}</p>
              <br />
            </div>
          ))}
        </div>
      </div>

      {/* holds suggestion controls */}
      <div className={`mr-6 ml-6 mb-6 flex flex-col xl:mb-0 xl:ml-0`}>
        {/* regular coach status suggestion form */}
        <form className={`border-2 p-2`}>
          <div className={`flex w-[380px] flex-row justify-between`}>
            <button
              className={`w-[30%] bg-check-green py-[2px] text-sm text-white shadow-md shadow-gray-400`}
            >
              Suggest Yes
            </button>
            <button
              className={`w-[30%] bg-check-orange py-[2px] text-sm text-white shadow-md shadow-gray-400`}
            >
              Suggest Maybe
            </button>
            <button
              className={`w-[30%] bg-check-red py-[2px] text-sm text-white shadow-md shadow-gray-400`}
            >
              Suggest No
            </button>
          </div>
          <textarea
            placeholder="Motivation"
            className="mt-3 w-full resize-y border-2 border-check-gray"
            required
          />
        </form>

        {/* admin status selection form */}
        <form
          className={`${
            user.role == UserRole.Admin ? 'visible' : 'hidden'
          } mt-10 flex flex-row justify-between border-2 p-2`}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <Fragment>
              {/* TODO fix this becoming wider when something is selected */}
              {/* TODO fix this looking horrible when a lot is selected */}
              <Select
                className="basic-single"
                classNamePrefix="select"
                isDisabled={false}
                isLoading={false}
                isClearable={true}
                isRtl={false}
                isSearchable={true}
                isMulti={true}
                name="Status"
                placeholder="Select Status"
                options={[
                  { value: '', label: 'Yes' },
                  { value: '', label: 'No' },
                  { value: '', label: 'Maybe' },
                  { value: '', label: 'Undecided' },
                ]}
                // value={skills}
                // options={skillOptions}
                // onChange={(e) =>
                //     setSkills(
                //         e.map((x) => {
                //           return { value: x.value, label: x.label };
                //         })
                //     )
                // }
              />
            </Fragment>
          </div>

          {/* button to submit the admin status choice */}
          <button
            className={`bg-check-gray px-2 py-[2px] text-sm shadow-md shadow-gray-400`}
          >
            Submit
          </button>
        </form>
      </div>
    </div>
  );
};

// TODO This should get the coach name somehow
const StudentStatusSuggestion: React.FC<StatusSuggestionProp> = ({
  statusSuggestion,
  coachMap,
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
    <div className="flex flex-row">
      <i className={`${myColor} w-[30px] px-2`}>{myLabel}</i>
      <p className="">{coachMap.get(statusSuggestion.coachId)}</p>
    </div>
  );
};

export default StudentView;
