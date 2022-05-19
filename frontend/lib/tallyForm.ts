import {Answer} from "./types";


/**
 * The values of the keys are equal to the 'key' column of the 'answer' table in the database.
 * These key values are unreliable. When using a different tally form, these values should be updated.
 * TODO: Find another way to identify Tally form answers.
 */
export const keys = {
  collegeOrUniversity: "3yJDjW",
  studies: "wLP0v2",
  degreeType: "319EDL",
  degreeYear: "wg94YK",
  linkCv: "w7NZ1z",
  uploadCv: "m6ZxA5",
  linkPortfolio: "wAB8AN",
  uploadPortfolio: "wbWOKE",
  livesInBelgium: "mO70dA",
  ableToWork128Hours: "mVz8vl",
  canWorkDuringJuly: "nPz0v0",
  hasParticipatedBefore: "wz7eGE",
  wantsToBeStudentCoach: "w5Z2eb",
  preferredLanguage: "wQ70vk",
  englishLevel: "meaEKo",
  email: "wa2GKy",
  phoneNumber: "nW80DQ",
  favoredRole: "3X4q1V",
  otherFavoredRole: "w8Ze6o",
  bestSkill: "n0ePZQ",
  linkMotivation: "wkNZKj",
  uploadMotivation: "mBxBAY",
  writeMotivation: "wvP2E8",
  commonPronouns: "3N70Mb",
  otherPronouns: "3qRPok"
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