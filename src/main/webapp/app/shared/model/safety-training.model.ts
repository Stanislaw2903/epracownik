import { Moment } from 'moment';

export interface ISafetyTraining {
  comments?: string;
  dateFrom?: Date | Moment;
  expires?: boolean;
  kind?: string;
}

export class SafetyTraining implements ISafetyTraining {
  constructor(public comments?: string, public dateFrom?: Date | Moment, public expires?: boolean, public kind?: string) {
    this.expires = this.expires || false;
  }
}
