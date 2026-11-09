export interface IIntegrationStatus {
  error: any[];
  success: [];
}

export interface IIsContractor {
  contractor?: boolean;
  integrateWithMandory?: boolean;
  externalIds?: number[];
}

export interface IIntegrationCandidate {
  externalId: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  type?: string;
  problem?: string;
}

export class IsContractor implements IIsContractor {
  constructor(public contractor?: boolean) {
    this.contractor = this.contractor || false;
  }
}
