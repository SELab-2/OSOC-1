import { Edition } from "../../pages/editions";
import { TrashIcon } from "@heroicons/react/solid";

type EditionCardProps = {
  edition: Edition;
  updateEdition: (edition: string) => void;
  deleteEdition: (edition: string) => void;
}

const EditionCard: React.FC<EditionCardProps> = ({ edition: { name, isActive }, updateEdition, deleteEdition }: EditionCardProps) => {
  return (
    <section className="max-w-sm m-auto border-2 overflow-hidden rounded-lg grid grid-cols-4">
      <img className="col-span-4 select-none" src="../../img/mountains.jpg"/>
      <div className="ml-2 mt-2 col-start-1 col-end-4 justify-self-start">
        <p className="font-bold text-lg">{ name }</p>
      </div>
        <div className="col-start-4 col-end-5 justify-self-end mr-2 mt-2">
          <TrashIcon className="w-6 h-6 hover:cursor-pointer" color="#F14A3B" onClick={() => deleteEdition(name)}/>
        </div>
      <button className="bg-osoc-btn-primary text-white px-3 py-2 mt-6 col-start-2 col-end-4 justify-self-center mb-3" onClick={() => updateEdition(name)}>Open Edition</button>
    </section>
  )
}

export default EditionCard;