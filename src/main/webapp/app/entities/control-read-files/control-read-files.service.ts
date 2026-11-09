import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IControlReadFiles } from '../../shared/model/control-read-files';

type EntityArrayResponseType = HttpResponse<IControlReadFiles[]>;

@Injectable({ providedIn: 'root' })
export class ControlReadFilesService {
  public resourceAbsencesUrl = SERVER_API_URL + 'api/absences';
  public resourcesUrl = SERVER_API_URL + 'api/documents';
  constructor(protected http: HttpClient) {}

  getUserAbsencesList(): Observable<any> {
    return this.http.get<any[]>(`${this.resourceAbsencesUrl}/dictionary/relatedUsers`).pipe(
      map(res => {
        return res.map(item => ({
          name: item.firstName + ' ' + item.lastName,
          id: item.id,
        }));
      })
    );
  }

  getTableData(req?: any): Observable<EntityArrayResponseType> {
    if (req?.userIds?.length) {
      req.userIds = req.userIds.map((item: any) => item.id);
    }

    if (!req?.year) delete req?.year;
    if (!req?.onlyConfirmed && req?.onlyConfirmed !== false) delete req?.onlyConfirmed;
    if (!req?.fileType) delete req?.fileType;
    if (!req?.userIds || !req?.userIds?.length) delete req?.userIds;

    const options = createRequestOption(req);

    return this.http.get<IControlReadFiles[]>(this.resourcesUrl, { params: options, observe: 'response' });
  }

  getFileTypes(): Observable<any> {
    return this.http.get<any>(`${this.resourcesUrl}/types`).pipe(
      map(items => {
        return items.map((item: any) => ({
          label: item.name,
          value: item.name,
        }));
      })
    );
  }
}
