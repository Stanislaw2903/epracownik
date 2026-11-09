import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IGraphicTableItem } from '../../shared/model/graphic.model';
import { createRequestOption } from 'app/shared/util/request-util';

type EntityArrayGraphicResponseType = HttpResponse<IGraphicTableItem[]>;

@Injectable({
  providedIn: 'root',
})
export class GraphicService {
  public resourceUrl = SERVER_API_URL + 'api/graphic';
  constructor(protected http: HttpClient) {}

  getData(req?: any): Observable<EntityArrayGraphicResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGraphicTableItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
