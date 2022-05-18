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

export type Edition = {
  /**
   * unique name of the edition
   */
  name: string;

  /**
   * Whether this edition is the current active edition
   */
  isActive: boolean;
};

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
 * This is the full object returned by a get to the Student endpoint with view List
 */
export type StudentDataList = {
  collection: StudentBaseList[];
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
// TODO fix this once communications is done
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
  answers: Answer[];
};

/**
 * This is the exact collection type returned by a get to the Student endpoint
 * Currently statusSuggestions, communications and answers still need to be dereferenced
 * Skills are returned as list of full objects
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

export type StudentBaseList = {
  id: UUID;
  firstName: string;
  lastName: string;
  status: string;
  alumn: boolean;
  possibleStudentCoach: boolean;
  statusSuggestionCount: StatusSuggestionCountList;
};

export type StatusSuggestionCountList = {
  Yes: number | null;
  Maybe: number | null;
  No: number | null;
};

/**
 * This is the StatusSuggestion type as defined in osoc.yaml
 */
export type StatusSuggestion = {
  suggester: User;
  status: StatusSuggestionStatus;
  motivation: string;
};

/**
 * This is the exact collection type returned by a get to the StatusSuggestion endpoint
 */
export type StatusSuggestionBase = {
  suggester: Url;
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
 * This is the Answer type as defined in osoc.yaml
 * optionId and key are dependent on tally and should probably not be used
 */
export type Answer = {
  id: UUID;
  key: string;
  question: string;
  answer: string[];
  optionId: string;
};

/**
 * Used for drag n drop
 */
export const ItemTypes = {
  STUDENTTILE: 'studentTile',
};

/**
 * This is one element from the exact collection type returned by a get to the Conflicts endpoint
 */
export type Conflict = {
  student: Url;
  projects: Url[];
};

/**
 * Helper type to all needed conflicts information
 */
export type conflictMapType = Map<
  UUID,
  { student: StudentBase; projectUrls: Set<Url>; amount: number }
>;
