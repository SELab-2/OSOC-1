export type Student = {
    id: string;
    firstName: string;
    lastName: string;
    status: string;
    statusSuggestions: StatusSuggestion[];
    alumn: boolean;
    tallyForm: TallyForm;
};

type StatusSuggestion = {
    coachId: string;
    status: string;
    motivation: string;
};

// this is bad mkay
type TallyForm = {
    livingBelgium: boolean,
    workTime: number, // 1-4 cba this
    workJuly: boolean,
    responsibilities: string,
    birthName: string,
    lastName: string,
    // different name whatever
    gender: string,
    // pronouns stuff
    language: string,
    englishLevel: number, // 1-5
    phoneNumber: string,
    email: string,
    // Skipping cv, portfolio, motivation
    studies: string[],
    diploma: string,
    diplomaYears: string,
    diplomaYear: string,
    school: string,
    role: string[],
    bestSkill: string,
    participated: boolean,
    studentCoach: boolean
}

type StudentProp = {
    student: Student;
};

const StudentView: React.FC<StudentProp> = ({ student }: StudentProp) => {
    return (
        <div className="flex flex-col mx-8 bg-osoc-neutral-bg">
            <div>
                <p className="font-bold">{student.tallyForm.birthName + " " + student.tallyForm.lastName}</p>
            </div>

        </div>
    );
};

export default StudentView;



/*
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
