export interface IUser {
  id?: any;
  externalId?: any;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated?: boolean;
  langKey?: string;
  authorities?: string[];
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  password?: string;
  authorizationToAcceptVacation?: string;
  approvalVerificationType?: string;
  approvalType?: string;
  relatedUsers?: any;
  manualInsert?: boolean;
  phoneNumber?: string;
  groupId?: any;
  groups?: any[];
  type?: string;
}

export class User implements IUser {
  constructor(
    public id?: any,
    public externalId?: any,
    public login?: string,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public activated?: boolean,
    public langKey?: string,
    public authorities?: string[],
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public password?: string,
    public authorizationToAcceptVacation?: string,
    public approvalVerificationType?: string,
    public approvalType?: string,
    public relatedUsers?: string,
    public manualInsert?: boolean,
    public phoneNumber?: string,
    public groupId?: any,
    public groups?: any[],
    public type?: string
  ) {}
}

export interface IUserGroup {
  id?: any;
  name?: string;
  code?: string;
}

export class UserGroup implements IUserGroup {
  constructor(public id?: any, public name?: string, public code?: string) {}
}
