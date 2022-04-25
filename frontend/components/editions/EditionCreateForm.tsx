import { Dispatch, SetStateAction, useState } from 'react';
import { PhotographIcon } from '@heroicons/react/outline';

type ECFProps = {
  /**
   * State setter to toggle showing the create form
   */
  setShowCreateForm: Dispatch<SetStateAction<boolean>>;

  /**
   * Function that takes an edition name and creates a new edition with that name.
   * 
   * @see {@link EDITIONS_PAGE}
   */
  createEdition: (name: string) => void;
};

/**
 * Edition create form in the shape of an edition card
 * 
 * @param ECFProps - @see {@link ECFProps}
 * @returns Edition Create Form Component
 */
const EditionCreateForm: React.FC<ECFProps> = ({
  setShowCreateForm,
  createEdition,
}: ECFProps) => {
  const [name, setName] = useState('');

  const close = () => {
    setName('');
    setShowCreateForm(false);
  };

  const create = () => {
    const preprocessedName = name.trim();

    if (preprocessedName) {
      createEdition(preprocessedName);

      close();
    }
  };

  return (
    <div className="m-auto grid h-full w-full max-w-sm grid-cols-2 overflow-hidden rounded-lg border-2 px-2 py-3">
      <div className="col-span-2 border-2">
        <PhotographIcon
          className="m-auto mt-8 h-8 w-8 hover:cursor-pointer"
          color="#b3b3b3"
        />
      </div>
      <label className="col-span-2 mx-2 my-4">
        New Edition Name:
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="mt-1 max-h-6 max-w-sm border-2"
          required
        />
      </label>

      <button
        className="mt-6 self-center justify-self-center bg-osoc-yellow px-2 py-1 text-white"
        onClick={close}
      >
        Cancel
      </button>

      <button
        className="mt-6 self-center justify-self-center bg-osoc-btn-primary px-2 py-1 text-white"
        onClick={create}
      >
        Create
      </button>
    </div>
  );
};
export default EditionCreateForm;
