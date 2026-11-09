import { Moment } from 'moment';

export interface IQualifications {
  id?: number;
  name?: string;
  startDate?: Moment;
  expireDate?: Moment;
  comments?: string;
  subsidized?: boolean;
}

export class Qualifications implements IQualifications {
  constructor(
    public id?: number,
    public name?: string,
    public startDate?: Moment,
    public expireDate?: Moment,
    public comments?: string,
    public subsidized?: boolean
  ) {
    this.subsidized = this.subsidized || false;
  }
}
