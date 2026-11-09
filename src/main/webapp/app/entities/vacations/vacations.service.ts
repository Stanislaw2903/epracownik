import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { IUser } from 'app/core/user/user.model';
import { IVacationAcceptModel, IVacationsModel } from 'app/shared/model/vacations.model';
import { convertDateToServer, createRequestOption } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';

type EntityVacationAcceptOrRejectApplication = HttpResponse<IVacationAcceptModel>;
type EntityArrayResponseType = HttpResponse<IVacationsModel[]>;
type EntityArrayUsersResponseType = HttpResponse<IUser[]>;
@Injectable({
  providedIn: 'root',
})
export class VacationsService {
  public resourceUrl = SERVER_API_URL + 'api/admin/vacation';
  constructor(protected http: HttpClient) {}

  updateApplicationStatus(application: IVacationAcceptModel): Observable<EntityVacationAcceptOrRejectApplication> {
    return this.http.put<IVacationAcceptModel>(this.resourceUrl, application, { observe: 'response' });
  }

  getVacations(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(convertDateToServer(req, ['dateFrom', 'dateTo', 'timeFrom', 'timeTo', 'createdDate'], true));
    return this.http.get<IVacationsModel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getUserApproval(): Observable<string> {
    return this.http.get<string>(this.resourceUrl + '/user-approval', { responseType: 'text' as 'json' });
  }

  getVacationBalance(externalId: number): Observable<{ used: number; vested: number }> {
    return this.http.get<{ used: number; vested: number }>(SERVER_API_URL + 'api/dashboard/vacation/' + externalId);
  }

  getVacationHistoryDetails(externalId: number, year: string): Observable<any[]> {
    return this.http.get<any[]>(SERVER_API_URL + 'api/vacation/history/details/' + externalId, {
      params: new HttpParams().set('year', year),
    });
  }

  getUsersDict(showAll = false): Observable<EntityArrayUsersResponseType> {
    let params = {};
    if (showAll) {
      params = createRequestOption({
        page: 0,
        size: 8000,
      });
    }
    return this.http.get<IUser[]>(SERVER_API_URL + 'api/users', { params, observe: 'response' });
  }
}
