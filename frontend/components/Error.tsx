import { ExclamationCircleIcon } from "@heroicons/react/solid";

type ErrorProps = {
  error: string;
  className?: string;
};


const Error: React.FC<ErrorProps> = ({ error, className }: ErrorProps) => {
  return (
    <div className={`m-auto bg-red-600 opacity-90 border-2 border-gray-200 flex flex-row w-11/12 items-center px-2 py-2 rounded-md ${className}`}>
      <div>
      <ExclamationCircleIcon className="w-6 h-6 self-start mr-1" color="white"/>
      </div>
      <div className="self-center w-full">
      <p className="text-white text-center font-medium">{ error }</p>
      </div>
    </div>
  )
}
export default Error;