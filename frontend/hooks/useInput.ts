import { ChangeEvent, useState } from 'react';

const useInput = (initValue: string) => {
  const [value, setValue] = useState(initValue);

  const reset = () => setValue(initValue);

  const attributes = {
    value,
    onChange: (e: ChangeEvent<HTMLInputElement>) => setValue(e.target.value),
  };

  return [value, reset, attributes] as const;
};

export default useInput;
