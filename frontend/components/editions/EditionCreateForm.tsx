import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';

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
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

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
    <div className="m-auto grid h-fit w-full max-w-sm grid-cols-2 overflow-hidden rounded-lg border-2 px-2 py-3">
      <label className="col-span-2 mt-2 mb-4 mx-4">
        New Edition Name:
        <input
          ref={inputRef}
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="mt-1 max-h-6 max-w-sm border-2"
          required
        />
      </label>

      <button
        className="mt-2 self-center justify-self-center bg-osoc-yellow px-2 py-1 text-black"
        onClick={close}
      >
        Cancel
      </button>

      <button
        className="mt-2 self-center justify-self-center bg-osoc-btn-primary px-2 py-1 text-black"
        onClick={create}
      >
        Create
      </button>
    </div>
  );
};
export default EditionCreateForm;
