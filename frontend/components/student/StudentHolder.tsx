import { ItemTypes, Student, StudentBase } from '../../lib/types';
import { useDrop } from 'react-dnd';
import StudentView from './StudentView';

type StudentHolderProp = {
  studentBase: StudentBase;
  setStudentBase: (studentBase: StudentBase) => void;
};

const StudentHolder: React.FC<StudentHolderProp> = ({
  studentBase,
  setStudentBase,
}: StudentHolderProp) => {
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
        <StudentView
          studentInput={studentBase}
          setOriginalStudentBase={setStudentBase}
        />
      )}
    </section>
  );
};

export default StudentHolder;
