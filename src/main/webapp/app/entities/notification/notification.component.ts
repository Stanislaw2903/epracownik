import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INotification } from 'app/shared/model/notification.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { NotificationService } from './notification.service';
import { NotificationDeleteDialogComponent } from './notification-delete-dialog.component';
import { NotificationReadService } from '../notification-read/notification-read.service';
import { LazyLoadEvent, SelectItem } from 'primeng/api';
import { IUser } from 'app/core/user/user.model';
import { TranslateService } from '@ngx-translate/core';
import { AccountService } from 'app/core/auth/account.service';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserGroupService } from 'app/admin/user-group/user-group.service';
import { compact, includes, isNil } from 'lodash';
import * as moment from 'moment';

@Component({
  selector: 'jhi-notification',
  templateUrl: './notification.component.html',
})
export class NotificationComponent implements OnInit, OnDestroy {
  notifications?: INotification[];
  notificationReadUsers: any[];
  notificationUsers: any[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string | undefined;
  ascending!: boolean;
  filters: any = { 'status.in': false };
  ngbPaginationPage = 1;
  loading: boolean;
  users: any[] = [];
  selectedUsers: any[] = [];
  selectedSenders: any[] = [];
  selectedStatus: any = false;

  notificationStatusDict: SelectItem[] = [];
  isHr = false;
  scopeTypeDict: SelectItem[] = [];
  scopeType = 'ME';
  userGroups: any[] = [];
  selectedGroups: number[] = [];

  constructor(
    protected notificationService: NotificationService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected notificationReadService: NotificationReadService,
    private translateService: TranslateService,
    private accountService: AccountService,
    private userGroupService: UserGroupService
  ) {
    this.loading = true;
    this.notificationReadUsers = [];
    this.notificationUsers = [];
    this.notificationStatusDict = this.notificationService.getNotificationStatusDict();
    this.isHr = this.accountService.hasAnyAuthority(Authority.HR);
    if (this.isHr) {
      this.scopeTypeDict = [
        { label: this.translateService.instant('ebokApp.graphic.filters.showAll'), value: 'ALL' },
        { label: this.translateService.instant('ebokApp.graphic.filters.showMe'), value: 'ME' },
        { label: this.translateService.instant('ebokApp.graphic.filters.showGroup'), value: 'GROUP' },
      ];
    }
  }

  static buildFilters(tableFilters: any): any {
    const filters: any = {};
    const value = (field: string): any => tableFilters?.[field]?.[0]?.value;

    if (!isNil(value('status'))) {
      filters['status.in'] = value('status');
    }

    const subject = value('subject');
    if (subject && String(subject).trim()) {
      filters['subject.contains'] = String(subject).trim();
    }

    const dates = value('createDate');
    if (Array.isArray(dates) && dates[0]) {
      filters['createDate.greaterThanOrEqual'] = moment(dates[0]).startOf('day').toISOString();
      filters['createDate.lessThanOrEqual'] = moment(dates[1] || dates[0])
        .endOf('day')
        .toISOString();
    }

    const senderIds = (value('createdBy') || []).map((user: IUser) => user.id);
    if (senderIds.length) {
      filters['createdById.in'] = senderIds;
    }

    const recipientIds = (value('recepient') || []).map((user: IUser) => user.id);
    if (recipientIds.length) {
      filters['recepientId.in'] = recipientIds;
    }

    return filters;
  }

  isGroupMode(): boolean {
    return this.isHr && this.scopeType === 'GROUP';
  }

  scopeParams(): any {
    if (this.scopeType === 'ALL' && this.isHr) {
      return { all: true };
    }
    if (this.isGroupMode()) {
      return { groups: this.selectedGroups };
    }
    return {};
  }

  onScopeChange(): void {
    if (!this.isGroupMode()) {
      this.selectedGroups = [];
    }
    this.page = 1;
    this.getUsers();
    this.loadPage(1);
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;
    if (this.isGroupMode() && !this.selectedGroups.length) {
      this.notifications = [];
      this.totalItems = 0;
      this.loading = false;
      return;
    }
    const sortValue = this.sort();
    const sort = dontNavigate || includes(sortValue, 'status,asc') ? compact([this.sort(), 'createDate,asc']) : this.sort();
    const query = Object.assign({}, this.filters, this.scopeParams(), {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort,
    });

    this.notificationService.query(query).subscribe(
      (res: HttpResponse<INotification[]>) => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
      () => this.onError()
    );
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInNotifications();
    this.getUsers();
    if (this.isHr) {
      this.userGroupService.getUserGroups({ size: 5000 }).subscribe(result => (this.userGroups = result.content || []));
    }
  }

  customSort(event: LazyLoadEvent): void {
    this.predicate = event.sortField || 'status';
    this.ascending = event?.sortOrder && event.sortOrder > 0 ? true : false;
    setTimeout(() => {
      if (this.loading && event.filters?.status?.[0]) {
        event.filters.status[0].value = false;
      }

      this.filters = NotificationComponent.buildFilters(event.filters);

      if (!this.loading) {
        this.loadPage();
      }
    });
  }

  customFilter(): void {
    this.page = 1;
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
        this.loadPage(pageNumber, true);
      }
    }).subscribe();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: INotification): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInNotifications(): void {
    this.eventSubscriber = this.eventManager.subscribe('notificationListModification', () => this.loadPage());
  }

  delete(notification: INotification): void {
    const modalRef = this.modalService.open(NotificationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.notification = notification;
  }

  getUsers(): void {
    if (this.isGroupMode() && !this.selectedGroups.length) {
      this.users = [];
      return;
    }
    this.notificationService.scopeUsers(this.scopeParams()).subscribe((res: HttpResponse<IUser[]>) => this.onSuccessUserRead(res.body));
  }

  private onSuccessUserRead(users: IUser[] | null): void {
    this.users = (users || []).map(user => ({
      ...user,
      label: `${user.lastName ?? ''} ${user.firstName ?? ''} (${user.login})`.trim(),
    }));
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    return result;
  }

  protected onSuccess(data: INotification[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/notification'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: [this.predicate + ',' + (this.ascending ? 'asc' : 'desc'), 'createDate,asc'],
        },
      });
    }
    this.notifications = data || [];
    this.ngbPaginationPage = this.page;
    this.loading = false;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  getReadUsers(id: number): void {
    this.notificationReadService.findByNotificationId(id).subscribe(
      (res: HttpResponse<any[]>) => {
        this.notificationReadUsers = res.body as any;
      },
      () => this.onError()
    );
  }

  getNotificationUsers(id: number): void {
    this.notificationService.find(id).subscribe(
      (res: HttpResponse<any>) => {
        this.notificationUsers = res.body.recepients;
      },
      () => this.onError()
    );
  }
}
