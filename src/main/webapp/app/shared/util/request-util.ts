import { HttpParams } from '@angular/common/http';
import { get, set } from 'lodash';
import * as moment from 'moment';

export interface Pagination {
  page: number;
  size: number;
  sort: string[];
}

export interface Search {
  query: string;
}

export interface SearchWithPagination extends Search, Pagination {}

export const convertDateFromServer = (resp: any, fields: string[]): any => {
  let tempDate;
  const prepareResponse = { ...resp };
  fields.forEach(field => {
    tempDate = get(prepareResponse, field);
    if (tempDate) {
      set(prepareResponse, field, moment(tempDate, 'x').toDate());
    }
  });

  return prepareResponse;
};

export const convertDateToServer = (resp: any, fields: string[], isStandardConv = false): any => {
  let tempDate;
  const prepareResponse = { ...resp };

  fields.forEach(field => {
    tempDate = get(prepareResponse, field);
    if (tempDate && !isStandardConv) {
      set(prepareResponse, field, parseInt(moment(tempDate).utc().format('x'), 10));
    } else if (tempDate) {
      set(prepareResponse, field, moment(tempDate).format('YYYY-MM-DDTHH:mm:ss') + '.000Z');
    }
  });

  return prepareResponse;
};

export const createRequestOption = (req?: any): HttpParams => {
  let options: HttpParams = new HttpParams();

  if (req) {
    Object.keys(req).forEach(key => {
      if (key !== 'sort') {
        options = options.set(key, req[key]);
      }
    });

    if (req.sort) {
      req.sort.forEach((val: string) => {
        options = options.append('sort', val);
      });
    }
  }

  return options;
};
