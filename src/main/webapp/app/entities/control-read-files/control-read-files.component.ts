import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { isNil, isObject, mapValues, omitBy } from 'lodash';
import { JhiEventManager } from 'ng-jhipster';
import { combineLatest, Subscription } from 'rxjs';
import { IControlReadFiles } from '../../shared/model/control-read-files';
import { IPagablePagination, IPagableResponse, IPagableSort } from './../../shared/model/pagable.model';
import { ControlReadFilesService } from './control-read-files.service';

export interface SelectRelatedUser {
  name: string;
  userId: string;
}

@Component({
  selector: 'jhi-control-read-files',
  templateUrl: 'control-read-files.component.html',
  styleUrls: ['./control-read-files.component.scss'],
})
export class ControlReadFilesComponent implements OnInit, OnDestroy {
  controlReadFilesTableData: IControlReadFiles[] = [];
  users: IUser[] = [];
  loading = true;
  fileTypesDict = [];
  fileStatusDict = [
    {
      label: 'Tylko zapoznane',
      value: true,
    },
    {
      label: 'Tylko nie zapoznane',
      value: false,
    },
  ];

  // Table configuration
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  predicate!: string;
  ascending!: boolean;
  page!: number;
  filters: any = {
    userIds: null,
  };
  multiselectPlaceholder = '';

  constructor(
    private controlReadFilesService: ControlReadFilesService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected userService: UserService
  ) {
    this.handleNavigation();
  }

  ngOnInit(): void {
    const payload: any = {
      page: 0,
      size: 5000,
      sort: ['lastName,asc'],
    };

    this.registerChangeInQualifications();
    this.userService.queryActive(payload).subscribe((res: HttpResponse<IUser[]>) => {
      this.users = res.body || [];

      // THIS IS WORKAROUND
      // https://github.com/primefaces/primeng/issues/9673
      this.multiselectPlaceholder = 'Select';
      setTimeout(() => {
        this.multiselectPlaceholder = '';
      });
      // END WORKAROUND
    });

    this.controlReadFilesService.getFileTypes().subscribe(result => {
      this.fileTypesDict = result;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
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

    this.controlReadFilesService
      .getTableData({
        page: pageToLoad,
        size: paginaton?.rows || this.itemsPerPage,
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
      this.router.navigate(['/control-read-files'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.loading = false;
    this.controlReadFilesTableData = (data?.content as IControlReadFiles[]) || [];
  }

  filter(filterCallback: any, value: any): void {
    filterCallback(value); // Przekazuje wybraną wartość do mechanizmu filtrowania PrimeNG.
  }

  getYear(controlReadFileItem: IControlReadFiles): string | null {
    const match = controlReadFileItem.fileName?.match(/_(\d{4})_/);
    return match ? match[1] : null;
  }

  getStatus(controlReadFileItem: IControlReadFiles): string {
    if (controlReadFileItem.confirmed) {
      return 'ebokApp.controlReadFiles.status.read';
    } else {
      return 'ebokApp.controlReadFiles.status.notRead';
    }
  }
}
