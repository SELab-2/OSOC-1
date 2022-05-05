import { Toast, useToaster } from "react-hot-toast";

const HotToastNotifications = () => {
    const { toasts, handlers } = useToaster();
    const { startPause, endPause } = handlers;

    return (
      <div onMouseEnter={startPause} onMouseLeave={endPause}/>
      // <div 
      //   style={{
      //     position: "fixed",
      //     top: 10,
      //     left: 10
      //   }}
      //   // onMouseEnter={startPause}
      //   onPlay={startPause}
      //   onMouseLeave={endPause}
      // >
      //   {/* {toasts.map((toast) => {
      //     return (
      //       // <></>
      //       <div
      //         key={toast.id}
      //         style={{
      //           position: "absolute",
      //           width: "13rem",
      //           padding: ".7rem",
      //           background: "rgba(175, 75, 62, 0.1)",
      //           borderRadius: "3rem",
      //           transition: "all 0.2s",
      //           opacity: toast.visible ? 1 : 0
      //         }}
      //       >
      //         Dit is hot
      //         {/* {toast.message} */}
      //       </div>
      //     );
      //   })} */}
      // </div>
    );
   };

export default HotToastNotifications;
   