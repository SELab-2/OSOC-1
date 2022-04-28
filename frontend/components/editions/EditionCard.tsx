import { TrashIcon } from '@heroicons/react/solid';
import { Edition } from '../../lib/types';

type EditionCardProps = {
  /**
   * The edition that this card represents
   */
  edition: Edition;

  /**
   * Setter to update the current viewed edition to this edition
   */
  updateEdition: (edition: string) => void;

  /**
   * Delete this edition
   */
  deleteEdition: (edition: string) => void;
};

/**
 * A card displaying information about an edition (currently only the name and whether it's active or not).
 * This card also adds interactions to set this edition as the currently viewed edition and to delete this edition.
 *
 * @param EditionCardProps - {@link EditionCardProps}
 * @returns Edition Card representing the given edition
 */
const EditionCard: React.FC<EditionCardProps> = ({
  edition: { name, isActive },
  updateEdition,
  deleteEdition,
}: EditionCardProps) => {
  return (
    <section className="m-auto grid max-w-sm grid-cols-4 overflow-hidden rounded-lg border-2">
      {isActive && (
        <p className="absolute translate-x-2 translate-y-2 rounded-md bg-osoc-green px-2 text-white">
          Active
        </p>
      )}
      <img className="col-span-4 select-none" src="../../img/mountains.jpg" />
      <div className="col-start-1 col-end-4 ml-2 mt-2 justify-self-start">
        <p className="text-lg font-bold">{name}</p>
      </div>
      <div className="col-start-4 col-end-5 mr-2 mt-2 justify-self-end">
        <TrashIcon
          className="h-6 w-6 hover:cursor-pointer"
          color="#F14A3B"
          onClick={() => deleteEdition(name)}
        />
      </div>
      <button
        className="col-start-2 col-end-4 mt-6 mb-3 justify-self-center bg-osoc-btn-primary px-3 py-2 text-white"
        onClick={() => updateEdition(name)}
      >
        Open Edition
      </button>
    </section>
  );
};

export default EditionCard;
