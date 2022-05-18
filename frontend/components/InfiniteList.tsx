import {useEffect, useRef, useState} from "react";
import useOnScreen from "../hooks/useOnScreen";
import {SpinnerCircular} from "spinners-react";

type InfiniteListProps<Type> = {
    list: Type[];
    renderItem: (student: Type) => JSX.Element;
    renderWhenEmpty: string;
    hasMoreItems: boolean;
    loading: boolean;
    loadMoreItems: () => void;
}

const InfiniteList: React.FC<InfiniteListProps<any>> = ({
    list,
    renderItem, renderWhenEmpty, hasMoreItems, loadMoreItems, loading
}: InfiniteListProps<any>) => {
    const elementRef = useRef<HTMLDivElement>(null);
    const isOnScreen = useOnScreen(elementRef);
    const [fetching, setFetching] = useState(false);

    useEffect(() => {
        console.log(' ' + { isOnScreen }.isOnScreen + ' ' + !fetching + ' '  + hasMoreItems + ' ' + !loading)
        if ({ isOnScreen }.isOnScreen && !fetching && hasMoreItems && !loading){
            setFetching(true);
            (async () => {
                console.log("calling load more items");
                loadMoreItems();
                await new Promise(f => setTimeout(f, 1500));
                setFetching(false);
            })();

        }
    }, [isOnScreen, { isOnScreen }.isOnScreen, fetching, hasMoreItems, loading])

    return (
        <div>
            {list.map((item) => renderItem(item))}
            {/*{hasMoreItems && <div ref={elementRef}>Loading more items</div>}*/}
            <div
                ref={elementRef}
                className={`${
                    hasMoreItems ? 'visible block' : 'hidden'
                } text-center`}
            >
                <p>{renderWhenEmpty}</p>
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
}

export default InfiniteList;