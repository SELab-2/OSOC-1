import { InformationCircleIcon } from '@heroicons/react/solid';

type InfoProps = {
  info: string;
  className?: string;
};

const Info: React.FC<InfoProps> = ({ info, className }: InfoProps) => {
  return (
    <div
      className={`m-auto flex w-11/12 flex-row items-center rounded-md border-2 border-green-300 bg-osoc-green px-2 py-2 opacity-90 ${className}`}
    >
      <div>
        <InformationCircleIcon
          className="mr-1 h-6 w-6 self-start"
          color="white"
        />
      </div>
      <div className="w-full self-center">
        <p className="text-center font-medium text-white">{info}</p>
      </div>
    </div>
  );
};
export default Info;
