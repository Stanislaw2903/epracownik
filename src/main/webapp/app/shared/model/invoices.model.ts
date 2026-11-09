import { Moment } from 'moment';

export interface IInvoices {
  id?: number;
  issueDate?: Moment;
  documentNumber?: string;
  paymentDeadline?: Moment;
  grossAmount?: number;
  toPay?: number;
  documentFileContentType?: string;
  documentFile?: any;
  status?: boolean;
}

export class Invoices implements IInvoices {
  constructor(
    public id?: number,
    public issueDate?: Moment,
    public documentNumber?: string,
    public paymentDeadline?: Moment,
    public grossAmount?: number,
    public toPay?: number,
    public documentFileContentType?: string,
    public documentFile?: any,
    public status?: boolean
  ) {
    this.status = this.status || false;
  }
}
