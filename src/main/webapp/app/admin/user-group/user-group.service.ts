import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';

@Injectable({
  providedIn: 'root',
})
export class UserGroupService {
  private baseUrl = SERVER_API_URL + 'api/userGroup';

  constructor(private http: HttpClient) {}

  getUserGroups(params: any = {}): Observable<any> {
    let httpParams = new HttpParams();
    if (params.codes) httpParams = httpParams.append('codes', params.codes);
    if (params.ids) httpParams = httpParams.append('ids', params.ids);
    if (params.name) httpParams = httpParams.append('name', params.name);
    if (params.code) httpParams = httpParams.append('code', params.code);
    if (params.page) httpParams = httpParams.append('page', params.page);
    if (params.size) httpParams = httpParams.append('size', params.size);
    if (params.sort) httpParams = httpParams.append('sort', params.sort);

    return this.http.get(this.baseUrl, { params: httpParams });
  }

  createUserGroup(group: { code: string; name: string }): Observable<any> {
    return this.http.post(this.baseUrl, group);
  }

  updateUserGroup(group: { id: number; code: string; name: string }): Observable<any> {
    return this.http.put(this.baseUrl, group);
  }

  deleteUserGroup(id: number): Observable<any> {
    return this.http.request('DELETE', `${this.baseUrl}/${id}`, {
      body: {}, // Pusty obiekt, jeżeli chcesz wysłać ciało
      observe: 'response',
    });
  }
}
