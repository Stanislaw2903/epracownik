export interface IEmployee {
  name: string;
  secondName?: string;
  surname: string;
  pesel: number;
  fathersName?: string;
  mothersName?: string;
  dateOfEmploy: Date | string;
  dateOfDismissal?: Date | string;
  dateOfBirth: Date | string;
  birthplace?: string;
}
