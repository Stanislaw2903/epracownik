import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IUser } from 'app/core/user/user.model';

@Injectable({ providedIn: 'root' })
export class RegisterService {
  accountInformation = {
    personalData: {
      clientType: 'individual',
      clientNumber: null,
      pesel: null,
      phone: null,
    },
    systemData: {
      email: '',
      password: '',
    },
  };
  constructor(private http: HttpClient) {}

  getAccountInformation(): any {
    return this.accountInformation;
  }

  setAccountInformation(accountInformation: any): any {
    this.accountInformation = accountInformation;
  }

  save(account: IUser): Observable<{}> {
    return this.http.post(SERVER_API_URL + 'api/register', account);
  }
}
