import { HttpClient, HttpParams } from '@angular/common/http';
import { IEmployeeIncome, IIncomesDetails } from './employee-income.interface';

import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { SERVER_API_URL } from './../../app.constants';
import * as moment from 'moment';

export interface IDocumentPDFPayroll {
  confirmed: string;
  file: File;
  fileType: string;
  id: number;
  viewed: string;
}
interface File {
  content: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class IncomesService {
  public resourceUrl = SERVER_API_URL + 'api/payroll';

  constructor(protected http: HttpClient) {}

  getPDFDocumentExist(filetype: string, year: string): Observable<any> {
    return this.http.get<any>(`${this.resourceUrl}/documents/${filetype}/exists/${year}`);
  }

  getFilePDF(filetype: string, year: string): Observable<IDocumentPDFPayroll> {
    return this.http.get<IDocumentPDFPayroll>(`${this.resourceUrl}/documents/${filetype}/${year}`);
  }

  setConfirmReadPdf(id: number): Observable<any> {
    return this.http.post<IDocumentPDFPayroll>(`${this.resourceUrl}/documents/${id}/confirm`, null);
  }

  getIncomes(year?: number | string): Observable<IEmployeeIncome> {
    let params = new HttpParams();
    if (year) {
      params = params.set('year', year.toString());
    }
    return this.http.get<IEmployeeIncome>(`${this.resourceUrl}`, { params });
  }

  getIncomesDetailsByListId(year?: number | string): Observable<IIncomesDetails> {
    let params = new HttpParams();
    if (year) {
      params = params.set('year', year.toString());
    }
    return this.http.get<IIncomesDetails>(`${this.resourceUrl}/details`, { params });
  }

  getDetails(currentYear: string): Observable<any> {
    console.log('currentYear1111 ', currentYear);
    return of([
      {
        month: moment('01-01-2020', 'DD-MM-YYYY').toDate(),
        listName: 'LP/2019/01/006',
        hourlySalary: 1,
        hourlySalaryCumulatively: 2,
        discretionaryBonus: 3,
        discretionaryBonusCumulatively: 4,
        equForWashing: 5,
        equForWashingCumulatively: 6,
        holidayPayFixedDues: 7,
        holidayPayFixedDuesCumulatively: 8,
        holidayPayVariableDues: 9,
        holidayPayVariableDuesCumulatively: 10,
        lifeInsurance: 11,
        lifeInsuranceCumulatively: 12,
      },
      {
        month: moment('01-02-2020', 'DD-MM-YYYY').toDate(),
        listName: 'LP/2019/01/006',
        hourlySalary: 1,
        hourlySalaryCumulatively: 2,
        discretionaryBonus: 3,
        discretionaryBonusCumulatively: 4,
        equForWashing: 5,
        equForWashingCumulatively: 6,
        holidayPayFixedDues: 7,
        holidayPayFixedDuesCumulatively: 8,
        holidayPayVariableDues: 9,
        holidayPayVariableDuesCumulatively: 10,
        lifeInsurance: 11,
        lifeInsuranceCumulatively: 12,
      },
    ]);
  }

  getAdditionalDescription(year: number, month: number): Observable<any> {
    let params = new HttpParams();
    if (year) {
      params = params.set('year', year.toString()).set('month', month.toString());
    }
    return this.http.get<IIncomesDetails>(`${this.resourceUrl}/additionalDescription`, { params });
  }

  getIngridients(): Observable<any> {
    return this.http.get<IIncomesDetails>(`${this.resourceUrl}/ingredients`);
  }

  getDeductions(): Observable<any> {
    return this.http.get<IIncomesDetails>(`${this.resourceUrl}/deductions`);
  }

  getBreaks(): Observable<any> {
    return this.http.get<IIncomesDetails>(`${this.resourceUrl}/breaks`);
  }
}
