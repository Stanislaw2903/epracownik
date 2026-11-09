import { UserVacationService } from './user-vacation.service';
import { Component, OnInit } from '@angular/core';
import { IVacationFilters, IVacationsModel } from 'app/shared/model/vacations.model';
import { CustomMenuItem } from 'app/shared/model/dictionaries.model';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { combineLatest, Subscription } from 'rxjs';
import { IVacationFullDayDict } from '../home.interface';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { VacationStatusDictService } from 'app/shared/dictionaries/vacation-status-dict.service';
import { ToastService } from 'app/shared/toast/toast.service';
import { JhiEventManager } from 'ng-jhipster';
import { HomeService } from '../home.service';
import { OnDestroy } from '@angular/core';
import { IPagablePagination, IPagableResponse, IPagableSort } from 'app/shared/model/pagable.model';
import { HttpHeaders } from '@angular/common/http';
import { isNil, isObject, mapValues, omitBy } from 'lodash';
import { ConfirmationService, MessageService, SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-user-vacation',
  templateUrl: './user-vacation.component.html',
  styleUrls: ['./user-vacation.component.scss'],
  providers: [ConfirmationService, MessageService],
})
export class UserVacationComponent implements OnInit, OnDestroy {
  vacationsData: IVacationsModel[] = [];
  loading = true;
  statuses: CustomMenuItem[] = [];
  fullDayDict: IVacationFullDayDict[] = [];
  vacationTypesDict: SelectItem[] = [];

  // Table configuration
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  predicate!: string;
  ascending!: boolean;
  page!: number;
  filters: any = {};

  constructor(
    private userVacationService: UserVacationService,
    private homeService: HomeService,
    private _toastService: ToastService,
    private vacationStatusDictService: VacationStatusDictService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    private translateService: TranslateService,
    private confirmationService: ConfirmationService
  ) {
    this.handleNavigation();
    this.fullDayDict = this.homeService.getFullDayDict();

    this.vacationStatusDictService.getVacationStatusDict().subscribe((dict: CustomMenuItem[]) => {
      this.statuses = dict;
    });

    this.homeService.getVacationTypes().subscribe((types: any) => {
      this.vacationTypesDict = types.map((type: any) => {
        return { label: this.translateService.instant('ebokApp.vacations.types.' + type), value: type };
      });
    });
  }

  getTranslateVacationType(vacationType: string): string {
    return this.vacationTypesDict.find((type: SelectItem) => type.value === vacationType)?.label ?? '';
  }

  ngOnInit(): void {
    this.registerChangeInQualifications();
  }

  removeApplication(id: number): void {
    this.confirmationService.confirm({
      message: 'Czy na pewno chcesz usunąć wniosek urlopowy?',
      header: 'Potwierdzenie',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.userVacationService.deleteApplication(id).subscribe(() => {
          this._toastService.sendMessage({
            type: 'success',
            title: 'ebokApp.vacations.vacationApplicationResponse.title',
            message: 'ebokApp.vacations.vacationApplicationResponse.removed',
          });
          this.loadPage();
        });
      },
    });
  }

  getFullDayValue(type: boolean): string {
    const fullDayValue = this.fullDayDict.find(day => day.value === type);
    return fullDayValue ? fullDayValue.name : '';
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: any, params: ParamMap) => {
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

    this.userVacationService
      .getVacations({
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

  customFilter(filtersEvent: IVacationFilters): void {
    this.filters = omitBy(
      mapValues(filtersEvent.filters, val => (isObject(val.value) && val.value?.withTime ? val.value?.withTime : val.value)),
      isNil
    );
    this.loadPage();
  }

  protected onSuccess(data: IPagableResponse | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = data?.totalElements || 0;
    this.page = page;
    if (navigate) {
      this.router.navigate(['/dashboard/user-vacation'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.loading = false;
    this.vacationsData = (data?.content as IVacationsModel[]) || [];
  }

  getTranslateStringFullDay(type: any): string {
    return 'entity.detail.fullDay.' + !!type;
  }

  getTranslateStringStatus(type: any): string {
    return 'ebokApp.vacations.statuses.' + type;
  }
}
