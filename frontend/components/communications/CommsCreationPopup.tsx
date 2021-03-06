import { Dispatch, FC, FormEvent, SetStateAction, useState } from 'react';
import Select from 'react-select';
import Popup from 'reactjs-popup';
import { SpinnerCircular } from 'spinners-react';
import { StudentBaseCommunication } from '../../lib/types';

type CCPProps = {
  createComms: (studentId: string, message: string) => Promise<void>;
  openPopup: boolean;
  setOpenPopup: Dispatch<SetStateAction<boolean>>;
  students: StudentBaseCommunication[];
};

/**
 *
 * @param EDPProps - Edition Deletion Popup Props
 * @returns Edition Deletion Popup Component
 */
const CommsCreationPopup: FC<CCPProps> = ({
  createComms,
  openPopup,
  setOpenPopup,
  students,
}: CCPProps) => {
  const [student, setStudent] = useState<StudentBaseCommunication | undefined>(
    undefined
  );
  const [message, setMessage] = useState('');

  const [loading, setLoading] = useState(false);

  const closePopup = () => {
    setOpenPopup(false);
  };

  const doCreate = async () => {
    setLoading(true);

    if (!student || !message) return;
    await createComms(student.id, message);

    setLoading(false);
    closePopup();
  };

  const submit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    await doCreate();
  };

  return (
    <Popup
      modal
      open={openPopup}
      data-backdrop="static"
      data-keyboard="false"
      closeOnDocumentClick={false}
      lockScroll={true}
      position="center center"
    >
      <div className="modal max-w-screen flex max-h-[85vh] min-w-[600px] flex-col bg-osoc-neutral-bg py-5">
        <a
          className="close"
          onClick={(e) => {
            e.stopPropagation();
            closePopup();
          }}
        >
          &times;
        </a>
        <h3 className="mb-3 px-5 text-xl">Register communication</h3>
        <section className="">
          {loading ? (
            <SpinnerCircular
              size={60}
              thickness={80}
              color="#FCB70F"
              secondaryColor="rgba(252, 183, 15, 0.4)"
              className="col-span-2 mx-auto"
            />
          ) : (
            <form
              className="grid grid-cols-4 justify-items-center gap-y-2"
              onSubmit={submit}
            >
              <label className="col-span-4 w-full px-5">
                Student
                <Select
                  className="mt-1"
                  isMulti={false}
                  isSearchable={true}
                  onChange={(value) => {
                    if (!value) {
                      return;
                    }
                    const _stud = students.find((s) => s.id === value.value);
                    setStudent(_stud);
                  }}
                  value={
                    {
                      label: student
                        ? student.firstName + ' ' + student.lastName
                        : undefined,
                      value: student?.id,
                    } as { value: string; label: string }
                  }
                  placeholder="Student"
                  options={students.map((stud) => {
                    return {
                      label: stud.firstName + ' ' + stud.lastName,
                      value: stud.id,
                    };
                  })}
                />
              </label>
              <label className="col-span-4 w-full px-5">
                Information
                <textarea
                  placeholder="Communication Information"
                  className="mx-2 mt-1 ml-0 min-h-[150px] w-full resize-y rounded border px-1"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                />
              </label>
              <button
                className="col-span-1 col-start-2 col-end-3 rounded-sm bg-gray-500 px-3 py-1 hover:brightness-95"
                onClick={(e) => {
                  e.stopPropagation();
                  e.preventDefault();
                  closePopup();
                }}
              >
                Cancel
              </button>
              <button
                className="col-span-1 col-start-3 col-end-4 rounded-sm bg-check-green px-3 py-1 hover:brightness-95 disabled:cursor-not-allowed disabled:bg-green-200 disabled:text-gray-400 disabled:brightness-100"
                type="submit"
                disabled={!student || !message}
              >
                Create
              </button>
            </form>
          )}
        </section>
      </div>
    </Popup>
  );
};
export default CommsCreationPopup;
