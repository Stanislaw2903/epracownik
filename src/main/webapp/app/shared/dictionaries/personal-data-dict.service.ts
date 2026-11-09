import { ICodeName } from './../model/dictionaries.model';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PersonalDataDictService {
  constructor() {}

  getMedicalKind(): Observable<ICodeName[]> {
    return of([
      {
        label: 'Okresowe',
        code: 'O',
      },
      {
        label: 'Wstępne',
        code: 'W',
      },
      {
        label: 'Sanepid',
        code: 'S',
      },
      {
        label: 'Wysokościowe',
        code: 'Y',
      },
      {
        label: 'Psychologiczne',
        code: 'P',
      },
      {
        label: 'Kontrolne',
        code: 'K',
      },
    ]);
  }

  getSafetyTrainingKind(): Observable<ICodeName[]> {
    return of([
      {
        label: 'Zmiana stanowiska pracy',
        code: 'Z',
      },
      {
        label: 'Okresowe st. BHP',
        code: 'B',
      },
      {
        label: 'Wstępne ogólne',
        code: 'P',
      },
      {
        label: 'Wstępne na st. pracy',
        code: 'W',
      },
      {
        label: 'Okresowe st. robotnicze',
        code: 'O',
      },
      {
        label: 'Okresowe st. Admin..biur. i inne',
        code: 'A',
      },
      {
        label: 'Okresowe osoby kierujące',
        code: 'K',
      },
      {
        label: 'Okresowe st. Inżynieryjno-techniczne',
        code: 'I',
      },
      {
        label: 'Metodyczne',
        code: 'M',
      },
      {
        label: 'Specjalistyczne',
        code: 'S',
      },
    ]);
  }
}
