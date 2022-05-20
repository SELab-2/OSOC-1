import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons';
import { answerIsYes, keys } from '../../lib/tallyForm';
import Link from 'next/link';

type StudentFormViewProp = {
  answers: typeof keys;
};

/**
 * Displays the answers the student gave in the tally form in a condensed fashion.
 */
const StudentFormView: React.FC<StudentFormViewProp> = ({
  answers,
}: StudentFormViewProp) => {
  const canParticipate = [
    answers.livesInBelgium,
    answers.ableToWork128Hours,
    answers.canWorkDuringJuly,
  ]
    .map(answerIsYes)
    .every(Boolean);
  const practicalBulletPoints: [boolean, string][] = [
    [
      canParticipate,
      'Can work during the month of July, Monday through Tuesday',
    ],
    [answerIsYes(answers.hasParticipatedBefore), 'Has participated before'],
    [
      answerIsYes(answers.wantsToBeStudentCoach),
      'Would like to be a student coach',
    ],
  ];
  const getMotivation = () => {
    const link = answers.linkMotivation || answers.uploadMotivation;
    if (link) {
      return (
        <Link href={link}>
          <span className="font-medium underline hover:cursor-pointer">
            link
          </span>
        </Link>
      );
    }
    return <p>{answers.writeMotivation}</p>;
  };
  return (
    <div>
      <h3 className="pt-8 text-2xl">Academia</h3>
      <ul className="list-inside list-disc">
        <li>
          Enrolled at{' '}
          <span className="font-semibold">{answers.collegeOrUniversity}</span>
        </li>
        <li>
          Studies: <span className="font-semibold">{answers.studies}</span>
        </li>
        <li>
          Type of degree:{' '}
          <span className="font-semibold">
            {answers.otherDegreeType || answers.degreeType}
          </span>
        </li>
        <li>
          Year into degree:{' '}
          <span className="font-semibold">{answers.degreeYear}</span>
        </li>
      </ul>

      <h3 className="pt-12 text-2xl">Experience</h3>
      <ul className="list-inside list-disc">
        <li>
          Best skill: <b>{answers.bestSkill}</b>
        </li>
        <li>
          <Link href={answers.linkCv || answers.uploadCv}>
            <span className="font-medium underline hover:cursor-pointer">
              CV
            </span>
          </Link>
        </li>
        <li>
          <Link href={answers.linkPortfolio || answers.uploadPortfolio}>
            <span className="font-medium underline hover:cursor-pointer">
              Portfolio
            </span>
          </Link>
        </li>
      </ul>

      <h3 className="pt-12 text-2xl">Practical</h3>
      <h4 className="pt-4">The student:</h4>
      {practicalBulletPoints.map(([isCheckmark, label]) => (
        <CheckmarkList isCheckmark={isCheckmark} label={label} key={label} />
      ))}
      {answers.otherResponsibilities && (
        <p>
          The student could be hindered by these other responsibilities:
          <span className="font-semibold">{answers.otherResponsibilities}</span>
        </p>
      )}
      <h4 className="pt-4">
        Applying for:{' '}
        <span className="font-semibold">
          {answers.favoredRole || answers.otherFavoredRole}
        </span>
      </h4>
      <h4 className="pt-4">Languages:</h4>
      <ul className="list-inside list-disc">
        <li>
          First language:{' '}
          <span className="font-semibold">{answers.preferredLanguage}</span>
        </li>
        <li>
          Level of English:{' '}
          <span className="font-semibold">{answers.englishLevel}</span>
        </li>
      </ul>
      <h4 className="pt-4">Contact:</h4>
      <ul className="list-inside list-disc">
        <li>
          Email address: <span className="font-semibold">{answers.email}</span>
        </li>
        <li>
          Phone number:{' '}
          <span className="font-semibold">{answers.phoneNumber}</span>
        </li>
      </ul>

      <h3 className="pt-12 text-2xl">Motivation</h3>
      {getMotivation()}
    </div>
  );
};

const check_mark = <FontAwesomeIcon icon={faCheck} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;

type CheckmarkListProp = {
  isCheckmark: boolean;
  label: string;
};

/**
 * Displays either a checkmark or an x mark depending on the value of isCheckmark.
 * Next to this symbol, the given label is rendered as a paragraph.
 */
const CheckmarkList: React.FC<CheckmarkListProp> = ({
  isCheckmark,
  label,
}: CheckmarkListProp) => {
  const symbol = isCheckmark ? check_mark : x_mark;
  const color = isCheckmark ? 'text-check-green' : 'text-check-red';
  return (
    <div className="flex flex-row items-center justify-start">
      <i className={`${color} w-[30px] px-2`}>{symbol}</i>
      <p>{label}</p>
    </div>
  );
};

export default StudentFormView;
