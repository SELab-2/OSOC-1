import { ChangeEvent } from 'react';
import useLocalStorage from './useLocalStorage';

const usePersistentInput = (key: string, initValue: string) => {
  const [value, setValue] = useLocalStorage(key, initValue);

  const reset = () => setValue(initValue);

  const attributes = {
    value,
    onChange: (e: ChangeEvent<HTMLInputElement>) => setValue(e.target.value),
  };

  return [value, reset, attributes] as const;
};

export default usePersistentInput;
