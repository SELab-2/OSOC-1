import { useEffect, useRef, useState } from 'react';
import useOnScreen from '../hooks/useOnScreen';
import { SpinnerCircular } from 'spinners-react';

type InfiniteListProps<Type> = {
  list: Type[];
  renderItem: (student: Type) => JSX.Element;
  loadingText: string;
  hasMoreItems: boolean;
  loading: boolean;
  loadMoreItems: () => void;
};

// I don't know how to fix these any types to follow the template type
/* eslint-disable  @typescript-eslint/no-explicit-any */
const InfiniteList: React.FC<InfiniteListProps<any>> = ({
  list,
  renderItem,
  loadingText,
  hasMoreItems,
  loadMoreItems,
  loading,
}: InfiniteListProps<any>) => {
  const elementRef = useRef<HTMLDivElement>(null);
  const isOnScreen = useOnScreen(elementRef);
  const [fetching, setFetching] = useState(false);

  /**
   * This checks if we need to load more items
   * To avoid accidental multi-loads, we wait 1.5 seconds between loads
   */
  useEffect(() => {
    if ({ isOnScreen }.isOnScreen && !fetching && hasMoreItems && !loading) {
      setFetching(true);
      (async () => {
        loadMoreItems();
        await new Promise((f) => setTimeout(f, 1500));
        setFetching(false);
      })();
    }
  }, [isOnScreen, { isOnScreen }.isOnScreen, fetching, hasMoreItems, loading]);

  return (
    <div>
      {list.map((item) => renderItem(item))}
      <div
        ref={elementRef}
        className={`${hasMoreItems ? 'visible block' : 'hidden'} text-center`}
      >
        <p>{loadingText}</p>
        <SpinnerCircular
          size={30}
          thickness={100}
          color="#FCB70F"
          secondaryColor="rgba(252, 183, 15, 0.4)"
          className="mx-auto"
        />
      </div>
    </div>
  );
};

export default InfiniteList;
