import { IPagablePagination, IPagableSort, IPagableResponse } from './../../shared/model/pagable.model';
import { AbsencesDictService } from './../../shared/dictionaries/absences-dict.service';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { IAbsences, IAbsencesDict } from 'app/shared/model/absences.model';
import { AbsencesService } from './absences.service';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { HttpHeaders } from '@angular/common/http';
import { combineLatest, Subscription } from 'rxjs';

import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { isNil, isObject, mapValues, omitBy } from 'lodash';

export interface SelectRelatedUser {
  name: string;
  externalId: string;
}

@Component({
  selector: 'jhi-absences',
  templateUrl: './absences.component.html',
  styleUrls: ['./absences.component.scss'],
})
export class AbsencesComponent implements OnInit, OnDestroy {
  absencesTableData: IAbsences[] = [];
  absencesDict: IAbsencesDict[] = [];
  relatedUsers: SelectRelatedUser[] = [];
  loading = true;

  // Table configuration
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  predicate!: string;
  ascending!: boolean;
  page!: number;
  filters: any = {
    externalIds: null,
  };

  constructor(
    private absencesService: AbsencesService,
    private absencesDictService: AbsencesDictService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager
  ) {
    this.absencesDictService.getAbsencesDict().subscribe((absencesDict: IAbsencesDict[]) => {
      this.absencesDict = absencesDict;
      this.handleNavigation();
    });
  }

  ngOnInit(): void {
    this.registerChangeInQualifications();
    this.absencesService.getUserAbsencesList().subscribe((relatedUsers: any[]) => {
      this.relatedUsers = relatedUsers;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  getAbsenceByCode(absence: IAbsences): string {
    const absencesData = this.absencesDict.find(item => {
      return item.code === absence.code && item.id === absence.id;
    });

    return absencesData?.label || '';
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? 1 + parseInt(page, 10) : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(
          {
            page: pageNumber - 1,
            rows: this.itemsPerPage,
          },
          true
        );
      }
    }).subscribe();
  }

  registerChangeInQualifications(): void {
    this.eventSubscriber = this.eventManager.subscribe('qualificationsListModification', () => this.loadPage());
  }

  loadPage(paginaton?: IPagablePagination, dontNavigate?: boolean): void {
    const pageToLoad: number = paginaton && !isNil(paginaton?.page) ? paginaton.page : this.page || 0;

    this.absencesService
      .getAbsences({
        page: pageToLoad,
        size: paginaton?.rows,
        sort: this.sort(),
        ...this.filters,
      })
      .subscribe((res: any) => {
        return this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
      });
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  customSort(sortData: IPagableSort): void {
    if (sortData.sortField) {
      this.predicate = sortData.sortField;
      this.ascending = sortData.sortOrder === 1;
      this.loadPage({
        page: this.page,
        rows: sortData.rows,
      });
    }
  }

  customFilter(filtersEvent: any): void {
    this.filters = omitBy(
      mapValues(filtersEvent?.filters, val => (isObject(val?.value) && val.value?.withTime ? val.value?.withTime : val?.value)),
      isNil
    );

    this.loadPage({
      page: this.page,
      rows: this.itemsPerPage,
    });
  }

  protected onSuccess(data: IPagableResponse | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = data?.totalElements || 0;
    this.page = page;
    if (navigate) {
      this.router.navigate(['/absences'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.loading = false;
    this.absencesTableData = (data?.content as IAbsences[]) || [];
  }

  filter(filterCallback: any, value: any): void {
    filterCallback(value); // Przekazuje wybraną wartość do mechanizmu filtrowania PrimeNG.
  }
}
