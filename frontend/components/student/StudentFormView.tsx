import {Answer} from '../../lib/types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";


const collegeOrUniversityKey = "3yJDjW";
const studiesKey = "wLP0v2";
const degreeTypeKey = "319EDL";
const degreeYearKey = "wg94YK";
const linkCvKey = "w7NZ1z";
const uploadCvKey = "m6ZxA5";
const linkPortfolioKey = "wAB8AN";
const uploadPortfolioKey = "wbWOKE";
const livesInBelgiumKey = "mO70dA";
const ableToWork128HoursKey = "mVz8vl";
const canWorkDuringJulyKey = "nPz0v0";
const hasParticipatedBeforeKey = "wz7eGE";
const wantsToBeStudentCoachKey = "w5Z2eb";
const preferredLanguageKey = "wQ70vk";
const englishLevelKey = "meaEKo";
const emailKey = "wa2GKy";
const phoneNumberKey = "nW80DQ";
const favoredRoleKey = "3X4q1V";


type StudentFormViewProp = {
    answers: Answer[];
};

const StudentFormView: React.FC<StudentFormViewProp> = ({answers}: StudentFormViewProp) => {
    const getAnswerByKey = (key: string) => answers.find(a => a.key == `question_${key}`)?.answer[0];
    const answerIsYes = (key: string) => getAnswerByKey(key)?.toLowerCase().startsWith("yes") || false;
    const canParticipate = [livesInBelgiumKey, ableToWork128HoursKey, canWorkDuringJulyKey].map(answerIsYes).every(Boolean)
    const list: [boolean, string][] = [
        [canParticipate, "Can work during the month of July, Monday through Tuesday"],
        [answerIsYes(hasParticipatedBeforeKey), "Has participated before"],
        [answerIsYes(wantsToBeStudentCoachKey), "Would like to be a student coach"]
    ]
    return (
        <div>
            <h3 className="text-2xl pt-8">Academia</h3>
            <ul className="list-inside list-disc">
                <li>Enrolled at <b>{getAnswerByKey(collegeOrUniversityKey)}</b></li>
                <li>Studies: <b>{getAnswerByKey(studiesKey)}</b></li>
                <li>Type of degree: <b>{getAnswerByKey(degreeTypeKey)}</b></li>
                <li>Year into degree: <b>{getAnswerByKey(degreeYearKey)}</b></li>
            </ul>

            <h3 className="text-2xl pt-12">Experience</h3>
            <ul className="list-inside list-disc">
                <li><a href={getAnswerByKey(linkCvKey) || getAnswerByKey(uploadCvKey)}
                       className="font-bold underline">CV</a></li>
                <li><a href={getAnswerByKey(linkPortfolioKey) || getAnswerByKey(uploadPortfolioKey)}
                       className="font-bold underline">Portfolio</a></li>
            </ul>

            <h3 className="text-2xl pt-12">Practical</h3>
            <h4 className="pt-4">The student:</h4>
            {list.map(([isCheckmark, label]) =>
                <CheckmarkList isCheckmark={isCheckmark} label={label}/>
            )}
            <h4 className="pt-4">Applying for: <b>{getAnswerByKey(favoredRoleKey)}</b></h4>
            <h4 className="pt-4">Languages:</h4>
            <ul className="list-inside list-disc">
                <li>First language: <b>{getAnswerByKey(preferredLanguageKey)}</b></li>
                <li>Level of English: <b>{getAnswerByKey(englishLevelKey)}</b></li>
            </ul>
            <h4 className="pt-4">Contact:</h4>
            <ul className="list-inside list-disc">
                <li>Email address: <b>{getAnswerByKey(emailKey)}</b></li>
                <li>Phone number: <b>{getAnswerByKey(phoneNumberKey)}</b></li>
            </ul>
        </div>
    )
};


const check_mark = <FontAwesomeIcon icon={faCheck} />;
const x_mark = <FontAwesomeIcon icon={faXmark} />;

type CheckmarkListProp = {
    isCheckmark: boolean;
    label: string
};

const CheckmarkList: React.FC<CheckmarkListProp> = ({isCheckmark, label}: CheckmarkListProp) => {
    const symbol = isCheckmark? check_mark : x_mark;
    const color = isCheckmark? "text-check-green" : "text-check-red";
    return (
        <div className="flex flex-row">
            <i className={`${color} w-[30px] px-2`}>{symbol}</i>
            <p>{label}</p>
        </div>
    );
};

export default StudentFormView;