import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IQualifications } from 'app/shared/model/qualifications.model';

type EntityResponseType = HttpResponse<IQualifications>;
type EntityArrayResponseType = HttpResponse<IQualifications[]>;

@Injectable({ providedIn: 'root' })
export class QualificationsService {
  public resourceUrl = SERVER_API_URL + 'api/qualifications';

  constructor(protected http: HttpClient) {}

  create(qualifications: IQualifications): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(qualifications);
    return this.http
      .post<IQualifications>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(qualifications: IQualifications): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(qualifications);
    return this.http
      .put<IQualifications>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IQualifications>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQualifications[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(qualifications: IQualifications): IQualifications {
    const copy: IQualifications = Object.assign({}, qualifications, {
      startDate: qualifications.startDate && qualifications.startDate.isValid() ? qualifications.startDate.format(DATE_FORMAT) : undefined,
      expireDate:
        qualifications.expireDate && qualifications.expireDate.isValid() ? qualifications.expireDate.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate ? moment(res.body.startDate) : undefined;
      res.body.expireDate = res.body.expireDate ? moment(res.body.expireDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((qualifications: IQualifications) => {
        qualifications.startDate = qualifications.startDate ? moment(qualifications.startDate) : undefined;
        qualifications.expireDate = qualifications.expireDate ? moment(qualifications.expireDate) : undefined;
      });
    }
    return res;
  }
}
