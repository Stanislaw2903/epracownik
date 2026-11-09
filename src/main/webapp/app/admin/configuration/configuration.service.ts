import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';

export interface ConfigProps {
  contexts: Contexts;
}

export interface Contexts {
  [key: string]: Context;
}

export interface Context {
  beans: Beans;
  parentId?: any;
}

export interface Beans {
  [key: string]: Bean;
}

export interface Bean {
  prefix: string;
  properties: any;
}

export interface Env {
  activeProfiles?: string[];
  propertySources: PropertySource[];
}

export interface PropertySource {
  name: string;
  properties: Properties;
}

export interface Properties {
  [key: string]: Property;
}

export interface Property {
  value: string;
  origin?: string;
}

@Injectable({ providedIn: 'root' })
export class ConfigurationService {
  constructor(private http: HttpClient) {}

  getBeans(): Observable<Bean[]> {
    return this.http.get<ConfigProps>(SERVER_API_URL + 'management/configprops').pipe(
      map(configProps =>
        Object.values(
          Object.values(configProps.contexts)
            .map(context => context.beans)
            .reduce((allBeans: Beans, contextBeans: Beans) => ({ ...allBeans, ...contextBeans }))
        )
      )
    );
  }

  getPropertySources(): Observable<PropertySource[]> {
    return this.http.get<Env>(SERVER_API_URL + 'management/env').pipe(map(env => env.propertySources));
  }

  getVacationButtonStatus(): Observable<any> {
    return this.http.get<any>(SERVER_API_URL + 'api/management/active-place-vacation-proposal');
  }

  saveVacationButtonStatus(status: boolean): Observable<any> {
    return this.http.put<any>(
      SERVER_API_URL + 'api/management/active-place-vacation-proposal',
      {},
      { params: new HttpParams().set('value', status + '') }
    );
  }
}
