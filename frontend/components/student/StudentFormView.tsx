import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";
import {answerIsYes, keys} from "../../lib/tallyForm";


type StudentFormViewProp = {
  answers: typeof keys
};

/**
 * Displays the answers the student gave in the tally form in a condensed fashion.
 */
const StudentFormView: React.FC<StudentFormViewProp> = ({answers}: StudentFormViewProp) => {
  const canParticipate =
    [answers.livesInBelgium, answers.ableToWork128Hours, answers.canWorkDuringJuly]
      .map(answerIsYes).every(Boolean)
  const practicalBulletPoints: [boolean, string][] = [
    [canParticipate, "Can work during the month of July, Monday through Tuesday"],
    [answerIsYes(answers.hasParticipatedBefore), "Has participated before"],
    [answerIsYes(answers.wantsToBeStudentCoach), "Would like to be a student coach"]
  ]
  const getMotivation = () => {
    const link = answers.linkMotivation || answers.uploadMotivation
    if (link) {
      return <a href={link} className="font-bold underline">link</a>
    }
    return <p>{answers.writeMotivation}</p>
  }
  return (
    <div>
      <h3 className="text-2xl pt-8">Academia</h3>
      <ul className="list-inside list-disc">
        <li>Enrolled at <b>{answers.collegeOrUniversity}</b></li>
        <li>Studies: <b>{answers.studies}</b></li>
        <li>Type of degree: <b>{answers.degreeType}</b></li>
        <li>Year into degree: <b>{answers.degreeYear}</b></li>
      </ul>

      <h3 className="text-2xl pt-12">Experience</h3>
      <ul className="list-inside list-disc">
        <li>Best skill: <b>{answers.bestSkill}</b></li>
        <li><a href={answers.linkCv || answers.uploadCv}
               className="font-bold underline">CV</a></li>
        <li><a href={answers.linkPortfolio || answers.uploadPortfolio}
               className="font-bold underline">Portfolio</a></li>
      </ul>

      <h3 className="text-2xl pt-12">Practical</h3>
      <h4 className="pt-4">The student:</h4>
      {practicalBulletPoints.map(([isCheckmark, label]) =>
        <CheckmarkList isCheckmark={isCheckmark} label={label}/>
      )}
      {answers.otherResponsibilities &&
          <p>The student could be hindered by these other responsibilities:
              <b>{answers.otherResponsibilities}</b></p>}
      <h4 className="pt-4">Applying for: <b>{answers.favoredRole || answers.otherFavoredRole}</b></h4>
      <h4 className="pt-4">Languages:</h4>
      <ul className="list-inside list-disc">
        <li>First language: <b>{answers.preferredLanguage}</b></li>
        <li>Level of English: <b>{answers.englishLevel}</b></li>
      </ul>
      <h4 className="pt-4">Contact:</h4>
      <ul className="list-inside list-disc">
        <li>Email address: <b>{answers.email}</b></li>
        <li>Phone number: <b>{answers.phoneNumber}</b></li>
      </ul>

      <h3 className="text-2xl pt-12">Motivation</h3>
      {getMotivation()}
    </div>
  )
};


const check_mark = <FontAwesomeIcon icon={faCheck}/>;
const x_mark = <FontAwesomeIcon icon={faXmark}/>;

type CheckmarkListProp = {
  isCheckmark: boolean;
  label: string
};

/**
 * Displays either a checkmark or an x mark depending on the value of isCheckmark.
 * Next to this symbol, the given label is rendered as a paragraph.
 */
const CheckmarkList: React.FC<CheckmarkListProp> = ({isCheckmark, label}: CheckmarkListProp) => {
  const symbol = isCheckmark ? check_mark : x_mark;
  const color = isCheckmark ? "text-check-green" : "text-check-red";
  return (
    <div className="flex flex-row">
      <i className={`${color} w-[30px] px-2`}>{symbol}</i>
      <p>{label}</p>
    </div>
  );
};

export default StudentFormView;