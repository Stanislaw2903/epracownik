import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AccountService } from 'app/core/auth/account.service';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserGroupService } from 'app/admin/user-group/user-group.service';
import { IGraphicDayCell, IGraphicDayColumn, IGraphicTableItem } from 'app/shared/model/graphic.model';
import { IPagablePagination, IPagableResponse } from 'app/shared/model/pagable.model';
import { isNil } from 'lodash';
import * as moment from 'moment';
import { JhiEventManager } from 'ng-jhipster';
import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
import { combineLatest, Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { GraphicService } from './graphic.service';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'jhi-graphic',
  templateUrl: './graphic.component.html',
  styleUrls: ['./graphic.component.scss'],
})
export class GraphicComponent implements OnInit, OnDestroy {
  loading = true;
  inited = false;
  isActive = true;
  tableData: IGraphicTableItem[] = [];
  dayColumns: IGraphicDayColumn[] = [];
  resultsLoaded = false;
  selectGraphicTypeDict: any;
  isHr = false;
  userGroups: any[] = [];
  private lastNameChanges = new Subject<string>();
  private lastNameSubscription?: Subscription;

  // Table config
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = 50;
  predicate!: string;
  ascending!: boolean;
  page!: number;
  filters: any = {
    graphicType: 'ME',
    year: moment().year(),
    month: moment().month() + 1,
    groups: [],
    lastName: '',
  };

  configFiled: any = {
    minMonthNumber: 1,
    maxMonthNumber: 12,
  };
  constructor(
    private graphicService: GraphicService,
    private translateService: TranslateService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    private accountService: AccountService,
    private userGroupService: UserGroupService
  ) {
    this.isHr = this.accountService.hasAnyAuthority(Authority.HR);
    const showMe = { label: this.translateService.instant('ebokApp.graphic.filters.showMe'), value: 'ME' };
    this.selectGraphicTypeDict = this.isHr
      ? [
          { label: this.translateService.instant('ebokApp.graphic.filters.showAll'), value: 'ALL' },
          showMe,
          { label: this.translateService.instant('ebokApp.graphic.filters.showGroup'), value: 'GROUP' },
        ]
      : [showMe];
    this.filters.graphicType = showMe;
    this.handleNavigation();
  }

  ngOnInit(): void {
    this.registerChanges();
    this.lastNameSubscription = this.lastNameChanges.pipe(debounceTime(400), distinctUntilChanged()).subscribe(() => {
      this.page = 0;
      if (this.resultsLoaded) {
        this.loadPage();
      }
    });
    if (this.isHr) {
      this.userGroupService.getUserGroups({ size: 5000 }).subscribe(result => (this.userGroups = result.content || []));
    }
  }

  onFilterChange(): void {
    this.resetResults();
  }

  onLastNameChange(value: string): void {
    this.filters.lastName = value;
    this.lastNameChanges.next(value);
  }

  changeFilterYear(): void {
    this.configFiled = {
      minMonthNumber: 1,
      maxMonthNumber: 12,
    };
    this.resetResults();
  }

  onMonthChange(): void {
    this.resetResults();
  }

