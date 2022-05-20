import { useEffect, useState, useRef, RefObject } from 'react';

/**
 * Custom react hook to check if an HTMLElement is visible
 * taken from https://stackoverflow.com/a/67826055/15516306
 * @param ref - the ref of the HTMLElement
 */
export default function useOnScreen(ref: RefObject<HTMLElement>) {
  const observerRef = useRef<IntersectionObserver | null>(null);
  const [isOnScreen, setIsOnScreen] = useState(false);

  useEffect(() => {
    observerRef.current = new IntersectionObserver(([entry]) =>
      setIsOnScreen(entry.isIntersecting)
    );
  }, []);

  useEffect(() => {
    if (observerRef.current && ref.current) {
      observerRef.current.observe(ref.current);

      return () => {
        observerRef.current?.disconnect();
      };
    }
  }, [ref, ref.current]);

  return isOnScreen;
}
