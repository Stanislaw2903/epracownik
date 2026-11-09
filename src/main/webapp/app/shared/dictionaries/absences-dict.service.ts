import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { IAbsencesDict } from '../model/absences.model';

@Injectable({
  providedIn: 'root',
})
export class AbsencesDictService {
  constructor() {}

  getAbsencesDict(): Observable<IAbsencesDict[]> {
    return of([
      {
        id: 1,
        code: 'X',
        label: 'Urlop bezpłatny',
      },
      {
        id: 2,
        code: 'B',
        label: 'Urlop wychowawczy',
      },
      {
        id: 3,
        code: 'W',
        label: 'Służba wojskowa lub zastępcza',
      },
      {
        id: 4,
        code: 'N',
        label: 'Choroba  (wynagr. za czas choroby)',
      },
      {
        id: 5,
        code: 'U',
        label: 'Urlop wypoczynkowy',
      },
      {
        id: 6,
        code: 'K',
        label: 'Okres nieobecności w pracy',
      },
      {
        id: 7,
        code: 'A',
        label: 'Urlop macierzyński',
      },
      {
        id: 9,
        code: 'N',
        label: 'Wypadek przy pracy lub choroba zawodowa ',
      },
      {
        id: 14,
        code: 'S',
        label: 'Skrócenie okresu wypowiedzenia ',
      },
      {
        id: 15,
        code: '0',
        label: 'Zasiłek rodz., piel. lub wych.',
      },
      {
        id: 16,
        code: '0',
        label: 'Świadczenie z obecnością w pracy',
      },
      {
        id: 17,
        code: 'K',
        label: 'Macierzyństwo',
      },
      {
        id: 18,
        code: '8',
        label: 'Dni dla Matki z dzieckiem do 14 lat ',
      },
      {
        id: 19,
        code: 'K',
        label: 'Choroba  (zasiłek chorobowy)',
      },
      {
        id: 20,
        code: 'Q',
        label: 'Choroba (dni bez prawa do wynagr.) ',
      },
      {
        id: 21,
        code: '0',
        label: 'Zasiłek rodz., piel. lub św. Rodzinne',
      },
      {
        id: 22,
        code: 'O',
        label: 'Urlop okolicznościowy ',
      },
      {
        id: 23,
        code: 'K',
        label: 'Świadczenie rehabilitacyjne',
      },
      {
        id: 24,
        code: 'K',
        label: 'Zasiłek opiekuńczy',
      },
      {
        id: 26,
        code: 'K',
        label: 'Choroba (osoba nieuprawniona do wynagr.) ',
      },
      {
        id: 27,
        code: '0',
        label: 'Odbiór godzin nadliczbowych',
      },
      {
        id: 28,
        code: 'P',
        label: 'Przestój w pracy ',
      },
      {
        id: 31,
        code: 'J',
        label: 'Urlop ojcowski ',
      },
      {
        id: 32,
        code: 'L',
        label: 'Oddelegowanie za granicę ',
      },
      {
        id: 33,
        code: '1',
        label: 'Urlop rodzicielski',
      },
      {
        id: 34,
        code: 'C',
        label: 'Urlop na warunkach url. macierzyńskiego',
      },
      {
        id: 35,
        code: '0',
        label: 'Zwolnienie z pracy w okresie wypowiedzenia',
      },
      {
        id: 40,
        code: '0',
        label: 'Urlop na poszukiwanie pracy',
      },
    ]);
  }
}
