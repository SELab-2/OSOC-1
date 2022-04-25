import { axiosAuthenticated } from './axios';
import {Skill, Url} from './types';
import Endpoints from './endpoints';
import axios from "axios";

export async function getSkills(
  setSkillOptions: (
    skillOptions: Array<{ value: string; label: string }>
  ) => void
) {
  await axiosAuthenticated
    .get<Skill[]>(Endpoints.SKILLS)
    .then((response) =>
      setSkillOptions(
        response.data.map((skill) => {
          return { value: skill.skillName, label: skill.skillName };
        })
      )
    )
    .catch((err) => console.log(err));
}

export async function getUrlList<Type>(urls: Url[], resultList: Type[]) {
    await axios
        .all(urls.map((url) => axiosAuthenticated.get<Type>(url)))
        .then((response) => {
            response.forEach((resp) => resultList.push(resp.data));
        })
        .catch((ex) => {
            console.log(ex);
        });
}

export async function getUrlDict<Type>(urls: Url[], resultMap: Map<Url, Type>) {
    await axios
        .all(urls.map((url) => axiosAuthenticated.get<Type>(url)))
        .then((response) => {
            response.forEach((resp) =>
                resultMap.set(resp.config.url as string, resp.data)
            );
        })
        .catch((ex) => {
            console.log('getUrlDict', ex);
        });
}
