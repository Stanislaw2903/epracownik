import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { INotificationRead } from 'app/shared/model/notification-read.model';

type EntityResponseType = HttpResponse<INotificationRead>;
type EntityArrayResponseType = HttpResponse<INotificationRead[]>;

@Injectable({ providedIn: 'root' })
export class NotificationReadService {
  public resourceUrl = SERVER_API_URL + 'api/notification-reads';

  constructor(protected http: HttpClient) {}

  create(notificationRead: INotificationRead): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notificationRead);
    return this.http
      .post<INotificationRead>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(notificationRead: INotificationRead): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notificationRead);
    return this.http
      .put<INotificationRead>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INotificationRead>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INotificationRead[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }
  findByNotificationId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<INotificationRead[]>(`${this.resourceUrl}/notification/${id}`, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(notificationRead: INotificationRead): INotificationRead {
    const copy: INotificationRead = Object.assign({}, notificationRead, {
      date: notificationRead.date && notificationRead.date.isValid() ? notificationRead.date.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((notificationRead: INotificationRead) => {
        notificationRead.date = notificationRead.date ? moment(notificationRead.date) : undefined;
      });
    }
    return res;
  }
}
