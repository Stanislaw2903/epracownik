import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { IVacationDetailData, IVacationFreeDays, IVacationHistoryDetails } from 'app/shared/model/vacations.model';
import { convertDateToServer } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';
import { IVacationApplication, IVacationFullDayDict } from './home.interface';

type EntityVacationApplication = HttpResponse<IVacationApplication>;
type EntityVacationDetailData = HttpResponse<IVacationDetailData>;

@Injectable({ providedIn: 'root' })
export class HomeService {
  public resourceUrl = SERVER_API_URL + 'api/vacation';

  constructor(protected http: HttpClient) {}

  getVacationData(): Observable<EntityVacationDetailData> {
    return this.http.get<IVacationDetailData>(SERVER_API_URL + 'api/dashboard/vacation', { observe: 'response' });
  }

  sendVacationApplication(application: IVacationApplication): Observable<EntityVacationApplication> {
    const request = convertDateToServer(application, ['dateFrom', 'dateTo']);
    return this.http.post<IVacationApplication>(this.resourceUrl, request, { observe: 'response' });
  }

  getVacationDaysChartData(year: string): Observable<number[]> {
    return this.http.get<number[]>(`${SERVER_API_URL}api/vacation/history/`, {
      params: new HttpParams().set('year', year),
    });
  }

  getVacationDaysDetails(year: string): Observable<IVacationHistoryDetails[]> {
    return this.http.get<IVacationHistoryDetails[]>(`${SERVER_API_URL}/api/vacation/history/details`, {
      params: new HttpParams().set('year', year),
    });
  }

  getFreeVacationDays(dateFrom: Date, dateTo: Date): Observable<IVacationFreeDays> {
    const dateRange = convertDateToServer({ dateFrom, dateTo }, ['dateFrom', 'dateTo'], true);
    return this.http.get<IVacationFreeDays>(`${SERVER_API_URL}api/dashboard/calculate`, {
      params: new HttpParams().set('dateFrom', dateRange.dateFrom).set('dateTo', dateRange.dateTo),
    });
  }

  getVacationDaysChart(): any {
    return {
      labels: [
        'Styczeń',
        'Luty',
        'Marzec',
        'Kwiecień',
        'Maj',
        'Czerwiec',
        'Lipiec',
        'Sierpień',
        'Wrzesień',
        'Październik',
        'Listopad',
        'Grudzień',
      ],
    };
  }

  getFullDayDict(): IVacationFullDayDict[] {
    return [
      {
        name: 'Pełnodniowy',
        value: true,
      },
      {
        name: 'Godzinowy',
        value: false,
      },
    ];
  }

  getVacationTypes(): Observable<any> {
    return this.http.get<any>(SERVER_API_URL + 'api/vacation/types');
  }

  canActiveVacationButton(): Observable<any> {
    return this.http.get<any>(SERVER_API_URL + 'api/management/can-place-vacation-proposal', { responseType: 'text' as 'json' });
  }
}