  private resetResults(): void {
    this.resultsLoaded = false;
    this.tableData = [];
    this.dayColumns = [];
    this.totalItems = 0;
    this.page = 0;
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
    this.lastNameSubscription?.unsubscribe();
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      if (this.inited === false) {
        this.inited = true;
        const page = params.get('page');
        const pageNumber = page !== null ? 1 + parseInt(page, 10) : 1;
        const sort = (params.get('sort') ?? data['defaultSort']).split(',');
        this.predicate = sort[0];
        this.ascending = sort[1] === 'asc';
        this.page = pageNumber - 1;
        this.loading = false;
      }
    }).subscribe();
  }

  registerChanges(): void {
    this.eventSubscriber = this.eventManager.subscribe('qualificationsListModification', () => this.loadPage());
  }

  onGraphicTypeChange(): void {
    if (!this.isGroupMode()) {
      this.filters.groups = [];
    }
    this.resetResults();
  }

  isGroupMode(): boolean {
    return this.isHr && this.filters.graphicType?.value === 'GROUP';
  }

  canLoad(): boolean {
    return !this.isGroupMode() || this.filters.groups?.length > 0;
  }

  loadPage(paginaton?: IPagablePagination, dontNavigate?: boolean): void {
    if (!this.canLoad()) {
      return;
    }
    const pageToLoad: number = paginaton && !isNil(paginaton?.page) ? paginaton.page : this.page || 0;
    if (paginaton?.rows) {
      this.itemsPerPage = paginaton.rows;
    }
    const payload = this.buildPayload(pageToLoad);

    this.dayColumns = this.getMonthWorkingDays();
    this.resultsLoaded = true;

    this.graphicService.getData(payload).subscribe((res: any) => {
      return this.onSuccess(res.body, pageToLoad, !dontNavigate);
    });
  }

  buildPayload(page: number): any {
    const payload: any = {
      page,
      size: this.itemsPerPage,
      sort: this.sort(),
      month: this.filters.month,
      year: this.filters.year,
    };
    if (this.filters.lastName?.trim()) {
      payload.lastName = this.filters.lastName.trim();
    }
    if (!this.isHr) {
      return payload;
    }
    switch (this.filters.graphicType?.value) {
      case 'ALL':
        payload.all = true;
        break;
      case 'GROUP':
        payload.groups = this.filters.groups;
        break;
    }
    return payload;
  }

  getMonthWorkingDays(): IGraphicDayColumn[] {
    const date = moment()
      .year(this.filters.year)
      .month(this.filters.month - 1)
      .startOf('month');
    const end = moment(date).endOf('month');
    const result: IGraphicDayColumn[] = [];

    while (date.isSameOrBefore(end, 'day')) {
      result.push({ key: date.format('YYYY-MM-DD'), label: date.format('DD') });
      date.add(1, 'day');
    }

    return result;
  }

  cell(item: IGraphicTableItem, key: string): IGraphicDayCell {
    return (item.days && item.days[key]) || { type: 'WORKING' };
  }

  cellClass(item: IGraphicTableItem, key: string): string {
    const c = this.cell(item, key);
    return c.type === 'ABSENCE' ? 'abs-' + (c.category || 'D') : c.type;
  }

  cellText(item: IGraphicTableItem, key: string): string {
    const c = this.cell(item, key);
    return c.type === 'ABSENCE' ? c.absKod || '' : '';
  }

  cellTooltip(item: IGraphicTableItem, key: string): string {
    const c = this.cell(item, key);
    return c.type === 'ABSENCE' && c.code ? this.translateService.instant('ebokApp.graphic.absence.' + c.code) : '';
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: IPagableResponse | null, page: number, navigate: boolean): void {
    this.totalItems = data?.totalElements || 0;
    this.page = page;
    if (navigate) {
      this.router.navigate(['/admin/graphic'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.loading = false;
    this.tableData = (data?.content as IGraphicTableItem[]) || [];
  }

  exportPdf(): void {
    const columns = [this.translateService.instant('ebokApp.graphic.table.username'), ...this.dayColumns.map(col => col.label)];

    const data = this.tableData.map(item => {
      return [
        (item.firstName ?? '') + ' ' + (item.lastName ?? ''),
        ...this.dayColumns.map(col => this.prepareCellData(this.cell(item, col.key))),
      ];
    });

    const columnWidth = ['*', ...new Array(this.dayColumns.length).fill('auto')];

    const documentDefinition = {
      pageOrientation: 'landscape',
      pageMargins: [20, 60, 20, 60],
      header: [
        { text: `Grafik absencji ${this.periodLabel()}`, alignment: 'center', margin: [0, 20, 0, 8] },
        { text: `Wygenerowano dnia ${moment().format('DD MMM YYYY HH:mm:ss')}`, alignment: 'center', fontSize: 7 },
      ],
      content: [
        {
          layout: 'lightHorizontalLines',
          fontSize: 6,
          table: {
            headerRows: 1,
            widths: columnWidth,
            body: [columns, ...data],
          },
        },
      ],
      footer: this.footerPdf,
    };
    pdfMake.createPdf(documentDefinition as any).download(`grafik-absencji-${this.periodLabel()}.pdf`);
  }

  periodLabel(): string {
    return moment()
      .year(this.filters.year)
      .month(this.filters.month - 1)
      .format('MM.YYYY');
  }

  prepareCellData(cell: IGraphicDayCell): any {
    const shade: { [k: string]: string } = { A: '#eff86d', B: '#ffd27f', C: '#ffb74d', D: '#f5e9a0' };
    if (cell.type === 'ABSENCE') {
      return { text: cell.absKod || '', alignment: 'center', fillColor: shade[cell.category || 'D'] || '#eff86d' };
    }
    return {
      text: '',
      fillColor: cell.type === 'HOLIDAY' ? '#66e98d' : cell.type === 'WEEKEND' ? '#ff8d8d' : null,
    };
  }

  footerPdf(currentPage: any, pageCount: any): any {
    return { text: `Strona ${currentPage.toString()} z ${pageCount}`, alignment: 'center', margin: [0, 20, 0, 8], fontSize: 7 };
  }

  exportExcel(): void {
    const columns = [this.translateService.instant('ebokApp.graphic.table.username'), ...this.dayColumns.map(col => col.label)];
    const rows = this.tableData.map(item => [
      (item.firstName ?? '') + ' ' + (item.lastName ?? ''),
      ...this.dayColumns.map(col => {
        const c = this.cell(item, col.key);
        if (c.type === 'ABSENCE') {
          return c.absKod || '';
        }
        return c.type === 'WORKING' ? '' : this.translateService.instant('ebokApp.graphic.type.' + c.type);
      }),
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
    link.download = `grafik-absencji-${this.periodLabel()}.xls`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
