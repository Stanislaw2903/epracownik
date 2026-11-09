import { Moment } from 'moment';
export interface IPersonalQualifications {
  completionDate?: Date | Moment;
  dateTo?: Date | Moment;
  expires?: boolean;
  name?: string;
}

export class PersonalQualifications implements IPersonalQualifications {
  constructor(public completionDate: Date | Moment, public dateTo: Date | Moment, public expires: boolean, public name: string) {}
}
