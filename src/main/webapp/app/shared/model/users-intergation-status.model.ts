export interface IIntegrationStatus {
  error: any[];
  success: [];
}

export interface IIsContractor {
  contractor?: boolean;
  integrateWithMandory?: boolean;
}

export class IsContractor implements IIsContractor {
  constructor(public contractor?: boolean) {
    this.contractor = this.contractor || false;
  }
}
