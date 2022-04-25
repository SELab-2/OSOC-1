import { axiosAuthenticated } from './axios';
import { Skill } from './types';
import Endpoints from './endpoints';

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
