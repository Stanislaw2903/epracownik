import { Moment } from 'moment';

export interface ICourses {
  completionDate?: Date | Moment;
  donated?: string;
  name?: string;
}

export class Courses implements ICourses {
  constructor(public comments?: string, public completionDate?: Date | Moment, public expires?: boolean, public kind?: string) {
    this.expires = this.expires || false;
  }
}
