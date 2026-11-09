import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface INotificationRead {
  id?: number;
  date?: Moment;
  notify?: number;
  users?: IUser[];
}

export class NotificationRead implements INotificationRead {
  constructor(public id?: number, public date?: Moment, public notify?: number, public users?: IUser[]) {}
}
