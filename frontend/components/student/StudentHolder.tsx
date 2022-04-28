import { useState } from 'react';
import { ItemTypes, Student, StudentBase } from '../../lib/types';
import { useDrop } from 'react-dnd';
import StudentView from './StudentView';

type StudentHolderProp = {
  setRefresh: (refresh: [boolean, boolean]) => void;
};

// TODO no actual functionality present yet
const StudentHolder: React.FC<StudentHolderProp> = ({
  setRefresh,
}: StudentHolderProp) => {
  const [studentBase, setStudentBase] = useState({} as StudentBase);
  /**
   * This catches the dropped studentTile
   * The studentTile passes its student as the DragObject to this function on drop
   * Then we allow the suggester to choose a position & reason, then assign student to project
   * Dropping is only allowed if the student is not yet assigned to this project
   */
  const [, drop] = useDrop(
    () => ({
      accept: ItemTypes.STUDENTTILE,
      canDrop: (item) => {
        return (
          studentBase.id === undefined || (item as Student).id != studentBase.id
        );
      },
      drop: (item) => {
        setStudentBase(item as StudentBase);
      },
      collect: (monitor) => ({
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop(),
      }),
    }),
    [studentBase]
  );

  return (
    <section className={`min-h-[80vh]`} ref={drop}>
      {/* hold the student information, only load this if an actual studentBase object exists */}
      {/* studentBase.id is used to check if this is an actual object or just an empty dummy */}
      {studentBase.id && (
        <StudentView studentInput={studentBase} setRefresh={setRefresh} />
      )}
    </section>
  );
};

export default StudentHolder;
