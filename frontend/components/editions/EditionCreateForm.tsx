import { Dispatch, SetStateAction, useState } from "react";
import { PhotographIcon } from "@heroicons/react/outline";

type ECFProps = {
  setShowCreateForm: Dispatch<SetStateAction<boolean>>;
  createEdition: (name: string) => void;
}

const EditionCreateForm: React.FC<ECFProps> = ({ setShowCreateForm, createEdition }: ECFProps) => {
  const [name, setName] = useState('');
  
  const close = () => {
    setName('');
    setShowCreateForm(false);
  }

  const create = () => {
    const preprocessedName = name.trim();

    if (preprocessedName) {
      createEdition(preprocessedName);

      close();
    }
  }
  
  return (
    <div className="max-w-sm m-auto grid grid-cols-2 border-2 overflow-hidden rounded-lg w-full h-full px-2 py-3">
      <div className="col-span-2 border-2">
        <PhotographIcon className="h-8 w-8 m-auto mt-8 hover:cursor-pointer" color="#b3b3b3"/>
      </div>
      <label className="col-span-2 mx-2 my-4">
      New Edition Name:
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        className="border-2 max-w-sm max-h-6 mt-1"
        required
      />
      </label>

      <button className="bg-osoc-yellow text-white px-2 py-1 mt-6 justify-self-center self-center" onClick={close}>
        Cancel
      </button>

      <button className="bg-osoc-btn-primary text-white px-2 py-1 mt-6 justify-self-center self-center" onClick={create}>
        Create
      </button>
    </div>
  )
}
export default EditionCreateForm;