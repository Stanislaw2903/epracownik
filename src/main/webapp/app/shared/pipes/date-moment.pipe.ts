import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({
  name: 'dateMoment',
})
export class DateMomentPipe implements PipeTransform {
  private romanNumber = {
    1: 'I',
    2: 'II',
    3: 'III',
    4: 'IV',
    5: 'V',
    6: 'VI',
    7: 'VII',
    8: 'VIII',
    9: 'IX',
    10: 'X',
    11: 'XI',
    12: 'XII',
  };
  transform(value: any, format = 'dd-MM-yyyy'): string {
    if (format === 'ROMAN') {
      return this.romanNumber[moment(value).format('M')];
    }
    return moment(value).format(format);
  }
}
