import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faCheck,
  faQuestion,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
const check_mark = <FontAwesomeIcon icon={faCheck} />;
const question_mark = <FontAwesomeIcon icon={faQuestion} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;
import { Fragment } from 'react';
import { Menu, Transition } from '@headlessui/react';
import { ChevronDownIcon } from '@heroicons/react/solid';
import { StatusSuggestion, Student } from '../../lib/types';

type StudentProp = {
  student: Student;
};

type StatusSuggestionProp = {
  statusSuggestion: StatusSuggestion;
};

// TODO no actual functionality present yet
const StudentView: React.FC<StudentProp> = ({ student }: StudentProp) => {
  return (
    <section className={`flex flex-col-reverse justify-between xl:flex-row`}>
      {/* hold the student information */}
      <div className="mx-8 flex flex-col bg-osoc-neutral-bg">
        <div>
          <h4 className="font-bold">
            {student.firstName + ' ' + student.lastName}
          </h4>
        </div>
        <div className="flex flex-col">
          <h5>Suggestions</h5>
          {student.statusSuggestions.map((statusSuggestion) => (
            <StudentStatusSuggestion
              key={statusSuggestion.coachId}
              statusSuggestion={statusSuggestion}
            />
          ))}
        </div>
      </div>

      {/* holds suggestion controls */}
      <div className={`mr-6 ml-6 mb-6 flex flex-col xl:mb-0 xl:ml-0`}>
        {/* regular coach status suggestion form */}
        <form className={`border-2 p-2`}>
          <div className={`flex w-[380px] flex-row justify-between`}>
            <button
              className={`w-[30%] bg-check-green py-[2px] text-sm text-white shadow-md shadow-gray-400`}
            >
              Suggest Yes
            </button>
            <button
              className={`w-[30%] bg-check-orange py-[2px] text-sm text-white shadow-md shadow-gray-400`}
            >
              Suggest Maybe
            </button>
            <button
              className={`w-[30%] bg-check-red py-[2px] text-sm text-white shadow-md shadow-gray-400`}
            >
              Suggest No
            </button>
          </div>
          <textarea
            placeholder="Motivation"
            className="mt-3 w-full resize-y border-2 border-check-gray"
          />
        </form>

        {/* TODO this should only be visible to admin role */}
        {/* admin status selection form */}
        <form className={`mt-10 flex flex-row justify-between border-2 p-2`}>
          <Menu as="div" className="relative inline-block text-left">
            <div>
              <Menu.Button className="inline-flex w-full justify-center border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50">
                Make permanent selection
                <ChevronDownIcon
                  className="-mr-1 ml-2 h-5 w-5"
                  aria-hidden="true"
                />
              </Menu.Button>
            </div>

            <Transition
              as={Fragment}
              enter="transition ease-out duration-100"
              enterFrom="transform opacity-0 scale-95"
              enterTo="transform opacity-100 scale-100"
              leave="transition ease-in duration-75"
              leaveFrom="transform opacity-100 scale-100"
              leaveTo="transform opacity-0 scale-95"
            >
              {/* These are the actual dropdown options */}
              <Menu.Items className="absolute right-0 w-full origin-top-right bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                <div className="">
                  <Menu.Item>
                    {({ active }) => (
                      <p
                        className={`${
                          active ? 'bg-gray-100 text-gray-900' : 'text-gray-700'
                        } block px-4 py-2 text-sm`}
                      >
                        opt1
                      </p>
                    )}
                  </Menu.Item>
                  <Menu.Item>
                    {({ active }) => (
                      <p
                        className={`${
                          active ? 'bg-gray-100 text-gray-900' : 'text-gray-700'
                        } block px-4 py-2 text-sm`}
                      >
                        opt2
                      </p>
                    )}
                  </Menu.Item>
                  <Menu.Item>
                    {({ active }) => (
                      <p
                        className={`${
                          active ? 'bg-gray-100 text-gray-900' : 'text-gray-700'
                        } block px-4 py-2 text-sm`}
                      >
                        opt3
                      </p>
                    )}
                  </Menu.Item>
                  {/* Don't know what type this dropdown menu will end up being yet */}
                  {/*<form method="POST" action="#">*/}
                  {/*    <Menu.Item>*/}
                  {/*        {({ active }) => (*/}
                  {/*            <button*/}
                  {/*                type="submit"*/}
                  {/*                className={`${*/}
                  {/*                    active ? 'bg-gray-100 text-gray-900' : 'text-gray-700'}*/}
                  {/*                block px-4 py-2 text-sm`}*/}
                  {/*            >*/}
                  {/*                Sign out*/}
                  {/*            </button>*/}
                  {/*        )}*/}
                  {/*    </Menu.Item>*/}
                  {/*</form>*/}
                </div>
              </Menu.Items>
            </Transition>
          </Menu>
          {/* button to submit the admin status choice */}
          <button
            className={`bg-check-gray px-2 py-[2px] text-sm shadow-md shadow-gray-400`}
          >
            Submit
          </button>
        </form>
      </div>
    </section>
  );
};

export default StudentView;

// TODO This should get the coach name somehow
const StudentStatusSuggestion: React.FC<StatusSuggestionProp> = ({
  statusSuggestion,
}: StatusSuggestionProp) => {
  let myLabel = question_mark;
  let myColor = 'text-check-orange';
  if (statusSuggestion.status == 'Yes') {
    myLabel = check_mark;
    myColor = 'text-check-green';
  } else if (statusSuggestion.status == 'No') {
    myLabel = x_mark;
    myColor = 'text-check-red';
  }

  return (
    <div className="flex flex-row">
      <i className={`${myColor} w-[30px] px-2`}>{myLabel}</i>
      <p className="">{statusSuggestion.coachId}</p>
    </div>
  );
};

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
