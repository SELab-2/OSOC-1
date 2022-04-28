import {PropsWithChildren, useEffect, useState} from 'react';
import {ItemTypes, Student, StudentBase} from '../../lib/types';
import {useDrop} from "react-dnd";
import StudentView from "./StudentView";

// type StudentProp = {
//   studentBase: StudentBase;
// };

type StudentHolderProp = PropsWithChildren<unknown>;

// TODO no actual functionality present yet
const StudentHolder: React.FC<StudentHolderProp> = () => {

  const [studentBase, setStudentBase] = useState({} as StudentBase);
  /**
   * This catches the dropped studentTile
   * The studentTile passes its student as the DragObject to this function on drop
   * Then we allow the suggester to choose a position & reason, then assign student to project
   * Dropping is only allowed if the student is not yet assigned to this project
   */
  const [{ isOver, canDrop }, drop] = useDrop(
      () => ({
        accept: ItemTypes.STUDENTTILE,
        canDrop: (item) => {
          return (studentBase.id === undefined) || (item as Student).id != studentBase.id;
          // return true;
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
      {studentBase.id && <StudentView studentInput={studentBase}/>}
    </section>
  );
};

export default StudentHolder;


/*
OSOC tally form for reference remove when layout finished

Practical Questions

Will you live in Belgium in july 2022 yes/no

Are you able to work 128 hours with a student employment agreement, or as a volunteer?
	yes, I can work with a student employment agreement in Belgium
	yes, I can work as a volunteer in Belgium
	No, but I would like to join this experience for free
	No, I won't be able to work as a student, as a volunteer or for free

Can you work during the month of July, Monday through Thursday (~09:00 to 17:00)?
	yes
	no, I wouldn't be able to work for the majority of days

Are there any responsibilities you might have which could hinder you during the day?
	open field question, can be empty



Personal details

Birth name
Last name

Would you like to be called by a different name than your birth name?
	no
	yes -> new field
		How would you like to be called?
			open text field

What is your gender?
	female
	male
	transgender
	rather not say

Would you like to add your pronouns?
	no
	yes -> new question
		Which pronouns do you prefer?
			she/her/hers
			he/him/his
			they/them/their
			ze/hir/hir
			by firstname
			by call name
			other -> new open field question
				Enter your pronouns

What language are you most fluent in?
	Dutch
	English
	French
	German
	Other -> new open field question
		What language are you most fluent in?

How would you rate your English?
	*
	**
	..
	*****

Phone number
	open field

Your email address
	open field



About you

CV upload / link
portfolio upload / link
motivation upload / link

Add a fun fact about yourself
	open text field



Studies & Experience

What do/did you study? (max 2)
	Backend development
	Business management
	Communication Sciences
	Computer Sciences
	Design
	Frontend development
	Marketing
	Photography
	Videography
	Other -> new open text field

What kind of diploma are you currently going for? (one max)
	A professional Bachelor
	An academic Bachelor
	An associate degree
	A master's degree
	Doctoral degree
	No diploma, I am self taught
	Other -> new open text field

How many years does your degree take?µ
	open text field

Which year of your degree are you in?

What is the name of your college or university?

Which role are you applying for? (max 2)
	Front-end developer
	Back-end developer
	UX / UI designer
	Graphic designer
	Business Modeller
	Storyteller
	Marketer
	Copywriter
	Video editor
	Photographer
	Other -> new open text field

Which skill would you list as your best one?
	open text field

Have you participated in osoc before?
	no
	yes -> Would you like to be a student coach this year?
		yes / no
 */
