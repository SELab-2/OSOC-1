import { useEffect, useState } from "react";

const useLocalStorage = <T>(key: string, initValue: T) => {
  let useVal;
  //SSR Next.js
  if (typeof window === 'undefined') {
    useVal = initValue;
  } else {
    const localVal = localStorage.getItem(key);
    useVal = localVal ? JSON.parse(localVal) : initValue;
  }

  const [value, setValue] = useState(useVal);

  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(value));
  }, [value]);

  return [value, setValue] as const;
}

export default useLocalStorage;