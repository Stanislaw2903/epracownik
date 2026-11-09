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
import { UserService } from 'app/core/user/user.service';
import { User, IUser } from 'app/core/user/user.model';
import { compact, includes, isNil } from 'lodash';

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
  selectedStatus: any = false;

  notificationStatusDict: SelectItem[] = [];

  constructor(
    protected notificationService: NotificationService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected notificationReadService: NotificationReadService,
    private userService: UserService
  ) {
    this.loading = true;
    this.notificationReadUsers = [];
    this.notificationUsers = [];
    this.notificationStatusDict = this.notificationService.getNotificationStatusDict();
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;
    const sortValue = this.sort();
    const sort = dontNavigate || includes(sortValue, 'status,asc') ? compact([this.sort(), 'createDate,asc']) : this.sort();
    const query = Object.assign({}, this.filters, {
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
  }

  customSort(event: LazyLoadEvent): void {
    this.predicate = event.sortField || 'status';
    this.ascending = event?.sortOrder && event.sortOrder > 0 ? true : false;
    setTimeout(() => {
      if (this.loading) {
        event.filters!.status[0].value = false;
      }

      if (event.filters && event.filters?.status && !isNil(event.filters.status[0].value)) {
        this.filters = { 'status.in': event.filters.status[0].value };
      } else {
        delete this.filters['status.in'];
      }

      if (event.filters && event.filters?.recepient && event.filters.recepient[0].value) {
        const recepientIds = event.filters.recepient[0].value.map((user: IUser) => user.id);
        if (recepientIds.length) {
          this.filters = { 'recepientId.in': recepientIds };
        }
      } else {
        delete this.filters['recepientId.in'];
      }

      if (!this.loading) {
        this.loadPage();
      }
    });
  }

  customFilter(): void {
    this.page = 0;
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
    this.userService.query().subscribe((res: HttpResponse<User[]>) => this.onSuccessUserRead(res.body));
  }

  private onSuccessUserRead(users: User[] | null): void {
    this.users = users as any;
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
