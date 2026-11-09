import { ISort } from './sort.model';

export interface IPageable {
  sort: ISort;
  offset: number;
  pageNumber: number;
  pageSize: number;
  unpaged: boolean;
  paged: boolean;
}
