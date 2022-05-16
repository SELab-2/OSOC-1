import { Dispatch, FC, SetStateAction, useState } from 'react';
import Popup from 'reactjs-popup';
import { SpinnerCircular } from 'spinners-react';
import { User } from '../../lib/types';

type UDFProps = {
  /**
   * name of user to delete
   */
  userName: string;

  /**
   * function that deletes the user in the backend service
   *
   * @see {@link USERS_PAGE}
   */
  deleteUser: () => Promise<void>;

  /**
   * state that holds whether the deletion confirm should be shown or not
   */
  openDeleteForm: boolean;

  /**
   * state update function that sets whether the deletion confirm should be shown or not
   */
  setUserDeleteForm: Dispatch<SetStateAction<User | undefined>>;
};

/**
 *
 * @param EDPProps - User Deletion Popup Props
 * @returns User Deletion Popup Component
 */
const UserDeleteForm: FC<UDFProps> = ({
  userName,
  deleteUser,
  openDeleteForm,
  setUserDeleteForm,
}: UDFProps) => {
  const [loading, setLoading] = useState(false);

  const closePopup = () => {
    setUserDeleteForm(undefined);
  };

  const doDelete = async () => {
    setLoading(true);
    await deleteUser();
    setLoading(false);
    closePopup();
  };

  return (
    <Popup
      modal
      open={openDeleteForm}
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
        <section className="grid grid-cols-2 justify-items-center gap-y-4">
          <p className="col-span-2 text-center">
            Are you sure you want to remove &quot;{userName}&quot; ?
          </p>

          {loading ? (
            <SpinnerCircular
              size={60}
              thickness={80}
              color="#FCB70F"
              secondaryColor="rgba(252, 183, 15, 0.4)"
              className="col-span-2 mx-auto"
            />
          ) : (
            <>
              <button
                className="col-span-1 bg-gray-500 px-3 py-1"
                onClick={closePopup}
              >
                No
              </button>
              <button
                className="col-span-1 bg-check-green px-3 py-1"
                onClick={doDelete}
              >
                Yes
              </button>
            </>
          )}
        </section>
      </div>
    </Popup>
  );
};
export default UserDeleteForm;
