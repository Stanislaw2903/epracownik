import { SERVER_API_URL } from 'app/app.constants';
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { IVacationsModel } from 'app/shared/model/vacations.model';
import { createRequestOption, convertDateToServer } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';

type EntityArrayResponseType = HttpResponse<IVacationsModel[]>;

@Injectable({ providedIn: 'root' })
export class UserVacationService {
  public resourceUrl = SERVER_API_URL + 'api/vacation';
  constructor(protected http: HttpClient) {}

  getVacations(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(convertDateToServer(req, ['dateFrom', 'dateTo', 'timeFrom', 'timeTo', 'createdDate'], true));
    return this.http.get<IVacationsModel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  deleteApplication(id: number): Observable<any> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
