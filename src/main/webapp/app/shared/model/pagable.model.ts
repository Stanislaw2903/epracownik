export interface IPagableResponse {
  content?: any;
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
  last: boolean;
  number: number;
  size: number;
  sort: Sort;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
export interface Pageable {
  sort: Sort;
  offset: number;
  pageNumber: number;
  pageSize: number;
  unpaged: boolean;
  paged: boolean;
}
export interface Sort {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}

export interface IPagablePagination {
  page: number;
  first?: number;
  rows: number;
  pageCount?: number;
}

export interface IPagableSort {
  first: number;
  rows: number;
  sortField: string;
  sortOrder: number;
  filters?: any;
  globalFilter?: null;
}
