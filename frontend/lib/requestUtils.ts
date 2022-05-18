import { axiosAuthenticated } from './axios';
import { Skill, Url, User, UserRole } from './types';
import Endpoints from './endpoints';
import axios, { AxiosError } from 'axios';
import { NextRouter } from 'next/dist/client/router';

/**
 * Function to parse axios request errors
 * @param error - The error thrown
 * @param setError - Callback to set error message
 * @param signal - AbortSignal for the original request
 * @param router - Router object needed for error handling on 418 response
 */
export function parseError(
  error: unknown,
  setError: (error: string) => void,
  router: NextRouter,
  signal?: AbortSignal
) {
  if (signal?.aborted) {
    return;
  }
  if (axios.isAxiosError(error)) {
    const _error = error as AxiosError;
    if (_error.response?.status === 418) {
      router.push('/login');
      return;
    }
    setError(_error.response?.statusText || 'An unknown error occurred');
  } else {
    setError('An unknown error occurred');
  }
}

/**
 * Function to get all skills in dropdown options format
 * this function will wait to return until the request is done
 *
 * @param setSkillOptions - setter for the resulting options list
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 418 response
 */
export async function getSkills(
  setSkillOptions: (
    skillOptions: Array<{ value: string; label: string }>
  ) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  await axiosAuthenticated
    .get<Skill[]>(Endpoints.SKILLS, { signal: signal })
    .then((response) =>
      setSkillOptions(
        response.data.map((skill) => {
          return { value: '', label: skill.skillName };
        })
      )
    )
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function to get all non-disabled users in a dropdown options format
 * This function will wait to return until the request is done
 *
 * @param setCoachOptions - Setter for the resulting options list
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 418 response
 */
export async function getCoaches(
  setCoachOptions: (
    CoachOptions: Array<{ value: User; label: string }>
  ) => void,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  await axiosAuthenticated
    .get<User[]>(Endpoints.USERS, { signal: signal })
    .then((response) =>
      setCoachOptions(
        response.data
          .filter((user) => user.role != UserRole.Disabled)
          .map((coach) => {
            return { value: coach, label: coach.username };
          })
      )
    )
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function that will get all urls given and push them into the resultList
 *
 * @param urls - List of urls to get
 * @param resultList - List to push results unto
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 418 response
 */
export async function getUrlList<Type>(
  urls: Url[],
  resultList: Type[],
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  await axios
    .all(
      urls.map((url) => axiosAuthenticated.get<Type>(url, { signal: signal }))
    )
    .then((response) => {
      if (signal.aborted) {
        return;
      }
      response.forEach((resp) => resultList.push(resp.data));
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}

/**
 * Function that will get all urls given and set them into the resultMap
 * This will map each url onto the object from the request
 *
 * @param urls - List of urls to get
 * @param resultMap - Map to set the results in
 * @param signal - AbortSignal for the axios request
 * @param setError - Callback to set error message
 * @param router - Router object needed for error handling on 418 response
 */
export async function getUrlMap<Type>(
  urls: Url[],
  resultMap: Map<Url, Type>,
  signal: AbortSignal,
  setError: (error: string) => void,
  router: NextRouter
) {
  await axios
    .all(
      urls.map((url) => axiosAuthenticated.get<Type>(url, { signal: signal }))
    )
    .then((response) => {
      if (signal.aborted) {
        return;
      }
      response.forEach((resp) =>
        resultMap.set(resp.config.url as string, resp.data)
      );
    })
    .catch((err) => {
      parseError(err, setError, router, signal);
    });
}
