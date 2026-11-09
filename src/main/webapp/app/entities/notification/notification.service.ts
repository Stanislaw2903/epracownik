import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { TranslateService } from '@ngx-translate/core';
import { SERVER_API_URL } from 'app/app.constants';
import { INotification } from 'app/shared/model/notification.model';
import { convertDateToServer, createRequestOption } from 'app/shared/util/request-util';
import { SelectItem } from 'primeng/api';

type EntityResponseType = HttpResponse<INotification>;
type EntityArrayResponseType = HttpResponse<INotification[]>;

@Injectable({ providedIn: 'root' })
export class NotificationService {
  public resourceUrl = SERVER_API_URL + 'api/notifications';

  constructor(protected http: HttpClient, private translate: TranslateService) {}

  create(notification: INotification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notification);
    const payload = convertDateToServer(copy, ['createDate', 'readDate']);
    return this.http
      .post<INotification>(this.resourceUrl, payload, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(notification: INotification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notification);
    const payload = convertDateToServer(copy, ['createDate', 'readDate']);
    return this.http
      .put<INotification>(this.resourceUrl, payload, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INotification>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
  findForUser(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INotification>(`${this.resourceUrl}/user/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INotification[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }
  userNotifications(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INotification[]>(this.resourceUrl + '/user', { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getNotificationStatusDict(): SelectItem[] {
    return [
      {
        label: this.translate.instant('ebokApp.notification.readed'),
        value: true,
      },
      {
        label: this.translate.instant('ebokApp.notification.nonReaded'),
        value: false,
      },
    ];
  }

  protected convertDateFromClient(notification: INotification): INotification {
    const copy: INotification = Object.assign({}, notification, {
      createDate: notification.createDate && notification.createDate.isValid() ? notification.createDate.toJSON() : undefined,
      readDate: notification.readDate && notification.readDate.isValid() ? notification.readDate.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createDate = res.body.createDate ? moment(res.body.createDate) : undefined;
      res.body.readDate = res.body.readDate ? moment(res.body.readDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((notification: INotification) => {
        notification.createDate = notification.createDate ? moment(notification.createDate) : undefined;
        notification.readDate = notification.readDate ? moment(notification.readDate) : undefined;
      });
    }
    return res;
  }
}
