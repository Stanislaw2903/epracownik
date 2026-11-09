import { VacationStatusDictService } from './../../shared/dictionaries/vacation-status-dict.service';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { IVacationFullDayDict } from 'app/home/home.interface';
import { HomeService } from 'app/home/home.service';
import { IVacationFilters, IVacationsModel, IVacationUpdate } from 'app/shared/model/vacations.model';
import { ToastService } from 'app/shared/toast/toast.service';
import * as moment from 'moment';
import { Moment } from 'moment';
import { FilterService, SelectItem } from 'primeng/api';
import { VacationsService } from './vacations.service';
import { CustomMenuItem } from 'app/shared/model/dictionaries.model';
import { ActivatedRoute, Router, Data, ParamMap } from '@angular/router';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { IPagablePagination, IPagableResponse, IPagableSort } from 'app/shared/model/pagable.model';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription, combineLatest } from 'rxjs';
import { isNil, isNumber, isObject, mapValues, omitBy, values } from 'lodash';
import { IUser } from 'app/core/user/user.model';
import { TranslateService } from '@ngx-translate/core';
import { SessionStorageService } from 'ngx-webstorage';
import { AccountService } from 'app/core/auth/account.service';
import { Authority } from 'app/shared/constants/authority.constants';

import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'jhi-vacations',
  templateUrl: './vacations.component.html',
  styleUrls: ['./vacations.component.scss'],
})
export class VacationsComponent implements OnInit, OnDestroy {
  vacationsData: IVacationsModel[] = [];
  loading = true;
  isAcceptOrRejectModalVisible = false;
  isRejectModalVisible = false;
  isAcceptModalVisible = false;
  isCancelModalVisible = false;
  statuses: CustomMenuItem[] = [];
  selectedStatuses: any[] = [];
  fullDayDict: IVacationFullDayDict[] = [];
  usersDict: SelectItem[] = [];
  tempStatus?: string;
  vacationApproveLevel = '';
  userApprove?: string;
  applicantBalance?: number;
  isHr = false;
  isApplicantHistoryVisible = false;
  applicantHistory: any[] = [];
  applicantHistoryYear: Date = moment().toDate();
  editApplication: IVacationUpdate = {
    id: 0,
    status: '',
  };
  vacationTypesDict: SelectItem[] = [];

  // Table configuration
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  predicate!: string;
  ascending!: boolean;
  page!: number;
  first = 0;
  filters: any = {};

  constructor(
    private $sessionStorage: SessionStorageService,
    private vacationsService: VacationsService,
    private homeService: HomeService,
    private _toastService: ToastService,
    private filterService: FilterService,
    private vacationStatusDictService: VacationStatusDictService,
    private translateService: TranslateService,
    private accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager
  ) {
    this.isHr = this.accountService.hasAnyAuthority(Authority.HR);
    this.vacationStatusDictService.getVacationDefaultStatusDict().subscribe((value: string) => {
      if (value) {
        this.filters.status = value;
        this.selectedStatuses = value.split(',');
      }

      this.handleNavigation();
    });

    this.vacationApproveLevel = this.$sessionStorage.retrieve('vacationApproveLevel');

    this.vacationsService.getUserApproval().subscribe((value: string) => {
      this.userApprove = value;
      console.log(this.userApprove === 'FINAL_VERIFICATION');
      console.log(this.userApprove);
    });

    this.fullDayDict = this.homeService.getFullDayDict();

    this.vacationStatusDictService.getVacationStatusDict().subscribe((dict: CustomMenuItem[]) => {
      this.statuses = dict;
    });

    this.vacationsService.getUsersDict(true).subscribe((dict: any) => {
      const userDict = dict.body ? dict.body : dict;
      this.usersDict = userDict
        .map((user: IUser) => {
          return { label: `${user.lastName} ${user.firstName}`, value: user.login };
        })
        .sort((a: SelectItem, b: SelectItem) => (a.label ?? '').localeCompare(b.label ?? ''));
    });

    this.homeService.getVacationTypes().subscribe((types: any) => {
      this.vacationTypesDict = types.map((type: any) => {
        return { label: this.translateService.instant('ebokApp.vacations.types.' + type), value: type };
      });
    });
  }

