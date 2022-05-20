import {Answer} from "./types";


/**
 * The values of the keys are equal to the 'key' column of the 'answer' table in the database.
 * These key values are unreliable. When using a different tally form, these values should be updated.
 * TODO: Find another way to identify Tally form answers.
 */
export const keys = {
  collegeOrUniversity: "3yJDjW",  // What is the name of your college or university?
  studies: "wLP0v2",  // What do/did you study?
  degreeType: "319EDL",  // What kind of diploma are you currently going for?
  otherDegreeType: "wME5v0", // if the student answers 'Other' to the question above.
  degreeYear: "wg94YK",  // Which year of your degree are you in?
  linkCv: "w7NZ1z",  // Or link to your CV
  uploadCv: "m6ZxA5",  // Upload your CV – size limit 10MB
  linkPortfolio: "wAB8AN",  // Or link to your portfolio / GitHub
  uploadPortfolio: "wbWOKE",  // Upload your portfolio – size limit 10MB
  livesInBelgium: "mO70dA",  // Will you live in Belgium in July 2022?
  ableToWork128Hours: "mVz8vl",  // Are you able to work 128 hours with a student employment agreement, or as a volunteer?
  canWorkDuringJuly: "nPz0v0",  // Can you work during the month of July, Monday through Thursday (~09:00 to 17:00)?
  hasParticipatedBefore: "wz7eGE",  // Have you participated in osoc before?
  wantsToBeStudentCoach: "w5Z2eb",  // Would you like to be a student coach this year?
  preferredLanguage: "wQ70vk",  // What language are you most fluent in?
  englishLevel: "meaEKo",  // How would you rate your English?
  email: "wa2GKy",  // Your email address
  phoneNumber: "nW80DQ",  // Phone number
  favoredRole: "3X4q1V",  // Which role are you applying for?
  otherFavoredRole: "w8Ze6o",  // Which role are you applying for that is not in the list above?
  bestSkill: "n0ePZQ",  // Which skill would you list as your best one?
  linkMotivation: "wkNZKj",  // Or link to your motivation
  uploadMotivation: "mBxBAY",  // Upload your motivation – size limit 10MB
  writeMotivation: "wvP2E8",  // Or write about your motivation
  commonPronouns: "3N70Mb",  // Which pronouns do you prefer?
  otherPronouns: "3qRPok",  // Enter your pronouns (for uncommon pronouns)
  otherResponsibilities: "3Ex0vL",  // Are there any responsibilities you might have which could hinder you during the day?
  preferredName: "w2Kr1b"  // How would you like to be called? (Only contains an answer when
                           // "Would you like to be called by a different name than your birth name?" was answered with yes.)
}


/**
 * Returns an object of the same type as 'keys', with the values replaced
 * by the actual answer strings stored in the given answers array.
 */
export function getAnswerStrings(answers: Answer[]): typeof keys {
  const getAnswerByKey = (key: string) => answers.find(a => a.key == `question_${key}`)?.answer[0] || "";
  return Object.assign({}, ...Object.keys(keys).map(k => ({[k]: getAnswerByKey(keys[k as keyof typeof keys])})));
}


export function answerIsYes(answer: string): boolean {
  return answer.toLowerCase().startsWith("yes");
}