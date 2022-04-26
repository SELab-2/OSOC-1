import {axiosAuthenticated} from './axios';
import {Skill, Url, User, UserRole} from './types';
import Endpoints from './endpoints';
import axios from 'axios';

/**
 * Function to get all skills in dropdown options format
 * this function will wait to return until the request is done
 *
 * @param setSkillOptions - setter for the resulting options list
 * @param signal
 */
export async function getSkills(
  setSkillOptions: (
    skillOptions: Array<{ value: string; label: string }>
  ) => void, signal: AbortSignal
) {
  await axiosAuthenticated
    .get<Skill[]>(Endpoints.SKILLS, {signal: signal})
    .then((response) =>
      setSkillOptions(
        response.data.map((skill) => {
          return { value: skill.skillName, label: skill.skillName };
        })
      )
    )
    .catch((err) => console.log(err));
}

/**
 * Function to get all non-disabled users in a dropdown options format
 * This function will wait to return until the request is done
 *
 * @param setCoachOptions - setter for the resulting options list
 * @param signal
 */
export async function getCoaches(
  setCoachOptions: (CoachOptions: Array<{ value: User; label: string }>) => void, signal: AbortSignal
) {
  await axiosAuthenticated
    .get<User[]>(Endpoints.USERS, {signal: signal})
    .then((response) =>
      setCoachOptions(
        response.data.filter(user => user.role != UserRole.Disabled).map((coach) => {
          return { value: coach, label: coach.username };
        })
      )
    )
    .catch((err) => console.log(err));
}

/**
 * Function that will get all urls given and push them into the resultList
 *
 * @param urls - list of urls to get
 * @param resultList - list to push results unto
 * @param signal
 */
export async function getUrlList<Type>(urls: Url[], resultList: Type[], signal: AbortSignal) {
  await axios
    .all(urls.map((url) => axiosAuthenticated.get<Type>(url, {signal: signal})))
    .then((response) => {
        if (signal.aborted){
            return;
        }
      response.forEach((resp) => resultList.push(resp.data));
    })
    .catch((ex) => {
      console.log(ex);
    });
}

/**
 * Function that will get all urls given and set them into the resultMap
 * This will map each url onto the object from the request
 *
 * @param urls - list of urls to get
 * @param resultMap - map to set the results in
 */
export async function getUrlDict<Type>(urls: Url[], resultMap: Map<Url, Type>, signal: AbortSignal) {
  await axios
    .all(urls.map((url) => axiosAuthenticated.get<Type>(url, {signal: signal})))
    .then((response) => {
        if (signal.aborted){
            return;
        }
      response.forEach((resp) =>
        resultMap.set(resp.config.url as string, resp.data)
      );
    })
    .catch((ex) => {
      console.log('getUrlDict', ex);
    });
}
