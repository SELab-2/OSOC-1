export type UUID = string;
export type AuthToken = string;
export type Url = string;

export type AuthTokens = {
  /**
   *  accessToken linked to the current user
   */
  accessToken: AuthToken;

  /**
   *  refreshToken linked to the current user
   */
  refreshToken: AuthToken;
};

export enum UserRole {
  Admin = 'Admin',
  Coach = 'Coach',
  Disabled = 'Disabled',
}

export type User = {
  /**
   * Unique identifier for the user
   */
  id: UUID;

  /**
   * Visible username of the user
   */
  username: string;

  /**
   * Email address of the user
   */
  email: string;

  /**
   * Current role of the user
   * @see {@link UserRole}
   */
  role: UserRole;
};

/**
 * All types below should conform to the osoc.yaml file!
 */

// TODO add documentation once these types are used with actual backend data and thus correct

export enum StatusSuggestionStatus {
  Yes = 'Yes',
  No = 'No',
  Maybe = 'Maybe',
}

export type StudentData = {
  collection: StudentBase[];
  totalLength: number;
};

export type ProjectData = {
  collection: ProjectBase[];
  totalLength: number;
};

// TODO fix this once tally form & communications is done
export type Student = {
  id: UUID;
  firstName: string;
  lastName: string;
  status: string;
  statusSuggestions: StatusSuggestion[];
  alumn: boolean;
  possibleStudentCoach: boolean;
  skills: Skill[];
  communications: Url[];
  answers: Url[];
};

export type StudentBase = {
  id: UUID;
  firstName: string;
  lastName: string;
  status: string;
  statusSuggestions: Url[];
  alumn: boolean;
  possibleStudentCoach: boolean;
  skills: Skill[];
  communications: Url[];
  answers: Url[];
};

export type StatusSuggestion = {
  coachId: UUID;
  status: StatusSuggestionStatus;
  motivation: string;
};

export type ProjectBase = {
  id: UUID;
  name: string;
  clientName: string;
  description: string;
  coaches: Url[];
  positions: Url[];
  assignments: Url[];
};

export type Project = {
  id: UUID;
  name: string;
  clientName: string;
  description: string;
  coaches: User[];
  positions: Position[];
  assignments: Assignment[];
};

export type Position = {
  id: UUID;
  skill: Skill;
  amount: number;
};

export type Assignment = {
  id: UUID;
  student: Student;
  position: Position;
  suggester: User;
  reason: string;
};

export type Skill = {
  skillName: string;
};

/**
 * Used for drag n drop
 */
export const ItemTypes = {
  STUDENTTILE: 'studentTile',
};
