import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { IAbsences } from 'app/shared/model/absences.model';
import { convertDateToServer, createRequestOption } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

type EntityArrayResponseType = HttpResponse<IAbsences[]>;
@Injectable({
  providedIn: 'root',
})
export class AbsencesService {
  public resourceUrl = SERVER_API_URL + 'api/absences';
  constructor(protected http: HttpClient) {}

  getAbsences(req?: any): Observable<EntityArrayResponseType> {
    if (!req?.firstName) delete req?.firstName;
    if (!req?.lastName) delete req?.lastName;
    if (!req?.externalIds) delete req?.externalIds;
    const options = createRequestOption(convertDateToServer(req, ['dateFrom', 'dateTo'], true));

    return this.http.get<IAbsences[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getUserAbsencesList(): Observable<any> {
    return this.http.get<any[]>(`${this.resourceUrl}/dictionary/relatedUsers`).pipe(
      map(res => {
        return res.map(item => ({
          name: item.firstName + ' ' + item.lastName,
          externalId: item.externalId,
        }));
      })
    );
  }

  // getAbsences(): Observable<IAbsences[]> {
  //   return of([
  //     {
  //       code: '21',
  //       dateFrom: '2020-12-30',
  //       dateTo: '2020-12-31',
  //       id: 1,
  //     },
  //     {
  //       code: '24',
  //       dateFrom: '2020-12-30',
  //       dateTo: '2020-12-31',
  //       id: 2,
  //     },
  //   ]);
  // }
}
