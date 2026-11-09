import { Moment } from 'moment';
export interface IAbsences {
  code?: string;
  dateFrom?: Date | Moment | string;
  dateTo?: Date | Moment | string;
  id?: number;
  ospId?: number;
  firstName?: string;
  lastName?: string;
}

export interface IAbsencesDict {
  label: string;
  id: number;
  code: string | number;
}

export class Absences implements IAbsences {
  constructor(public code?: string, public dateFrom?: Date | Moment | string, public dateTo?: Date | Moment | string, public id?: number) {}
}