  ngOnInit(): void {
    this.filterService.register('only-same-date', (value: Date | Moment, filter: Date | string): boolean => {
      if (filter === undefined || filter === null || filter === '') {
        return true;
      }

      if (value === undefined || value === null) {
        return false;
      }

      return moment(value).startOf('day').isSame(moment(filter).startOf('day'));
    });

    this.filterService.register('date-same-or-after', (value: Date | Moment, filter: any): boolean => {
      if (filter === undefined || filter === null || filter === '') {
        return true;
      }

      if (value === undefined || value === null) {
        return false;
      }

      if (filter.withTime) {
        return moment(value).isSameOrAfter(moment(filter.withTime));
      } else {
        return moment(value).startOf('day').isSameOrAfter(moment(filter).startOf('day'));
      }
    });

    this.filterService.register('date-same-or-before', (value: Date | Moment, filter: any): boolean => {
      if (filter === undefined || filter === null || filter === '') {
        return true;
      }

      if (value === undefined || value === null) {
        return false;
      }

      if (filter.withTime) {
        return moment(value).isSameOrBefore(moment(filter.withTime));
      } else {
        return moment(value).startOf('day').isSameOrBefore(moment(filter).startOf('day'));
      }
    });

    this.registerChangeInQualifications();
  }

  getTranslateVacationType(vacationType: string): string {
    return this.vacationTypesDict.find((type: SelectItem) => type.value === vacationType)?.label ?? '';
  }

  getFullDayValue(type: boolean): string {
    const fullDayValue = this.fullDayDict.find(day => day.value === type);
    return fullDayValue ? fullDayValue.name : '';
  }

  openAcceptOrRejectModal(application: IVacationsModel): void {
    this.editApplication = { ...application };
    this.isAcceptOrRejectModalVisible = true;
    this.tempStatus = application.status;
    this.applicantBalance = undefined;
    const externalId = application.createdBy?.externalId;
    if (externalId) {
      this.vacationsService.getVacationBalance(externalId).subscribe(balance => {
        this.applicantBalance = balance?.vested;
      });
    }
  }

  isNotEnoughDaysForApplicant(): boolean {
    const limited = this.editApplication.vacationType === 'ANNUAL_LEAVE' || this.editApplication.vacationType === 'LEAVE_ON_REQUEST';
    if (!limited || this.applicantBalance === undefined) {
      return false;
    }
    const requested = Number(this.editApplication.used ?? 0);
    return requested > this.applicantBalance;
  }

  openApplicantHistory(): void {
    this.applicantHistoryYear = moment().toDate();
    this.isApplicantHistoryVisible = true;
    this.loadApplicantHistory();
  }

  loadApplicantHistory(): void {
    const externalId = this.editApplication.createdBy?.externalId;
    if (!externalId) {
      this.applicantHistory = [];
      return;
    }
    this.vacationsService.getVacationHistoryDetails(externalId, moment(this.applicantHistoryYear).format('YYYY')).subscribe(data => {
      this.applicantHistory = data || [];
    });
  }

  changeApplicantHistoryYear(previousYear = false): void {
    this.applicantHistoryYear = previousYear ? moment(this.applicantHistoryYear).subtract(1, 'year').toDate() : moment().toDate();
    this.loadApplicantHistory();
  }

  sendRejectApplication(): void {
    this.isRejectModalVisible = false;
    this.sendApplication();
  }

  sendAcceptApplication(): void {
    this.isAcceptModalVisible = false;
    this.sendApplication();
  }

  sendCancelApplication(): void {
    this.isCancelModalVisible = false;
    this.sendApplication();
  }

  sendVacationApplication(status: string): void {
    this.editApplication.status = status;

    if (status === 'REJECTED') {
      this.isRejectModalVisible = true;
      return;
    }

    if (status === 'ACCEPTED') {
      this.isAcceptModalVisible = true;
      return;
    }

    if (status === 'CANCELLED') {
      this.isCancelModalVisible = true;
      return;
    }

    this.sendApplication();
  }

