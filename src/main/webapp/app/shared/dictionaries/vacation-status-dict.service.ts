import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CustomMenuItem } from '../model/dictionaries.model';
import { TranslateService } from '@ngx-translate/core';
import { SERVER_API_URL } from 'app/app.constants';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class VacationStatusDictService {
  constructor(private translateService: TranslateService, protected http: HttpClient) {}

  getVacationStatusDict(): Observable<CustomMenuItem[]> {
    return this.http.get<any[]>(SERVER_API_URL + '/api/admin/vacation/statusType').pipe(
      map((res: any[]) => {
        return res.map((item: any) => ({
          label: this.translateService.instant(`ebokApp.vacations.statuses.${item.name}`),
          value: item.name,
        }));
      })
    );
  }

  getVacationDefaultStatusDict(): Observable<string> {
    return this.http.get<string>(SERVER_API_URL + '/api/admin/vacation/statusType/default', { responseType: 'text' as 'json' });
  }
}
