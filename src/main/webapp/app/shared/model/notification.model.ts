import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { notificationCategory } from 'app/shared/model/enumerations/notification-category.model';
import { notificationCanal } from 'app/shared/model/enumerations/notification-canal.model';

export interface INotification {
  groups?: any;
  id?: number;
  subject?: string;
  description?: string;
  createDate?: Moment;
  readDate?: Moment;
  status?: boolean;
  category?: notificationCategory;
  createdBy?: IUser;
  recepients?: IUser[];
  sendSms?: boolean;
  sendNotification?: boolean;
  sendMail?: boolean;
  canal?: notificationCanal;
}

export class Notification implements INotification {
  constructor(
    public id?: number,
    public subject?: string,
    public description?: string,
    public createDate?: Moment,
    public readDate?: Moment,
    public status?: boolean,
    public category?: notificationCategory,
    public createdBy?: IUser,
    public recepients?: IUser[],
    public sendSms?: boolean,
    public groups?: boolean,
    public sendNotification?: boolean,
    public sendMail?: boolean,
    public canal?: notificationCanal
  ) {
    this.status = this.status || false;
  }
}
