import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IIntegrationCandidate, IIntegrationStatus, IIsContractor } from 'app/shared/model/users-intergation-status.model';
import { createRequestOption } from 'app/shared/util/request-util';
import { SelectItem } from 'primeng/api';
import { IUser, IUserGroup } from './user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  public resourceUrl = SERVER_API_URL + 'api/users';

  integrateWithMandory = false;

  constructor(private http: HttpClient) {}

  create(user: IUser): Observable<IUser> {
    return this.http.post<IUser>(this.resourceUrl, user);
  }

  update(user: IUser): Observable<IUser> {
    return this.http.put<IUser>(this.resourceUrl, user);
  }

  find(login: string): Observable<IUser> {
    return this.http.get<IUser>(`${this.resourceUrl}/${login}`);
  }

  query(req?: any): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryWithPhone(req?: any): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(`${this.resourceUrl}?withPhone`, { params: options, observe: 'response' });
  }

  queryGroup(req?: any): Observable<HttpResponse<IUserGroup[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUserGroup[]>(`${this.resourceUrl}/groups`, { params: options, observe: 'response' });
  }

  queryActive(req?: any): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(`${this.resourceUrl}?active`, { params: options, observe: 'response' });
  }

  getDictAllUsers(): Observable<IUser[]> {
    return this.http.get<IUser[]>(`${this.resourceUrl}/all`);
  }

  delete(login: string): Observable<{}> {
    return this.http.delete(`${this.resourceUrl}/${login}`);
  }

  authorities(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/users/authorities');
  }
  updateUserDatabase(body: IIsContractor): Observable<IIntegrationStatus> {
    return this.http.post<IIntegrationStatus>(SERVER_API_URL + 'api/updateUserDatabase', body);
  }

  resendMail(): Observable<any> {
    return this.http.get<any>(SERVER_API_URL + 'api/activation/resend');
  }

  resendMailToUser(userId: number): Observable<IIntegrationStatus> {
    return this.http.post<IIntegrationStatus>(`${SERVER_API_URL}api/activation/resend/${userId}`, {});
  }

  resendMailToUsers(userIds: number[]): Observable<IIntegrationStatus> {
    return this.http.post<IIntegrationStatus>(`${SERVER_API_URL}api/activation/resend`, userIds);
  }

  getIntegrationCandidates(contractor: boolean): Observable<IIntegrationCandidate[]> {
    return this.http.get<IIntegrationCandidate[]>(SERVER_API_URL + 'api/integration/candidates', {
      params: { contractor: String(contractor) },
    });
  }

  permissionVacation(): Observable<SelectItem[]> {
    return of([
      {
        label: 'Wszyscy',
        value: 'ALL',
      },
      {
        label: 'Dla wybranej grupy',
        value: 'SPECIFIC_GROUP',
      },
      {
        label: 'Dla grup użytkowników',
        value: 'USER_GROUP',
      },
      {
        label: 'Dla nikogo',
        value: 'NO_ONE',
      },
    ]);
  }

  approvalVerificationTypes(): Observable<SelectItem[]> {
    return of([
      {
        label: 'Jednopoziomowy',
        value: 'ONE_LEVEL',
      },
      {
        label: 'Dwupoziomowy',
        value: 'TWO_LEVEL',
      },
    ]);
  }

  approvalTypes(): Observable<SelectItem[]> {
    return of([
      {
        label: 'Wstępna akceptacja',
        value: 'INITIAL_VERIFICATION',
      },
      {
        label: 'Ostateczna akceptacja',
        value: 'FINAL_VERIFICATION',
      },
    ]);
  }

  getRelatedUser(actualEditedUserId: number): Observable<any> {
    return this.http.get<any>(SERVER_API_URL + 'api/users/relatedUsers/' + actualEditedUserId);
  }
}
