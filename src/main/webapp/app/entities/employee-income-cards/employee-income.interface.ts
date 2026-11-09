import { IPageable } from './../../shared/model/pageable.model';
import { ISort } from './../../shared/model/sort.model';

export interface IEmployeeIncome {
  content: IEmployeeIncomeContent[];
  pageable?: IPageable;
  totalPages?: number;
  totalElements?: number;
  last?: boolean;
  number?: number;
  size?: number;
  sort?: ISort;
  first?: boolean;
  numberOfElements?: number;
  empty?: boolean;
}

export interface IEmployeeIncomeContent {
  socialInsuranceBenefits: number;
  listId: number;
  externalId: number;
  paymentDate: Date | string;
  dateTo: Date | string;
  salaryInCash: number;
  salaryInKind?: number;
  taxed: number;
  grossPremium: number;
  taxedWithoutZus?: number;
  withoutZusAndTax: number;
  inability: number;
  type: string;
  socialSecurityContributionsPaid: ISocialSecurityContributionsPaid;
  costOfGettingIncome: number;
  socialSecurityDeducted: number;
  incomeInAGivenMonth: number;
  incomeFromBeginningOfYear: number;
  rate: number;
  freeAmount: number;
  accruedAdvancePaymentForIncomeTax: number;
  healthInsurancePremium: IHealthInsurancePremium;
  advancePaymentForIncomeTax: IAdvancePaymentForIncomeTax;
}

export interface ISocialSecurityContributionsPaid {
  retirement: number;
  disability: number;
  sickness: number;
  contributionsSum: number;
}

export interface IHealthInsurancePremium {
  taken: number;
  deducted: number;
}

export interface IAdvancePaymentForIncomeTax {
  due: number;
  collected: number;
  deductions: number;
  toPay: number;
}

export interface IIncomesDetails {
  content: IIncomesDetailsContent[];
  pageable: IPageable;
  totalPages: number;
  totalElements: number;
  last: boolean;
  number: number;
  size: number;
  sort: ISort;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface IIncomesDetailsContent {
  amount: number;
  code: string;
}

export interface IPayrollDetails {
  rows?: RowsEntity[] | null;
  columns?: ColumnsEntity[] | null;
}
export interface RowsEntity {
  listNum: string;
  listId: number;
  paymentDate: number;
  rows?: RowsEntity1[] | null;
}
export interface RowsEntity1 {
  code: string;
  amount: number;
}
export interface ColumnsEntity {
  code: number;
  description: string;
}
