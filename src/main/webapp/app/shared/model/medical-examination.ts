import { Moment } from 'moment';

export interface MedicalExamination {
  comments?: string;
  dateFrom?: Date | Moment;
  dateTo?: Date | Moment;
  expires?: boolean;
  kind?: string;
  sanitaryTests?: string;
}

export class MedicalExamination implements MedicalExamination {
  constructor(
    public comments?: string,
    public dateFrom?: Date | Moment,
    public dateTo?: Date | Moment,
    public expires?: boolean,
    public sanitaryTests?: string
  ) {
    this.expires = this.expires || false;
  }
}
