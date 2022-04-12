import { useEffect, useState } from "react";

const useLocalStorage = (key: string, initValue: unknown) => {
  const localVal = localStorage.getItem(key);
  const useVal = localVal ? JSON.parse(localVal) : initValue;
  const [value, setValue] = useState(useVal);

  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(value));
  }, [value]);

  return [value, setValue] as const;
}

export default useLocalStorage;