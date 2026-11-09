import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { MedicalExamination } from 'app/shared/model/medical-examination';
import { createRequestOption } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';

type EntityArrayResponseType = HttpResponse<MedicalExamination[]>;
@Injectable({
  providedIn: 'root',
})
export class SafetyTrainingService {
  public resourceUrl = SERVER_API_URL + 'api/safety-training';
  constructor(protected http: HttpClient) {}

  getSafetyTraining(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<MedicalExamination[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
