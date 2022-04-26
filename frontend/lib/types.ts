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
 * All possible values for the Student - StatusSuggestion - status type
 * Not to be confused with Student - status
 */
export enum StatusSuggestionStatus {
  Yes = 'Yes',
  No = 'No',
  Maybe = 'Maybe',
}

/**
 * This is the full object returned by a get to the Student endpoint
 */
export type StudentData = {
  collection: StudentBase[];
  totalLength: number;
};

/**
 * This is the full object returned by a get to the Project endpoint
 */
export type ProjectData = {
  collection: ProjectBase[];
  totalLength: number;
};

/**
 * This is the Student type as defined in osoc.yaml
 */
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

/**
 * This is the exact collection type returned by a get to the Student endpoint
 */
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

/**
 * This is the StatusSuggestion type as defined in osoc.yaml
 */
export type StatusSuggestion = {
  coachId: UUID;
  status: StatusSuggestionStatus;
  motivation: string;
};

/**
 * This is the exact collection type returned by a get to the Project endpoint
 */
export type ProjectBase = {
  id: UUID;
  name: string;
  clientName: string;
  description: string;
  coaches: Url[];
  positions: Url[];
  assignments: Url[];
};

/**
 * This is the Project type as defined in osoc.yaml
 */
export type Project = {
  id: UUID;
  name: string;
  clientName: string;
  description: string;
  coaches: User[];
  positions: Position[];
  assignments: Assignment[];
};

/**
 * This is the Position type as defined in osoc.yaml
 */
export type Position = {
  id: UUID;
  skill: Skill;
  amount: number;
};

/**
 * This is the Assignment type as defined in osoc.yaml
 */
export type Assignment = {
  id: UUID;
  student: Student;
  position: Position;
  suggester: User;
  reason: string;
};

/**
 * This is the Skill type as defined in osoc.yaml
 */
export type Skill = {
  skillName: string;
};

/**
 * Used for drag n drop
 */
export const ItemTypes = {
  STUDENTTILE: 'studentTile',
};
