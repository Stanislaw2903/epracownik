import { Moment } from 'moment';

export interface IVacationsModel {
  comment?: string;
  createdBy?: CreatedByOrProcessedBy | null;
  dateFrom?: Date | Moment | string;
  dateTo?: Date | Moment | string;
  fullDay?: boolean;
  id?: number;
  processedBy?: CreatedByOrProcessedBy | null;
  status?: string;
  statusDescription?: string;
  sendDate?: Date | Moment | string;
  used?: number;
  createdDate?: Date | Moment | string;
  vacationType?: string;
  erpId?: number | null;
}
export interface CreatedByOrProcessedBy {
  activated: boolean;
  email: string;
  externalId: number;
  firstName: string;
  id: number;
  imageUrl: string;
  langKey: string;
  lastName: string;
  login: string;
  resetDate: string;
  approvalVerification?: string;
}

export interface IVacationFilters {
  filters: Filters;
  filteredValue?: any;
}
export interface Filters {
  createdBy: CreatedByOrSendDateOrDateFromOrStatus;
  sendDate: CreatedByOrSendDateOrDateFromOrStatus;
  fullDay: FullDay;
  dateFrom: CreatedByOrSendDateOrDateFromOrStatus;
  dateTo: DateToOrProcessedBy;
  timeFrom: TimeFromOrTimeTo;
  timeTo: TimeFromOrTimeTo;
  processedBy: DateToOrProcessedBy;
  status: CreatedByOrSendDateOrDateFromOrStatus;
}
export interface CreatedByOrSendDateOrDateFromOrStatus {
  value: string;
  matchMode: string;
}
export interface FullDay {
  value: boolean;
  matchMode: string;
}
export interface DateToOrProcessedBy {
  value?: null;
  matchMode: string;
}
export interface TimeFromOrTimeTo {
  value: Value;
  matchMode: string;
}
export interface Value {
  withTime: string;
}

export interface IVacationAcceptModel {
  id?: number;
  comment?: string;
  status?: string;
}

export enum VacationStatus {
  WAITING = 'WAITING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
}

export interface IVacationUpdate extends IVacationAcceptModel {
  comment?: string;
  createdBy?: CreatedByOrProcessedBy | null;
  dateFrom?: Date | Moment | string;
  dateTo?: Date | Moment | string;
  fullDay?: boolean;
  processedBy?: CreatedByOrProcessedBy | null;
  statusDescription?: string;
  createdDate?: Date | Moment | string;
  status?: string;
  erpId?: number | null;
  vacationType?: string;
  used?: number;
}

export interface IVacationHistoryDetails {
  days: Date[] | Moment[] | string[];
  daysTaken: number;
  month: number;
}

export interface IVacationDetailData {
  used: number;
  vested: number;
  total?: number;
  percentage?: number;
}

export interface IVacationFreeDays {
  used: number;
  tooMuch?: number;
  workingDays: number;
}

export class Vacations implements IVacationsModel {
  constructor(
    public comment?: string,
    public dateFrom?: string,
    public dateTo?: string,
    public fullDay?: boolean,
    public id?: number,
    public status?: string,
    public statusDescription?: string,
    public vacationType?: string
  ) {}
}
