import { ExclamationCircleIcon } from '@heroicons/react/solid';

type ErrorProps = {
  error: string;
  className?: string;
};

const Error: React.FC<ErrorProps> = ({ error, className }: ErrorProps) => {
  return (
    <div
      className={`m-auto flex w-11/12 flex-row items-center rounded-md border-2 border-gray-200 bg-red-600 px-2 py-2 opacity-90 ${className}`}
    >
      <div>
        <ExclamationCircleIcon
          className="mr-1 h-6 w-6 self-start"
          color="white"
        />
      </div>
      <div className="w-full self-center">
        <p className="text-center font-medium text-white">{error}</p>
      </div>
    </div>
  );
};
export default Error;
