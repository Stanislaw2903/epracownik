import { ICourses } from './../../../shared/model/courses.model';
import { CoursesService } from './courses.service';
import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, Data, ParamMap } from '@angular/router';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { IPagablePagination, IPagableResponse, IPagableSort } from 'app/shared/model/pagable.model';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription, combineLatest } from 'rxjs';
import { isNil } from 'lodash';

@Component({
  selector: 'jhi-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.scss'],
})
export class CoursesComponent implements OnInit, OnDestroy {
  coursesTableData: ICourses[] = [];
  loading = true;

  // Table configuration
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  predicate!: string;
  ascending!: boolean;
  page!: number;

  constructor(
    private coursesService: CoursesService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager
  ) {
    this.handleNavigation();
  }

  ngOnInit(): void {
    this.registerChangeInQualifications();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
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

    this.coursesService
      .getCourses({
        page: pageToLoad,
        size: paginaton?.rows,
        sort: this.sort(),
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

  protected onSuccess(data: IPagableResponse | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = data?.totalElements || 0;
    this.page = page;
    if (navigate) {
      this.router.navigate(['/personal-data/courses'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.loading = false;
    this.coursesTableData = (data?.content as ICourses[]) || [];
  }
}