  sendApplication(): void {
    this.vacationsService.updateApplicationStatus(this.editApplication).subscribe(
      () => {
        this.editApplication = {
          id: 0,
          comment: '',
          status: '',
        };

        this.isAcceptOrRejectModalVisible = false;

        this._toastService.sendMessage({
          type: 'success',
          title: 'ebokApp.vacations.vacationApplicationResponse.title',
          message: 'ebokApp.vacations.vacationApplicationResponse.success',
        });
        this.loadPage();
      },
      () => {
        this.editApplication.status = this.tempStatus;
        this._toastService.sendMessage({
          type: 'error',
          title: 'ebokApp.vacations.vacationApplicationResponse.title',
          message: 'ebokApp.vacations.vacationApplicationResponse.error',
        });
      }
    );
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
      console.log(this.filters);
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(
          {
            page: pageNumber - 1,
            rows: this.itemsPerPage,
            sort: this.sort(),
            ...this.filters,
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

    this.vacationsService
      .getVacations({
        page: pageToLoad,
        size: paginaton?.rows || this.itemsPerPage,
        sort: this.sort(),
        ...this.filters,
      })
      .subscribe((res: any) => {
        return this.onSuccess(res.body, pageToLoad, !dontNavigate);
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
    if (this.loading) {
      setTimeout(() => {
        sortData.filters.status.value = 'WAITING';
      });
    }
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
    // Zmiana filtra resetuje stronę - inaczej zostaje stary page (np. 8) i przy mniejszym wyniku grid jest pusty.
    this.page = 0;
    this.first = 0;
    this.loadPage({ page: 0, rows: this.itemsPerPage });
  }

  protected onSuccess(data: IPagableResponse | null, page: number, navigate: boolean): void {
    this.totalItems = data?.totalElements || 0;
    this.page = page;
    this.first = page * this.itemsPerPage;
    if (navigate) {
      this.router.navigate(['/vacations'], {
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
  exportPdf(): void {
    const columns = values(
      this.translateService.instant([
        'ebokApp.vacations.table.id',
        'ebokApp.vacations.table.createdBy',
        'ebokApp.vacations.table.sendDate',
        'ebokApp.vacations.table.status',
        'ebokApp.vacations.table.used',
        'ebokApp.vacations.table.dateFrom',
        'ebokApp.vacations.table.dateTo',
        'ebokApp.vacations.table.processedBy',
        'ebokApp.vacations.table.fullDay',
        'ebokApp.vacations.table.comment',
      ])
    );

    const filters = [
      '',
      this.filters.createdBy ? this.getDropdownValue(this.usersDict, this.filters.createdBy, 'value', 'label') : '',
      this.filters.createdDate ? moment(this.filters.createdDate).format('DD-MM-YYYY') : '',
      this.filters.status ? this.getDropdownValue(this.statuses, this.filters.status, 'value', 'label') : '',
      '',
      this.filters.dateFrom ? moment(this.filters.dateFrom).format('DD-MM-YYYY') : '',
      this.filters.dateTo ? moment(this.filters.dateTo).format('DD-MM-YYYY') : '',
      this.filters.processedBy ? this.getDropdownValue(this.usersDict, this.filters.processedBy, 'value', 'label') : '',
      !isNil(this.filters.fullDay) ? this.getDropdownValue(this.usersDict, this.filters.fullDay, 'value', 'label') : '',
      '',
    ];

    const data = this.vacationsData.map(vacation => {
      return [
        vacation.id,
        `${vacation.createdBy?.firstName || ''} ${vacation.createdBy?.lastName || ''}`,
        moment(vacation.createdDate).format('DD MMM YYYY'),
        this.translateService.instant(this.getTranslateStringStatus(vacation.status)),
        isNumber(vacation.used) ? vacation.used : '',
        vacation.fullDay ? moment(vacation.dateFrom).format('DD MMM YYYY') : moment(vacation.dateFrom).format('DD MMM YYYY HH:mm:ss'),
        vacation.fullDay ? moment(vacation.dateTo).format('DD MMM YYYY') : moment(vacation.dateTo).format('DD MMM YYYY HH:mm:ss'),
        `${vacation.processedBy?.firstName || ''} ${vacation.processedBy?.lastName || ''}`,
        this.translateService.instant(this.getTranslateStringFullDay(vacation.fullDay)),
        vacation.comment,
      ];
    });

    const documentDefinition = {
      pageMargins: [20, 60, 20, 60],
      header: [
        { text: `Wnioski urlopowe`, alignment: 'center', margin: [0, 20, 0, 8] },
        { text: `Wygenerowano dnia ${moment().format('DD MMM YYYY HH:mm:ss')}`, alignment: 'center', fontSize: 7 },
      ],
      content: [
        {
          layout: 'lightHorizontalLines',
          fontSize: 8,
          table: {
            headerRows: 2,
            widths: new Array(10).fill('auto'),
            body: [columns, filters, ...data],
          },
        },
      ],
      footer: this.footerPdf,
    };
    pdfMake.createPdf(documentDefinition as any).download('wnioski urlopowe.pdf');
  }

  footerPdf(currentPage: any, pageCount: any): any {
    return { text: `Strona ${currentPage.toString()} z ${pageCount}`, alignment: 'center', margin: [0, 20, 0, 8], fontSize: 7 };
  }

  exportExcel(): void {
    const columns = values(
      this.translateService.instant([
        'ebokApp.vacations.table.id',
        'ebokApp.vacations.table.createdBy',
        'ebokApp.vacations.table.sendDate',
        'ebokApp.vacations.table.status',
        'ebokApp.vacations.table.used',
        'ebokApp.vacations.table.dateFrom',
        'ebokApp.vacations.table.dateTo',
        'ebokApp.vacations.table.processedBy',
        'ebokApp.vacations.table.fullDay',
        'ebokApp.vacations.table.vacationType',
      ])
    );

    const rows = this.vacationsData.map(vacation => [
      vacation.id,
      `${vacation.createdBy?.firstName || ''} ${vacation.createdBy?.lastName || ''}`,
      moment(vacation.createdDate).format('DD-MM-YYYY'),
      this.translateService.instant(this.getTranslateStringStatus(vacation.status)),
      isNumber(vacation.used) ? vacation.used : '',
      vacation.fullDay ? moment(vacation.dateFrom).format('DD-MM-YYYY') : moment(vacation.dateFrom).format('DD-MM-YYYY HH:mm'),
      vacation.fullDay ? moment(vacation.dateTo).format('DD-MM-YYYY') : moment(vacation.dateTo).format('DD-MM-YYYY HH:mm'),
      `${vacation.processedBy?.firstName || ''} ${vacation.processedBy?.lastName || ''}`,
      this.translateService.instant(this.getTranslateStringFullDay(vacation.fullDay)),
      this.getTranslateVacationType(vacation.vacationType ?? ''),
    ]);

    const escape = (val: any): string =>
      String(val ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');

    const headerHtml = `<tr>${columns.map(col => `<th>${escape(col)}</th>`).join('')}</tr>`;
    const bodyHtml = rows.map(row => `<tr>${row.map(cell => `<td>${escape(cell)}</td>`).join('')}</tr>`).join('');
    const table = `<table border="1"><thead>${headerHtml}</thead><tbody>${bodyHtml}</tbody></table>`;
    const html = `<html xmlns:x="urn:schemas-microsoft-com:office:excel"><head><meta charset="UTF-8"></head><body>${table}</body></html>`;

    const blob = new Blob(['﻿', html], { type: 'application/vnd.ms-excel' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'wnioski urlopowe.xls';
    link.click();
    URL.revokeObjectURL(url);
  }

  getDropdownValue(dict: any, value: any, key: string, wantedValue: string): string {
    const getDictItem = dict.find((dictValue: any) => dictValue[key] === value);

    return getDictItem ? getDictItem[wantedValue] : '';
  }

  isCommentTooLong(): boolean {
    return (this.editApplication?.statusDescription?.length ?? 0) >= 255;
  }

  onDescriptionChange(): void {
    if (this.editApplication?.statusDescription) {
      this.editApplication.statusDescription = this.editApplication.statusDescription.slice(0, 255);
    }
  }
}
